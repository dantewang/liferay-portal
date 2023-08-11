/*******************************************************************************
 * Copyright (c) 2016 Raymond Augé and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 ******************************************************************************/

package com.liferay.portal.osgi.web.http.servlet.internal.context;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServiceRuntimeController;
import com.liferay.portal.osgi.web.http.servlet.internal.customizer.EventListenerServiceTrackerCustomizer;
import com.liferay.portal.osgi.web.http.servlet.internal.customizer.FilterServiceTrackerCustomizer;
import com.liferay.portal.osgi.web.http.servlet.internal.customizer.ResourceServiceTrackerCustomizer;
import com.liferay.portal.osgi.web.http.servlet.internal.customizer.ServletServiceTrackerCustomizer;
import com.liferay.portal.osgi.web.http.servlet.internal.error.IllegalContextNameException;
import com.liferay.portal.osgi.web.http.servlet.internal.error.IllegalContextPathException;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EndpointRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.FilterRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.ListenerRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.ResourceRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.ServletRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.HttpSessionAdaptor;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.Match;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.ServletContextAdaptor;
import com.liferay.portal.osgi.web.http.servlet.internal.util.DTOUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.util.EventListeners;
import com.liferay.portal.osgi.web.http.servlet.internal.util.PathUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.util.ServiceProperties;

import java.net.URI;
import java.net.URISyntaxException;

import java.security.AccessController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.ErrorPageDTO;
import org.osgi.service.http.runtime.dto.FilterDTO;
import org.osgi.service.http.runtime.dto.ListenerDTO;
import org.osgi.service.http.runtime.dto.RequestInfoDTO;
import org.osgi.service.http.runtime.dto.ResourceDTO;
import org.osgi.service.http.runtime.dto.ServletContextDTO;
import org.osgi.service.http.runtime.dto.ServletDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Raymond Augé
 */
public class ContextController {

	public ContextController(
		BundleContext bundleContext,
		ServiceReference<ServletContextHelper> serviceReference,
		ProxyContext proxyContext,
		HttpServiceRuntimeController httpServiceRuntimeController,
		String contextName, String contextPath) {

		_validate(contextName, contextPath);

		_bundleContext = bundleContext;

		_servletContextHelperServiceReference = serviceReference;

		_proxyContext = proxyContext;
		_httpServiceRuntimeController = httpServiceRuntimeController;
		_contextName = contextName;

		if (contextPath.equals(StringPool.SLASH)) {
			contextPath = StringPool.BLANK;
		}

		_contextPath = contextPath;

		_contextServiceId = (long)serviceReference.getProperty(
			Constants.SERVICE_ID);

		_initParamsMap = ServiceProperties.parseInitParams(
			serviceReference,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_INIT_PARAM_PREFIX,
			proxyContext.getServletContext());

		_servletContextListenerServiceTracker = new ServiceTracker<>(
			_bundleContext, ServletContextListener.class.getName(),
			new EventListenerServiceTrackerCustomizer(
				_bundleContext, this, httpServiceRuntimeController));

		_servletContextListenerServiceTracker.open();

		_servletContextAttributeListenerServiceTracker = new ServiceTracker<>(
			_bundleContext, ServletContextAttributeListener.class.getName(),
			new EventListenerServiceTrackerCustomizer(
				_bundleContext, this, httpServiceRuntimeController));

		_servletContextAttributeListenerServiceTracker.open();

		_servletRequestListenerServiceTracker = new ServiceTracker<>(
			_bundleContext, ServletRequestListener.class.getName(),
			new EventListenerServiceTrackerCustomizer(
				_bundleContext, this, httpServiceRuntimeController));

		_servletRequestListenerServiceTracker.open();

		_servletRequestAttributeListenerServiceTracker = new ServiceTracker<>(
			_bundleContext, ServletRequestAttributeListener.class.getName(),
			new EventListenerServiceTrackerCustomizer(
				_bundleContext, this, httpServiceRuntimeController));

		_servletRequestAttributeListenerServiceTracker.open();

		_httpSessionListenerServiceTracker = new ServiceTracker<>(
			_bundleContext, HttpSessionListener.class.getName(),
			new EventListenerServiceTrackerCustomizer(
				_bundleContext, this, httpServiceRuntimeController));

		_httpSessionListenerServiceTracker.open();

		_httpSessionAttributeListenerServiceTracker = new ServiceTracker<>(
			_bundleContext, HttpSessionAttributeListener.class.getName(),
			new EventListenerServiceTrackerCustomizer(
				_bundleContext, this, httpServiceRuntimeController));

		_httpSessionAttributeListenerServiceTracker.open();

		ServletContext servletContext =
			httpServiceRuntimeController.getParentServletContext();

		if ((servletContext.getMajorVersion() >= 3) &&
			(servletContext.getMinorVersion() > 0)) {

			_httpSessionIdListenerServiceTracker = new ServiceTracker<>(
				_bundleContext, HttpSessionIdListener.class.getName(),
				new EventListenerServiceTrackerCustomizer(
					_bundleContext, this, httpServiceRuntimeController));

			_httpSessionIdListenerServiceTracker.open();
		}
		else {
			_httpSessionIdListenerServiceTracker = null;
		}

		_filterServiceTracker = new ServiceTracker<>(
			_bundleContext, Filter.class,
			new FilterServiceTrackerCustomizer(
				_bundleContext, this, httpServiceRuntimeController));

		_filterServiceTracker.open();

		_servletServiceTracker = new ServiceTracker<>(
			_bundleContext, Servlet.class,
			new ServletServiceTrackerCustomizer(
				_bundleContext, this, httpServiceRuntimeController));

		_servletServiceTracker.open();

		_resourceServiceTracker = new ServiceTracker<>(
			_bundleContext, Object.class,
			new ResourceServiceTrackerCustomizer(
				_bundleContext, this, httpServiceRuntimeController));

		_resourceServiceTracker.open();
	}

	public void checkShutdown() {
		if (_shutdown) {
			throw new IllegalStateException("Context is already shutdown");
		}
	}

	public void createContextAttributes() {
		getProxyContext().createContextAttributes(this);
	}

	public ServletContext createServletContextAdaptor(
		Bundle curBundle, ServletContextHelper curServletContextHelper) {

		ServletContextAdaptor adaptor = new ServletContextAdaptor(
			this, curBundle, curServletContextHelper, _eventListeners,
			AccessController.getContext());

		return adaptor.createServletContext();
	}

	public void destroy() {
		_flushActiveSessions();
		_resourceServiceTracker.close();
		_servletServiceTracker.close();
		_filterServiceTracker.close();

		if (_httpSessionIdListenerServiceTracker != null) {
			_httpSessionIdListenerServiceTracker.close();
		}

		_httpSessionAttributeListenerServiceTracker.close();
		_httpSessionListenerServiceTracker.close();
		_servletRequestAttributeListenerServiceTracker.close();
		_servletRequestListenerServiceTracker.close();
		_servletContextAttributeListenerServiceTracker.close();
		_servletContextListenerServiceTracker.close();

		_endpointRegistrations.clear();
		_filterRegistrations.clear();
		_listenerRegistrations.clear();
		_eventListeners.clear();
		_proxyContext.destroy();

		_shutdown = true;
	}

	public void destroyContextAttributes() {
		if (_shutdown) {
			return;
		}

		_proxyContext.destroyContextAttributes(this);
	}

	public void fireSessionIdChanged(String oldSessionId) {
		if (_shutdown) {
			return;
		}

		List<HttpSessionIdListener> listeners = _eventListeners.get(
			HttpSessionIdListener.class);

		if (listeners.isEmpty()) {
			return;
		}

		for (HttpSessionAdaptor httpSessionAdaptor :
				_activeSessionsMap.values()) {

			HttpSessionEvent httpSessionEvent = new HttpSessionEvent(
				httpSessionAdaptor);

			for (HttpSessionIdListener listener : listeners) {
				listener.sessionIdChanged(httpSessionEvent, oldSessionId);
			}
		}
	}

	public Map<String, HttpSessionAdaptor> getActiveSessions() {
		return _activeSessionsMap;
	}

	public String getContextName() {
		return _contextName;
	}

	public String getContextPath() {
		return _contextPath;
	}

	public DispatchTargets getDispatchTargets(
		String pathString, RequestInfoDTO requestInfoDTO) {

		String[] parts = _toParts(pathString);

		String requestURI = parts[0];
		String queryString = parts[2];

		// perfect match

		DispatchTargets dispatchTargets = _getDispatchTargets(
			requestURI, null, queryString, Match.EXACT, requestInfoDTO);

		if (dispatchTargets == null) {

			// extension match

			dispatchTargets = _getDispatchTargets(
				requestURI, parts[1], queryString, Match.EXTENSION,
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

	public DispatchTargets getDispatchTargets(
		String servletName, String requestURI, String servletPath,
		String pathInfo, String extension, String queryString, Match match,
		RequestInfoDTO requestInfoDTO) {

		checkShutdown();

		EndpointRegistration<?> endpointRegistration = null;

		for (EndpointRegistration<?> curEndpointRegistration :
				_endpointRegistrations) {

			if (Objects.nonNull(
					curEndpointRegistration.match(
						servletName, servletPath, pathInfo, extension,
						match))) {

				endpointRegistration = curEndpointRegistration;

				break;
			}
		}

		if (endpointRegistration == null) {
			return null;
		}

		if (match == Match.EXTENSION) {
			servletPath = servletPath + pathInfo;
			pathInfo = null;
		}

		_addEndpointRegistrationsToRequestInfo(
			endpointRegistration, requestInfoDTO);

		if (_filterRegistrations.isEmpty()) {
			return new DispatchTargets(
				this, endpointRegistration, servletName, requestURI,
				servletPath, pathInfo, queryString);
		}

		if (requestURI != null) {
			int x = requestURI.lastIndexOf('.');

			if (x != -1) {
				extension = requestURI.substring(x + 1);
			}
		}

		List<FilterRegistration> matchingFilterRegistrations =
			new ArrayList<>();

		_collectFilters(
			matchingFilterRegistrations, endpointRegistration.getName(),
			requestURI, extension);

		_addFilterRegistrationsToRequestInfo(
			matchingFilterRegistrations, requestInfoDTO);

		return new DispatchTargets(
			this, endpointRegistration, matchingFilterRegistrations,
			servletName, requestURI, servletPath, pathInfo, queryString);
	}

	public Set<EndpointRegistration<?>> getEndpointRegistrations() {
		return _endpointRegistrations;
	}

	public EventListeners getEventListeners() {
		return _eventListeners;
	}

	public Set<FilterRegistration> getFilterRegistrations() {
		return _filterRegistrations;
	}

	public String getFullContextPath() {
		List<String> endpoints =
			_httpServiceRuntimeController.getHttpServiceEndpoints();

		if (endpoints.isEmpty()) {
			String servletPath = _proxyContext.getServletPath();

			return servletPath.concat(_contextPath);
		}

		String defaultEndpoint = endpoints.get(0);

		if (defaultEndpoint.endsWith("/")) {
			defaultEndpoint = defaultEndpoint.substring(
				0, defaultEndpoint.length() - 1);
		}

		return defaultEndpoint + _contextPath;
	}

	public HttpServiceRuntimeController getHttpServiceRuntimeController() {
		return _httpServiceRuntimeController;
	}

	public Map<String, String> getInitParams() {
		return _initParamsMap;
	}

	public Set<ListenerRegistration> getListenerRegistrations() {
		return _listenerRegistrations;
	}

	public ProxyContext getProxyContext() {
		return _proxyContext;
	}

	public long getServiceId() {
		return _contextServiceId;
	}

	public synchronized ServletContextDTO getServletContextDTO() {
		ServletContextDTO servletContextDTO = new ServletContextDTO();

		servletContextDTO.attributes = _getDTOAttributes(
			_proxyContext.getServletContext());
		servletContextDTO.contextPath = getContextPath();
		servletContextDTO.initParams = new HashMap<>(_initParamsMap);
		servletContextDTO.name = getContextName();
		servletContextDTO.serviceId = getServiceId();

		_collectEndpointDTOs(servletContextDTO);
		_collectFilterDTOs(servletContextDTO);
		_collectListenerDTOs(servletContextDTO);

		return servletContextDTO;
	}

	public ServletContextHelper getServletContextHelper(Bundle curBundle) {
		BundleContext bundleContext = curBundle.getBundleContext();

		return bundleContext.getService(_servletContextHelperServiceReference);
	}

	public HttpSessionAdaptor getSessionAdaptor(
		HttpSession httpSession, ServletContext servletContext) {

		String sessionId = httpSession.getId();

		HttpSessionAdaptor httpSessionAdaptor = _activeSessionsMap.get(
			sessionId);

		if (httpSessionAdaptor != null) {
			return httpSessionAdaptor;
		}

		httpSessionAdaptor = HttpSessionAdaptor.createHttpSessionAdaptor(
			httpSession, servletContext, this);

		HttpSessionAdaptor previousHttpSessionAdaptor =
			_activeSessionsMap.putIfAbsent(sessionId, httpSessionAdaptor);

		if (previousHttpSessionAdaptor != null) {
			return previousHttpSessionAdaptor;
		}

		List<HttpSessionListener> httpSessionListeners = _eventListeners.get(
			HttpSessionListener.class);

		if (httpSessionListeners.isEmpty()) {
			return httpSessionAdaptor;
		}

		HttpSessionEvent httpSessionEvent = new HttpSessionEvent(
			httpSessionAdaptor);

		for (HttpSessionListener httpSessionListener : httpSessionListeners) {
			httpSessionListener.sessionCreated(httpSessionEvent);
		}

		return httpSessionAdaptor;
	}

	public boolean matches(org.osgi.framework.Filter targetFilter) {
		return targetFilter.match(_servletContextHelperServiceReference);
	}

	public boolean matches(ServiceReference<?> serviceReference) {
		String contextSelector = (String)serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT);

		if (_contextName.equals(contextSelector)) {
			return true;
		}

		if (contextSelector == null) {
			contextSelector = StringBundler.concat(
				"(", HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "=",
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				")");
		}

		if (contextSelector.startsWith(StringPool.OPEN_PARENTHESIS)) {
			org.osgi.framework.Filter targetFilter;

			try {
				targetFilter = FrameworkUtil.createFilter(contextSelector);
			}
			catch (InvalidSyntaxException invalidSyntaxException) {
				throw new IllegalArgumentException(invalidSyntaxException);
			}

			if (matches(targetFilter)) {
				return true;
			}
		}

		return false;
	}

	public void removeActiveSession(String id) {
		_activeSessionsMap.remove(id);
	}

	@Override
	public String toString() {
		String value = _string;

		if (value == null) {
			value = StringBundler.concat(
				ContextController.class.getSimpleName(), '[', _contextName,
				", ", _bundleContext.getBundle(), ']');

			_string = value;
		}

		return value;
	}

	public void ungetServletContextHelper(Bundle curBundle) {
		BundleContext bundleContext = curBundle.getBundleContext();

		try {
			bundleContext.ungetService(_servletContextHelperServiceReference);
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalStateException);
			}
		}
	}

	public static final class ServiceHolder<S>
		implements Comparable<ServiceHolder<?>> {

		public ServiceHolder(
			S service, Bundle bundle, long serviceId, int serviceRanking) {

			_service = service;
			_bundle = bundle;
			_serviceId = serviceId;
			_serviceRanking = serviceRanking;

			_serviceObjects = null;
		}

		public ServiceHolder(ServiceObjects<S> serviceObjects) {
			_serviceObjects = serviceObjects;

			ServiceReference<S> serviceReference =
				serviceObjects.getServiceReference();

			_service = serviceObjects.getService();
			_bundle = serviceReference.getBundle();
			_serviceId = (Long)serviceReference.getProperty(
				Constants.SERVICE_ID);

			_serviceRanking = GetterUtil.getInteger(
				serviceReference.getProperty(Constants.SERVICE_RANKING));
		}

		@Override
		public int compareTo(ServiceHolder<?> other) {
			if (_serviceRanking != other._serviceRanking) {
				if (_serviceRanking < other._serviceRanking) {
					return 1;
				}

				return -1;
			}

			return Long.compare(_serviceId, other._serviceId);
		}

		public S get() {
			return _service;
		}

		public Bundle getBundle() {
			return _bundle;
		}

		public ServiceReference<S> getServiceReference() {
			if (_serviceObjects == null) {
				return null;
			}

			return _serviceObjects.getServiceReference();
		}

		public void release() {
			if ((_serviceObjects != null) && (_service != null)) {
				try {
					_serviceObjects.ungetService(_service);
				}
				catch (IllegalStateException illegalStateException) {
					if (_log.isDebugEnabled()) {
						_log.debug(illegalStateException);
					}
				}
			}
		}

		private final Bundle _bundle;
		private final S _service;
		private final long _serviceId;
		private final ServiceObjects<S> _serviceObjects;
		private final int _serviceRanking;

	}

	private void _addEndpointRegistrationsToRequestInfo(
		EndpointRegistration<?> endpointRegistration,
		RequestInfoDTO requestInfoDTO) {

		if (requestInfoDTO == null) {
			return;
		}

		requestInfoDTO.servletContextId = getServiceId();

		if (endpointRegistration instanceof ResourceRegistration) {
			requestInfoDTO.resourceDTO =
				(ResourceDTO)endpointRegistration.getD();
		}
		else {
			requestInfoDTO.servletDTO = (ServletDTO)endpointRegistration.getD();
		}
	}

	private void _addFilterRegistrationsToRequestInfo(
		List<FilterRegistration> matchedFilterRegistrations,
		RequestInfoDTO requestInfoDTO) {

		if (requestInfoDTO == null) {
			return;
		}

		FilterDTO[] filterDTOs =
			new FilterDTO[matchedFilterRegistrations.size()];

		for (int i = 0; i < filterDTOs.length; i++) {
			FilterRegistration filterRegistration =
				matchedFilterRegistrations.get(i);

			filterDTOs[i] = filterRegistration.getD();
		}

		requestInfoDTO.filterDTOs = filterDTOs;
	}

	private void _collectEndpointDTOs(ServletContextDTO servletContextDTO) {
		List<ErrorPageDTO> errorPageDTOs = new ArrayList<>();
		List<ResourceDTO> resourceDTOs = new ArrayList<>();
		List<ServletDTO> servletDTOs = new ArrayList<>();

		for (EndpointRegistration<?> endpointRegistration :
				_endpointRegistrations) {

			if (endpointRegistration instanceof ResourceRegistration) {
				resourceDTOs.add(
					DTOUtil.clone((ResourceDTO)endpointRegistration.getD()));
			}
			else {
				ServletRegistration servletRegistration =
					(ServletRegistration)endpointRegistration;

				servletDTOs.add(DTOUtil.clone(servletRegistration.getD()));

				ErrorPageDTO errorPageDTO =
					servletRegistration.getErrorPageDTO();

				if (errorPageDTO != null) {
					errorPageDTOs.add(DTOUtil.clone(errorPageDTO));
				}
			}
		}

		servletContextDTO.errorPageDTOs = errorPageDTOs.toArray(
			new ErrorPageDTO[0]);
		servletContextDTO.resourceDTOs = resourceDTOs.toArray(
			new ResourceDTO[0]);
		servletContextDTO.servletDTOs = servletDTOs.toArray(new ServletDTO[0]);
	}

	private void _collectFilterDTOs(ServletContextDTO servletContextDTO) {
		List<FilterDTO> filterDTOs = new ArrayList<>();

		for (FilterRegistration filterRegistration : _filterRegistrations) {
			filterDTOs.add(DTOUtil.clone(filterRegistration.getD()));
		}

		servletContextDTO.filterDTOs = filterDTOs.toArray(new FilterDTO[0]);
	}

	private void _collectFilters(
		List<FilterRegistration> matchingFilterRegistrations,
		String servletName, String requestURI, String extension) {

		for (FilterRegistration filterRegistration : _filterRegistrations) {
			if (Objects.nonNull(
					filterRegistration.match(
						servletName, requestURI, extension)) &&
				!matchingFilterRegistrations.contains(filterRegistration)) {

				matchingFilterRegistrations.add(filterRegistration);
			}
		}
	}

	private void _collectListenerDTOs(ServletContextDTO servletContextDTO) {
		List<ListenerDTO> listenerDTOs = new ArrayList<>();

		for (ListenerRegistration listenerRegistration :
				_listenerRegistrations) {

			listenerDTOs.add(DTOUtil.clone(listenerRegistration.getD()));
		}

		servletContextDTO.listenerDTOs = listenerDTOs.toArray(
			new ListenerDTO[0]);
	}

	private void _flushActiveSessions() {
		Collection<HttpSessionAdaptor> httpSessionAdaptors =
			_activeSessionsMap.values();

		Iterator<HttpSessionAdaptor> iterator = httpSessionAdaptors.iterator();

		while (iterator.hasNext()) {
			HttpSessionAdaptor httpSessionAdaptor = iterator.next();

			httpSessionAdaptor.invalidate();

			iterator.remove();
		}
	}

	private DispatchTargets _getDispatchTargets(
		String requestURI, String extension, String queryString, Match match,
		RequestInfoDTO requestInfoDTO) {

		int index = requestURI.lastIndexOf('/');

		String servletPath = requestURI;
		String pathInfo = null;

		if (match == Match.DEFAULT_SERVLET) {
			pathInfo = servletPath;
			servletPath = StringPool.SLASH;
		}

		while (true) {
			DispatchTargets dispatchTargets = getDispatchTargets(
				null, requestURI, servletPath, pathInfo, extension, queryString,
				match, requestInfoDTO);

			if (dispatchTargets != null) {
				return dispatchTargets;
			}

			if ((match == Match.EXACT) || (index == -1)) {
				break;
			}

			servletPath = requestURI.substring(0, index);

			pathInfo = requestURI.substring(index);

			index = servletPath.lastIndexOf('/');
		}

		return null;
	}

	private Map<String, Object> _getDTOAttributes(
		ServletContext servletContext) {

		Map<String, Object> map = new HashMap<>();

		for (Enumeration<String> attributeNamesEnumeration =
				servletContext.getAttributeNames();
			 attributeNamesEnumeration.hasMoreElements();) {

			String name = attributeNamesEnumeration.nextElement();

			map.put(name, DTOUtil.mapValue(servletContext.getAttribute(name)));
		}

		return Collections.unmodifiableMap(map);
	}

	private String[] _toParts(String path) {
		String requestURI = PathUtil.extractRequestURI(path);
		String queryString = null;

		if (!requestURI.equals(path) && (path.length() > requestURI.length())) {
			queryString = path.substring(requestURI.length() + 1);
		}

		int index = requestURI.lastIndexOf(CharPool.PERIOD);

		String extension = null;

		if ((index != -1) && (index > requestURI.lastIndexOf(CharPool.SLASH))) {
			extension = requestURI.substring(index + 1);
		}

		return new String[] {requestURI, extension, queryString};
	}

	private void _validate(
		String preValidationContextName, String preValidationContextPath) {

		Matcher matcher = _contextNamePattern.matcher(preValidationContextName);

		if (!matcher.matches()) {
			throw new IllegalContextNameException(
				"The context name '" + preValidationContextName +
					"' does not follow Bundle-SymbolicName syntax.",
				DTOConstants.FAILURE_REASON_VALIDATION_FAILED);
		}

		try {
			new URI("http", "localhost", preValidationContextPath, null);
		}
		catch (URISyntaxException uriSyntaxException) {
			IllegalContextPathException illegalContextPathException =
				new IllegalContextPathException(
					"The context path '" + preValidationContextPath +
						"' is not valid URI path syntax.",
					DTOConstants.FAILURE_REASON_VALIDATION_FAILED);

			illegalContextPathException.addSuppressed(uriSyntaxException);

			throw illegalContextPathException;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContextController.class.getName());

	private static final Pattern _contextNamePattern = Pattern.compile(
		"^([a-zA-Z_0-9\\-]+\\.)*[a-zA-Z_0-9\\-]+$");

	private final ConcurrentMap<String, HttpSessionAdaptor> _activeSessionsMap =
		new ConcurrentHashMap<>();
	private final BundleContext _bundleContext;
	private final String _contextName;
	private final String _contextPath;
	private final long _contextServiceId;
	private final Set<EndpointRegistration<?>> _endpointRegistrations =
		new ConcurrentSkipListSet<>();
	private final EventListeners _eventListeners = new EventListeners();
	private final Set<FilterRegistration> _filterRegistrations =
		new ConcurrentSkipListSet<>();
	private final ServiceTracker<Filter, AtomicReference<FilterRegistration>>
		_filterServiceTracker;
	private final HttpServiceRuntimeController _httpServiceRuntimeController;
	private final ServiceTracker
		<EventListener, AtomicReference<ListenerRegistration>>
			_httpSessionAttributeListenerServiceTracker;
	private final ServiceTracker
		<EventListener, AtomicReference<ListenerRegistration>>
			_httpSessionIdListenerServiceTracker;
	private final ServiceTracker
		<EventListener, AtomicReference<ListenerRegistration>>
			_httpSessionListenerServiceTracker;
	private final Map<String, String> _initParamsMap;
	private final Set<ListenerRegistration> _listenerRegistrations =
		new HashSet<>();
	private final ProxyContext _proxyContext;
	private final ServiceTracker<Object, AtomicReference<ResourceRegistration>>
		_resourceServiceTracker;
	private final ServiceTracker
		<EventListener, AtomicReference<ListenerRegistration>>
			_servletContextAttributeListenerServiceTracker;
	private final ServiceReference<ServletContextHelper>
		_servletContextHelperServiceReference;
	private final ServiceTracker
		<EventListener, AtomicReference<ListenerRegistration>>
			_servletContextListenerServiceTracker;
	private final ServiceTracker
		<EventListener, AtomicReference<ListenerRegistration>>
			_servletRequestAttributeListenerServiceTracker;
	private final ServiceTracker
		<EventListener, AtomicReference<ListenerRegistration>>
			_servletRequestListenerServiceTracker;
	private final ServiceTracker<Servlet, AtomicReference<ServletRegistration>>
		_servletServiceTracker;
	private boolean _shutdown;
	private String _string;

}