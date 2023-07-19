/*******************************************************************************
 * Copyright (c) 2014, 2015 Raymond Augé and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 ******************************************************************************/

package com.liferay.portal.osgi.web.http.servlet.internal;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.DispatchTargets;
import com.liferay.portal.osgi.web.http.servlet.internal.context.HttpContextHelperFactory;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ProxyContext;
import com.liferay.portal.osgi.web.http.servlet.internal.error.HttpWhiteboardFailureException;
import com.liferay.portal.osgi.web.http.servlet.internal.error.IllegalContextNameException;
import com.liferay.portal.osgi.web.http.servlet.internal.error.IllegalContextPathException;
import com.liferay.portal.osgi.web.http.servlet.internal.error.PatternInUseException;
import com.liferay.portal.osgi.web.http.servlet.internal.error.ServletAlreadyRegisteredException;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.Match;
import com.liferay.portal.osgi.web.http.servlet.internal.util.Const;
import com.liferay.portal.osgi.web.http.servlet.internal.util.Path;
import com.liferay.portal.osgi.web.http.servlet.internal.util.ServiceProperties;
import com.liferay.portal.osgi.web.http.servlet.internal.util.StringPlus;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.dto.DTO;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.HttpServiceRuntimeConstants;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.ErrorPageDTO;
import org.osgi.service.http.runtime.dto.FailedServletContextDTO;
import org.osgi.service.http.runtime.dto.FilterDTO;
import org.osgi.service.http.runtime.dto.ListenerDTO;
import org.osgi.service.http.runtime.dto.RequestInfoDTO;
import org.osgi.service.http.runtime.dto.ResourceDTO;
import org.osgi.service.http.runtime.dto.ServletDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Raymond Augé
 */
public class HttpServiceRuntimeController
	implements ServiceTrackerCustomizer
		<ServletContextHelper, AtomicReference<ContextController>> {

	public HttpServiceRuntimeController(
		BundleContext trackingBundleContext,
		BundleContext consumingBundleContext,
		ServletContext parentServletContext, String targetFilter,
		Map<String, Object> attributesMap) {

		_trackingBundleContext = trackingBundleContext;
		_consumingBundleContext = consumingBundleContext;
		_parentServletContext = parentServletContext;
		_targetFilter = targetFilter;
		_attributesMap = attributesMap;

		_contextServiceTracker = new ServiceTracker<>(
			trackingBundleContext, ServletContextHelper.class, this);

		_contextServiceTracker.open();
	}

	@Override
	public synchronized AtomicReference<ContextController> addingService(
		ServiceReference<ServletContextHelper> serviceReference) {

		AtomicReference<ContextController> result = new AtomicReference<>();

		if (!matches(serviceReference)) {
			return result;
		}

		String contextName = (String)serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME);
		String contextPath = (String)serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH);

		try {
			if (contextName == null) {
				throw new IllegalContextNameException(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +
						" is null. Ignoring!",
					DTOConstants.FAILURE_REASON_VALIDATION_FAILED);
			}

			if (contextPath == null) {
				throw new IllegalContextPathException(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH +
						" is null. Ignoring!",
					DTOConstants.FAILURE_REASON_VALIDATION_FAILED);
			}

			ContextController contextController = new ContextController(
				_trackingBundleContext, _consumingBundleContext,
				serviceReference,
				new ProxyContext(contextName, _parentServletContext), this,
				contextName, contextPath);

			_controllersMap.put(serviceReference, contextController);

			result.set(contextController);
		}
		catch (HttpWhiteboardFailureException httpWhiteboardFailureException) {
			_log.error(httpWhiteboardFailureException);

			_recordFailedServletContextDTO(
				serviceReference, contextName, contextPath,
				httpWhiteboardFailureException.getFailureReason());
		}
		catch (Exception exception) {
			_log.error(exception);

			_recordFailedServletContextDTO(
				serviceReference, contextName, contextPath,
				DTOConstants.FAILURE_REASON_EXCEPTION_ON_INIT);
		}

		return result;
	}

	public void destroy() {
		_contextServiceTracker.close();

		_controllersMap.clear();
		_dtosMap.clear();
		_registeredObjects.clear();

		_attributesMap = null;
		_trackingBundleContext = null;
		_consumingBundleContext = null;
		_legacyIdGenerator = null;
		_parentServletContext = null;
		_registeredObjects = null;
		_contextServiceTracker = null;
	}

	public boolean doDispatch(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String path)
		throws IOException, ServletException {

		DispatchTargets dispatchTargets = getDispatchTargets(path, null);

		if (dispatchTargets == null) {
			return false;
		}

		return dispatchTargets.doDispatch(
			httpServletRequest, httpServletResponse, path,
			httpServletRequest.getDispatcherType());
	}

	public Collection<ContextController> getContextControllers() {
		return Collections.unmodifiableCollection(_controllersMap.values());
	}

	public DispatchTargets getDispatchTargets(
		String pathString, RequestInfoDTO requestInfoDTO) {

		Path path = new Path(pathString);

		String queryString = path.getQueryString();
		String requestURI = path.getRequestURI();

		// perfect match

		DispatchTargets dispatchTargets = _getDispatchTargets(
			requestURI, null, queryString, Match.EXACT, requestInfoDTO);

		if (dispatchTargets == null) {

			// extension match

			dispatchTargets = _getDispatchTargets(
				requestURI, path.getExtension(), queryString, Match.EXTENSION,
				requestInfoDTO);
		}

		if (dispatchTargets == null) {

			// regex match

			dispatchTargets = _getDispatchTargets(
				requestURI, null, queryString, Match.REGEX, requestInfoDTO);
		}

		if (dispatchTargets == null) {

			// handle '/' aliases

			dispatchTargets = _getDispatchTargets(
				requestURI, null, queryString, Match.DEFAULT_SERVLET,
				requestInfoDTO);
		}

		return dispatchTargets;
	}

	public <T> T[] getDTOs(Class<T> clazz, Function<DTO, T> function) {
		Map<ServiceReference<?>, DTO> map = _dtosMap.get(clazz);

		return TransformUtil.transformToArray(
			map.values(),
			dto -> {
				if (function == null) {
					return clazz.cast(dto);
				}

				return function.apply(dto);
			},
			clazz);
	}

	public List<String> getHttpServiceEndpoints() {
		return Arrays.asList(
			StringPlus.from(
				_attributesMap.get(
					HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT)));
	}

	public ServletContext getParentServletContext() {
		return _parentServletContext;
	}

	public Set<Object> getRegisteredObjects() {
		return _registeredObjects;
	}

	public boolean matches(ServiceReference<?> serviceReference) {
		String target = (String)serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET);

		if (target == null) {
			return true;
		}

		Filter targetFilter;

		try {
			targetFilter = FrameworkUtil.createFilter(target);
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			throw new IllegalArgumentException(invalidSyntaxException);
		}

		if (targetFilter.matches(_attributesMap)) {
			return true;
		}

		return false;
	}

	@Override
	public synchronized void modifiedService(
		ServiceReference<ServletContextHelper> serviceReference,
		AtomicReference<ContextController> contextController) {

		removedService(serviceReference, contextController);

		AtomicReference<ContextController> added = addingService(
			serviceReference);

		contextController.set(added.get());
	}

	public void recordDTO(ServiceReference<?> serviceReference, DTO dto) {
		Map<ServiceReference<?>, DTO> dtosMap = _dtosMap.computeIfAbsent(
			dto.getClass(), clazz -> new ConcurrentHashMap<>());

		dtosMap.putIfAbsent(serviceReference, dto);
	}

	public void registerHttpServiceResources(
			Bundle bundle, String alias, String name, HttpContext httpContext)
		throws NamespaceException {

		if (alias == null) {
			throw new IllegalArgumentException("Alias cannot be null");
		}

		if (name == null) {
			throw new IllegalArgumentException("Name cannot be null");
		}

		String pattern = alias;

		if (pattern.startsWith("/*.")) {
			pattern = pattern.substring(1);
		}
		else if (!pattern.contains("*.") &&
				 !pattern.endsWith(Const.SLASH_STAR) &&
				 !pattern.endsWith(Const.SLASH)) {

			pattern += Const.SLASH_STAR;
		}

		ContextController.checkPattern(alias);

		synchronized (_legacyMappingsMap) {
			HttpServiceObjectRegistration httpServiceObjectRegistration = null;

			HttpContextHelperFactory httpContextHelperFactory =
				_getOrRegisterHttpContextHelperFactory(bundle, httpContext);

			try {
				String fullAlias = _getFullAlias(
					alias, httpContextHelperFactory);

				if (_legacyMappingsMap.containsKey(fullAlias)) {
					throw new PatternInUseException(alias);
				}

				BundleContext bundleContext = bundle.getBundleContext();

				ServiceRegistration<?> serviceRegistration =
					bundleContext.registerService(
						Object.class, "resource",
						HashMapDictionaryBuilder.<String, Object>put(
							Const.EQUINOX_LEGACY_TCCL_PROP,
							() -> {
								Thread currentThread = Thread.currentThread();

								return currentThread.getContextClassLoader();
							}
						).put(
							Constants.SERVICE_RANKING, Integer.MAX_VALUE
						).put(
							HttpWhiteboardConstants.
								HTTP_WHITEBOARD_CONTEXT_SELECT,
							httpContextHelperFactory.getFilter()
						).put(
							HttpWhiteboardConstants.
								HTTP_WHITEBOARD_RESOURCE_PATTERN,
							pattern
						).put(
							HttpWhiteboardConstants.
								HTTP_WHITEBOARD_RESOURCE_PREFIX,
							name
						).put(
							HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET,
							_targetFilter
						).build());

				httpServiceObjectRegistration =
					new HttpServiceObjectRegistration(
						fullAlias, serviceRegistration,
						httpContextHelperFactory, bundle);

				Set<HttpServiceObjectRegistration>
					httpServiceObjectRegistrations =
						_bundleRegistrationsMap.computeIfAbsent(
							bundle, k -> new HashSet<>());

				httpServiceObjectRegistrations.add(
					httpServiceObjectRegistration);

				Map<String, String> aliasCustomizationsMap =
					_bundleAliasCustomizationsMap.computeIfAbsent(
						bundle, k -> new HashMap<>());

				aliasCustomizationsMap.put(alias, fullAlias);

				_legacyMappingsMap.put(
					httpServiceObjectRegistration.serviceKey,
					httpServiceObjectRegistration);
			}
			finally {
				if ((httpServiceObjectRegistration == null) ||
					!_legacyMappingsMap.containsKey(
						httpServiceObjectRegistration.serviceKey)) {

					_decrementFactoryUseCount(httpContextHelperFactory);
				}
			}
		}
	}

	public void registerHttpServiceServlet(
			Bundle bundle, String alias, Servlet servlet,
			Dictionary<String, String> initParams, HttpContext httpContext)
		throws NamespaceException, ServletException {

		if (alias == null) {
			throw new IllegalArgumentException("Alias cannot be null");
		}

		if (servlet == null) {
			throw new IllegalArgumentException("Servlet cannot be null");
		}

		ContextController.checkPattern(alias);

		Object pattern = alias;

		if (!alias.endsWith(Const.SLASH_STAR) &&
			!alias.startsWith(Const.STAR_DOT) &&
			!alias.contains(Const.SLASH_STAR_DOT)) {

			if (alias.endsWith(Const.SLASH)) {
				pattern = new String[] {alias, alias + '*'};
			}
			else {
				pattern = new String[] {alias, alias + Const.SLASH_STAR};
			}
		}

		synchronized (_legacyMappingsMap) {
			LegacyServlet legacyServlet = new LegacyServlet(servlet);

			if (_registeredObjects.contains(legacyServlet)) {
				throw new ServletAlreadyRegisteredException(servlet);
			}

			HttpServiceObjectRegistration httpServiceObjectRegistration = null;
			ServiceRegistration<Servlet> serviceRegistration = null;

			HttpContextHelperFactory httpContextHelperFactory =
				_getOrRegisterHttpContextHelperFactory(bundle, httpContext);

			try {
				String fullAlias = _getFullAlias(
					alias, httpContextHelperFactory);

				if (_legacyMappingsMap.containsKey(fullAlias)) {
					throw new PatternInUseException(alias);
				}

				Class<?> clazz = servlet.getClass();

				String servletName = clazz.getName();

				if ((initParams != null) &&
					(initParams.get(Const.SERVLET_NAME) != null)) {

					servletName = initParams.get(Const.SERVLET_NAME);
				}

				Dictionary<String, Object> dictionary =
					HashMapDictionaryBuilder.<String, Object>put(
						Const.EQUINOX_LEGACY_TCCL_PROP,
						() -> {
							Thread currentThread = Thread.currentThread();

							return currentThread.getContextClassLoader();
						}
					).put(
						Constants.SERVICE_RANKING, Integer.MAX_VALUE
					).put(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
						httpContextHelperFactory.getFilter()
					).put(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME,
						servletName
					).put(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN,
						pattern
					).put(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET,
						_targetFilter
					).build();

				_fillInitParams(
					dictionary, initParams,
					HttpWhiteboardConstants.
						HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX);

				BundleContext bundleContext = bundle.getBundleContext();

				serviceRegistration = bundleContext.registerService(
					Servlet.class, legacyServlet, dictionary);

				legacyServlet.checkForError();

				httpServiceObjectRegistration =
					new HttpServiceObjectRegistration(
						fullAlias, serviceRegistration,
						httpContextHelperFactory, bundle);

				Set<HttpServiceObjectRegistration>
					httpServiceObjectRegistrations =
						_bundleRegistrationsMap.computeIfAbsent(
							bundle, k -> new HashSet<>());

				httpServiceObjectRegistrations.add(
					httpServiceObjectRegistration);

				Map<String, String> aliasCustomizationsMap =
					_bundleAliasCustomizationsMap.computeIfAbsent(
						bundle, k -> new HashMap<>());

				aliasCustomizationsMap.put(alias, fullAlias);

				_legacyMappingsMap.put(
					httpServiceObjectRegistration.serviceKey,
					httpServiceObjectRegistration);
			}
			finally {
				if ((httpServiceObjectRegistration == null) ||
					!_legacyMappingsMap.containsKey(
						httpServiceObjectRegistration.serviceKey)) {

					_decrementFactoryUseCount(httpContextHelperFactory);

					if (serviceRegistration != null) {
						serviceRegistration.unregister();
					}
				}
			}
		}
	}

	@Override
	public synchronized void removedService(
		ServiceReference<ServletContextHelper> serviceReference,
		AtomicReference<ContextController> contextControllerRef) {

		ContextController contextController = contextControllerRef.get();

		if (contextController != null) {
			contextController.destroy();
		}

		_controllersMap.remove(serviceReference);
		removeDTO(FailedServletContextDTO.class, serviceReference);
		_trackingBundleContext.ungetService(serviceReference);
	}

	public void removeDTO(
		Class<?> clazz, ServiceReference<?> serviceReference) {

		Map<ServiceReference<?>, DTO> map = _dtosMap.get(clazz);

		if (map != null) {
			map.remove(serviceReference);
		}
	}

	public void unregisterHttpServiceAlias(Bundle bundle, String alias) {
		synchronized (_legacyMappingsMap) {
			Map<String, String> aliasCustomizationsMap =
				_bundleAliasCustomizationsMap.get(bundle);

			String aliasCustomization =
				(aliasCustomizationsMap == null) ? null :
					aliasCustomizationsMap.remove(alias);

			if (aliasCustomization == null) {
				throw new IllegalArgumentException(
					"The bundle did not register the alias: " + alias);
			}

			HttpServiceObjectRegistration httpServiceObjectRegistration =
				_legacyMappingsMap.get(aliasCustomization);

			if (httpServiceObjectRegistration == null) {
				throw new IllegalArgumentException(
					"No registration found for alias: " + alias);
			}

			Set<HttpServiceObjectRegistration> httpServiceObjectRegistrations =
				_bundleRegistrationsMap.get(bundle);

			if ((httpServiceObjectRegistrations == null) ||
				!httpServiceObjectRegistrations.remove(
					httpServiceObjectRegistration)) {

				throw new IllegalArgumentException(
					"The bundle did not register the alias: " + alias);
			}

			try {
				httpServiceObjectRegistration.serviceRegistration.unregister();
			}
			catch (IllegalStateException illegalStateException) {
				if (_log.isDebugEnabled()) {
					_log.debug(illegalStateException);
				}
			}

			_decrementFactoryUseCount(httpServiceObjectRegistration.factory);
			_legacyMappingsMap.remove(aliasCustomization);
		}
	}

	public void unregisterHttpServiceObjects(Bundle bundle) {
		synchronized (_legacyMappingsMap) {
			_bundleAliasCustomizationsMap.remove(bundle);

			Set<HttpServiceObjectRegistration> httpServiceObjectRegistrations =
				_bundleRegistrationsMap.remove(bundle);

			if (httpServiceObjectRegistrations == null) {
				return;
			}

			for (HttpServiceObjectRegistration httpServiceObjectRegistration :
					httpServiceObjectRegistrations) {

				try {
					httpServiceObjectRegistration.serviceRegistration.
						unregister();
				}
				catch (IllegalStateException illegalStateException) {
					if (_log.isDebugEnabled()) {
						_log.debug(illegalStateException);
					}
				}

				_decrementFactoryUseCount(
					httpServiceObjectRegistration.factory);
				_legacyMappingsMap.remove(
					httpServiceObjectRegistration.serviceKey);
			}
		}
	}

	private void _decrementFactoryUseCount(
		HttpContextHelperFactory httpContextHelperFactory) {

		synchronized (_httpContextHelperFactoriesMap) {
			if (httpContextHelperFactory.decrementUseCount() == 0) {
				_httpContextHelperFactoriesMap.remove(
					httpContextHelperFactory.getHttpContext());
			}
		}
	}

	private void _fillInitParams(
		Dictionary<String, Object> props, Dictionary<String, String> initParams,
		String prefix) {

		if (initParams != null) {
			for (Enumeration<String> keysEnumeration = initParams.keys();
				 keysEnumeration.hasMoreElements();) {

				String key = keysEnumeration.nextElement();

				String value = initParams.get(key);

				if (value != null) {
					props.put(prefix + key, value);
				}
			}
		}
	}

	private long _generateLegacyId() {
		return _legacyIdGenerator.getAndIncrement();
	}

	private Collection<ContextController> _getContextControllers(
		String requestURI) {

		int pos = requestURI.lastIndexOf('/');

		while (true) {
			List<ContextController> contextControllers = new ArrayList<>();

			for (ContextController contextController :
					_controllersMap.values()) {

				if (Objects.equals(
						contextController.getContextPath(), requestURI)) {

					contextControllers.add(contextController);
				}
			}

			if (!contextControllers.isEmpty()) {
				return contextControllers;
			}

			if (pos > -1) {
				requestURI = requestURI.substring(0, pos);

				pos = requestURI.lastIndexOf('/');

				continue;
			}

			break;
		}

		return null;
	}

	private DispatchTargets _getDispatchTargets(
		String requestURI, String extension, String queryString, Match match,
		RequestInfoDTO requestInfoDTO) {

		Collection<ContextController> contextControllers =
			_getContextControllers(requestURI);

		if ((contextControllers == null) || contextControllers.isEmpty()) {
			return null;
		}

		Iterator<ContextController> iterator = contextControllers.iterator();

		ContextController firstContextController = iterator.next();

		String contextPath = firstContextController.getContextPath();

		requestURI = requestURI.substring(contextPath.length());

		int pos = requestURI.lastIndexOf('/');

		String servletPath = requestURI;

		String pathInfo = null;

		if (match == Match.DEFAULT_SERVLET) {
			pathInfo = servletPath;
			servletPath = Const.SLASH;
		}

		while (true) {
			for (ContextController contextController : contextControllers) {
				DispatchTargets dispatchTargets =
					contextController.getDispatchTargets(
						null, requestURI, servletPath, pathInfo, extension,
						queryString, match, requestInfoDTO);

				if (dispatchTargets != null) {
					return dispatchTargets;
				}
			}

			if (match == Match.EXACT) {
				break;
			}

			if (pos > -1) {
				servletPath = requestURI.substring(0, pos);

				pathInfo = requestURI.substring(pos);

				pos = servletPath.lastIndexOf('/');

				continue;
			}

			break;
		}

		return null;
	}

	private String _getFullAlias(
		String alias, HttpContextHelperFactory factory) {

		AtomicReference<ContextController> controllerRef =
			_contextServiceTracker.getService(factory.getServiceReference());

		if (controllerRef != null) {
			ContextController controller = controllerRef.get();

			if (controller != null) {
				return controller.getContextPath() + alias;
			}
		}

		return alias;
	}

	private HttpContextHelperFactory _getOrRegisterHttpContextHelperFactory(
		Bundle initiatingBundle, HttpContext httpContext) {

		if (httpContext == null) {
			throw new NullPointerException("A null HttpContext is not allowed");
		}

		synchronized (_httpContextHelperFactoriesMap) {
			HttpContextHelperFactory httpContextHelperFactory =
				_httpContextHelperFactoriesMap.get(httpContext);

			if (httpContextHelperFactory == null) {
				httpContextHelperFactory = new HttpContextHelperFactory(
					httpContext);

				httpContextHelperFactory.setRegistration(
					_consumingBundleContext.registerService(
						ServletContextHelper.class, httpContextHelperFactory,
						HashMapDictionaryBuilder.<String, Object>put(
							Const.EQUINOX_LEGACY_CONTEXT_HELPER, Boolean.TRUE
						).put(
							Const.EQUINOX_LEGACY_HTTP_CONTEXT_INITIATING_ID,
							initiatingBundle.getBundleId()
						).put(
							HttpWhiteboardConstants.
								HTTP_WHITEBOARD_CONTEXT_NAME,
							() -> {
								Class<?> clazz = httpContext.getClass();

								String className = clazz.getName();

								return StringBundler.concat(
									className.replaceAll(
										"[^a-zA-Z_0-9\\-]", "_"),
									"-", _generateLegacyId());
							}
						).put(
							HttpWhiteboardConstants.
								HTTP_WHITEBOARD_CONTEXT_PATH,
							"/"
						).put(
							HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET,
							_targetFilter
						).build()));

				_httpContextHelperFactoriesMap.put(
					httpContext, httpContextHelperFactory);
			}

			httpContextHelperFactory.incrementUseCount();

			return httpContextHelperFactory;
		}
	}

	private void _recordFailedServletContextDTO(
		ServiceReference<ServletContextHelper> serviceReference,
		String contextName, String contextPath, int failureReason) {

		FailedServletContextDTO failedServletContextDTO =
			new FailedServletContextDTO();

		failedServletContextDTO.attributes = Collections.emptyMap();
		failedServletContextDTO.contextPath = contextPath;
		failedServletContextDTO.errorPageDTOs = new ErrorPageDTO[0];
		failedServletContextDTO.failureReason = failureReason;
		failedServletContextDTO.filterDTOs = new FilterDTO[0];
		failedServletContextDTO.initParams = ServiceProperties.parseInitParams(
			serviceReference,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_INIT_PARAM_PREFIX);
		failedServletContextDTO.listenerDTOs = new ListenerDTO[0];
		failedServletContextDTO.name = contextName;
		failedServletContextDTO.resourceDTOs = new ResourceDTO[0];
		failedServletContextDTO.serviceId = (Long)serviceReference.getProperty(
			Constants.SERVICE_ID);
		failedServletContextDTO.servletDTOs = new ServletDTO[0];

		recordDTO(serviceReference, failedServletContextDTO);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HttpServiceRuntimeController.class.getName());

	private Map<String, Object> _attributesMap;
	private final Map<Bundle, Map<String, String>>
		_bundleAliasCustomizationsMap = new HashMap<>();
	private final Map<Bundle, Set<HttpServiceObjectRegistration>>
		_bundleRegistrationsMap = new HashMap<>();
	private BundleContext _consumingBundleContext;
	private ServiceTracker
		<ServletContextHelper, AtomicReference<ContextController>>
			_contextServiceTracker;
	private final ConcurrentMap
		<ServiceReference<ServletContextHelper>, ContextController>
			_controllersMap = new ConcurrentHashMap<>();
	private final Map<Class<?>, Map<ServiceReference<?>, DTO>> _dtosMap =
		new ConcurrentHashMap<>();
	private final Map<HttpContext, HttpContextHelperFactory>
		_httpContextHelperFactoriesMap = Collections.synchronizedMap(
			new HashMap<>());
	private AtomicLong _legacyIdGenerator = new AtomicLong(0);
	private final Map<Object, HttpServiceObjectRegistration>
		_legacyMappingsMap = Collections.synchronizedMap(new HashMap<>());
	private ServletContext _parentServletContext;
	private Set<Object> _registeredObjects = Collections.newSetFromMap(
		new ConcurrentHashMap<>());
	private final String _targetFilter;
	private BundleContext _trackingBundleContext;

	private static class LegacyServiceObject {

		public void checkForError() {
			Exception exception = error.get();

			if (exception != null) {
				ReflectionUtil.throwException(exception);
			}
		}

		protected final AtomicReference<Exception> error =
			new AtomicReference<>(
				new ServletException("The init() method was never called."));

	}

	private static class LegacyServlet
		extends LegacyServiceObject implements Servlet {

		public LegacyServlet(Servlet servlet) {
			_servlet = servlet;
		}

		@Override
		public void destroy() {
			_servlet.destroy();
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof LegacyServlet) {
				LegacyServlet legacyServlet = (LegacyServlet)other;

				other = legacyServlet._servlet;
			}

			return _servlet.equals(other);
		}

		@Override
		public ServletConfig getServletConfig() {
			return _servlet.getServletConfig();
		}

		@Override
		public String getServletInfo() {
			return _servlet.getServletInfo();
		}

		@Override
		public int hashCode() {
			return _servlet.hashCode();
		}

		@Override
		public void init(ServletConfig config) {
			try {
				_servlet.init(config);

				error.set(null);
			}
			catch (Exception exception) {
				error.set(exception);

				ReflectionUtil.throwException(exception);
			}
		}

		@Override
		public void service(
				ServletRequest servletRequest, ServletResponse servletResponse)
			throws IOException, ServletException {

			_servlet.service(servletRequest, servletResponse);
		}

		private final Servlet _servlet;

	}

}