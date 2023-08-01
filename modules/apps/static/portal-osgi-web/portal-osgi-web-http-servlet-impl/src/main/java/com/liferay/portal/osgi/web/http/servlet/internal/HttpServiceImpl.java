/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.osgi.web.http.servlet.internal;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.osgi.web.http.servlet.internal.constants.HttpServiceConstants;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.HttpContextHelperFactory;
import com.liferay.portal.osgi.web.http.servlet.internal.error.PatternInUseException;
import com.liferay.portal.osgi.web.http.servlet.internal.error.ServletAlreadyRegisteredException;
import com.liferay.portal.osgi.web.http.servlet.internal.util.Const;

import java.io.IOException;

import java.net.URL;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Dante Wang
 */
public class HttpServiceImpl implements HttpService {

	public HttpServiceImpl(
		Bundle bundle,
		HttpServiceRuntimeController httpServiceRuntimeController,
		String targetFilter) {

		_bundle = bundle;
		_httpServiceRuntimeController = httpServiceRuntimeController;
		_targetFilter = targetFilter;
	}

	@Override
	public synchronized HttpContext createDefaultHttpContext() {
		_checkShutdown();

		return new DefaultHttpContext();
	}

	@Override
	public synchronized void registerResources(
		String alias, String name, HttpContext httpContext) {

		_checkShutdown();

		try {
			AccessController.doPrivileged(
				(PrivilegedExceptionAction<Void>)() -> {
					_registerHttpServiceResources(
						alias, name, _getOrCreateHttpContext(httpContext));

					return null;
				});
		}
		catch (PrivilegedActionException privilegedActionException) {
			ReflectionUtil.throwException(
				privilegedActionException.getException());
		}
	}

	@Override
	public synchronized void registerServlet(
		String alias, Servlet servlet, Dictionary initParams,
		HttpContext httpContext) {

		_checkShutdown();

		try {
			AccessController.doPrivileged(
				(PrivilegedExceptionAction<Void>)() -> {
					_registerHttpServiceServlet(
						alias, servlet, initParams,
						_getOrCreateHttpContext(httpContext));

					return null;
				});
		}
		catch (PrivilegedActionException privilegedActionException) {
			ReflectionUtil.throwException(
				privilegedActionException.getException());
		}
	}

	@Override
	public synchronized void unregister(String alias) {
		_checkShutdown();

		synchronized (_legacyMappingsMap) {
			String aliasCustomization = _aliasCustomizationsMap.remove(alias);

			if (aliasCustomization == null) {
				throw new IllegalArgumentException(
					"The bundle did not register the alias: " + alias);
			}

			HttpServiceRegistrationBag httpServiceRegistrationBag =
				_legacyMappingsMap.get(aliasCustomization);

			if (httpServiceRegistrationBag == null) {
				throw new IllegalArgumentException(
					"No registration found for alias: " + alias);
			}

			if (!_httpServiceRegistrationBags.remove(
					httpServiceRegistrationBag)) {

				throw new IllegalArgumentException(
					"The bundle did not register the alias: " + alias);
			}

			try {
				httpServiceRegistrationBag._serviceRegistration.unregister();
			}
			catch (IllegalStateException illegalStateException) {
				if (_log.isDebugEnabled()) {
					_log.debug(illegalStateException);
				}
			}

			_decrementFactoryUseCount(httpServiceRegistrationBag._factory);
			_legacyMappingsMap.remove(aliasCustomization);
		}
	}

	protected synchronized void shutdown() {
		synchronized (_legacyMappingsMap) {
			_aliasCustomizationsMap.clear();

			for (HttpServiceRegistrationBag httpServiceRegistrationBag :
					_httpServiceRegistrationBags) {

				try {
					httpServiceRegistrationBag._serviceRegistration.
						unregister();
				}
				catch (IllegalStateException illegalStateException) {
					if (_log.isDebugEnabled()) {
						_log.debug(illegalStateException);
					}
				}

				_decrementFactoryUseCount(httpServiceRegistrationBag._factory);
				_legacyMappingsMap.remove(
					httpServiceRegistrationBag._serviceKey);
			}
		}

		_shutdown = true;
	}

	private void _checkShutdown() {
		if (_shutdown) {
			throw new IllegalStateException(
				"Service instance is already shutdown");
		}
	}

	private void _decrementFactoryUseCount(
		HttpContextHelperFactory httpContextHelperFactory) {

		if (httpContextHelperFactory.decrementUseCount() == 0) {
			_httpContextHelperFactoriesMap.remove(
				httpContextHelperFactory.getHttpContext());
		}
	}

	private void _fillInitParams(
		Dictionary<String, Object> props,
		Dictionary<String, String> initParams) {

		if (initParams != null) {
			for (Enumeration<String> keysEnumeration = initParams.keys();
				 keysEnumeration.hasMoreElements();) {

				String key = keysEnumeration.nextElement();

				String value = initParams.get(key);

				if (value != null) {
					props.put(
						HttpWhiteboardConstants.
							HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + key,
						value);
				}
			}
		}
	}

	private long _generateLegacyId() {
		return _legacyIdGenerator.getAndIncrement();
	}

	private String _getFullAlias(
		String alias, HttpContextHelperFactory httpContextHelperFactory) {

		ContextController controller =
			_httpServiceRuntimeController.getContextController(
				httpContextHelperFactory.getServiceReference());

		if (controller == null) {
			return alias;
		}

		return controller.getContextPath() + alias;
	}

	private HttpContext _getOrCreateHttpContext(HttpContext httpContext) {
		if (httpContext == null) {
			return createDefaultHttpContext();
		}

		return httpContext;
	}

	private HttpContextHelperFactory _getOrRegisterHttpContextHelperFactory(
		HttpContext httpContext) {

		if (httpContext == null) {
			throw new NullPointerException("A null HttpContext is not allowed");
		}

		return _httpContextHelperFactoriesMap.computeIfAbsent(
			httpContext,
			key -> {
				HttpContextHelperFactory httpContextHelperFactory =
					new HttpContextHelperFactory(key);

				BundleContext bundleContext = _bundle.getBundleContext();

				httpContextHelperFactory.setRegistration(
					bundleContext.registerService(
						ServletContextHelper.class, httpContextHelperFactory,
						HashMapDictionaryBuilder.<String, Object>put(
							HttpWhiteboardConstants.
								HTTP_WHITEBOARD_CONTEXT_NAME,
							() -> {
								Class<?> clazz = key.getClass();

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

				httpContextHelperFactory.incrementUseCount();

				return httpContextHelperFactory;
			});
	}

	private void _registerHttpServiceResources(
			String alias, String name, HttpContext httpContext)
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
				 !pattern.endsWith(StringPool.SLASH)) {

			pattern += Const.SLASH_STAR;
		}

		ContextController.checkPattern(alias);

		synchronized (_legacyMappingsMap) {
			HttpServiceRegistrationBag httpServiceRegistrationBag = null;

			HttpContextHelperFactory httpContextHelperFactory =
				_getOrRegisterHttpContextHelperFactory(httpContext);

			try {
				String fullAlias = _getFullAlias(
					alias, httpContextHelperFactory);

				if (_legacyMappingsMap.containsKey(fullAlias)) {
					throw new PatternInUseException(alias);
				}

				BundleContext bundleContext = _bundle.getBundleContext();

				ServiceRegistration<?> serviceRegistration =
					bundleContext.registerService(
						Object.class, "resource",
						HashMapDictionaryBuilder.<String, Object>put(
							Constants.SERVICE_RANKING, Integer.MAX_VALUE
						).put(
							HttpServiceConstants.CONTEXT_CLASSLOADER,
							() -> {
								Thread currentThread = Thread.currentThread();

								return currentThread.getContextClassLoader();
							}
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

				httpServiceRegistrationBag = new HttpServiceRegistrationBag(
					fullAlias, serviceRegistration, httpContextHelperFactory);

				_httpServiceRegistrationBags.add(httpServiceRegistrationBag);

				_aliasCustomizationsMap.put(alias, fullAlias);

				_legacyMappingsMap.put(
					httpServiceRegistrationBag._serviceKey,
					httpServiceRegistrationBag);
			}
			finally {
				if ((httpServiceRegistrationBag == null) ||
					!_legacyMappingsMap.containsKey(
						httpServiceRegistrationBag._serviceKey)) {

					_decrementFactoryUseCount(httpContextHelperFactory);
				}
			}
		}
	}

	private void _registerHttpServiceServlet(
			String alias, Servlet servlet,
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

			if (alias.endsWith(StringPool.SLASH)) {
				pattern = new String[] {alias, alias + '*'};
			}
			else {
				pattern = new String[] {alias, alias + Const.SLASH_STAR};
			}
		}

		synchronized (_legacyMappingsMap) {
			LegacyServlet legacyServlet = new LegacyServlet(servlet);

			Set<Object> registeredObjects =
				_httpServiceRuntimeController.getRegisteredObjects();

			if (registeredObjects.contains(legacyServlet)) {
				throw new ServletAlreadyRegisteredException(servlet);
			}

			HttpServiceRegistrationBag httpServiceRegistrationBag = null;
			ServiceRegistration<Servlet> serviceRegistration = null;

			HttpContextHelperFactory httpContextHelperFactory =
				_getOrRegisterHttpContextHelperFactory(httpContext);

			try {
				String fullAlias = _getFullAlias(
					alias, httpContextHelperFactory);

				if (_legacyMappingsMap.containsKey(fullAlias)) {
					throw new PatternInUseException(alias);
				}

				Class<?> clazz = servlet.getClass();

				String servletName = clazz.getName();

				if ((initParams != null) &&
					(initParams.get(_SERVLET_NAME) != null)) {

					servletName = initParams.get(_SERVLET_NAME);
				}

				Dictionary<String, Object> dictionary =
					HashMapDictionaryBuilder.<String, Object>put(
						Constants.SERVICE_RANKING, Integer.MAX_VALUE
					).put(
						HttpServiceConstants.CONTEXT_CLASSLOADER,
						() -> {
							Thread currentThread = Thread.currentThread();

							return currentThread.getContextClassLoader();
						}
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

				_fillInitParams(dictionary, initParams);

				BundleContext bundleContext = _bundle.getBundleContext();

				serviceRegistration = bundleContext.registerService(
					Servlet.class, legacyServlet, dictionary);

				legacyServlet.checkForError();

				httpServiceRegistrationBag = new HttpServiceRegistrationBag(
					fullAlias, serviceRegistration, httpContextHelperFactory);

				_httpServiceRegistrationBags.add(httpServiceRegistrationBag);

				_aliasCustomizationsMap.put(alias, fullAlias);

				_legacyMappingsMap.put(
					httpServiceRegistrationBag._serviceKey,
					httpServiceRegistrationBag);
			}
			finally {
				if ((httpServiceRegistrationBag == null) ||
					!_legacyMappingsMap.containsKey(
						httpServiceRegistrationBag._serviceKey)) {

					_decrementFactoryUseCount(httpContextHelperFactory);

					if (serviceRegistration != null) {
						serviceRegistration.unregister();
					}
				}
			}
		}
	}

	private static final String _SERVLET_NAME = "servlet-name";

	private static final Log _log = LogFactoryUtil.getLog(
		HttpServiceImpl.class.getName());

	private final Map<String, String> _aliasCustomizationsMap =
		new ConcurrentHashMap<>();
	private final Bundle _bundle;
	private final Map<HttpContext, HttpContextHelperFactory>
		_httpContextHelperFactoriesMap = new ConcurrentHashMap<>();
	private final Set<HttpServiceRegistrationBag> _httpServiceRegistrationBags =
		Collections.newSetFromMap(new ConcurrentHashMap<>());
	private final HttpServiceRuntimeController _httpServiceRuntimeController;
	private final AtomicLong _legacyIdGenerator = new AtomicLong(0);
	private final Map<Object, HttpServiceRegistrationBag> _legacyMappingsMap =
		Collections.synchronizedMap(new HashMap<>());
	private boolean _shutdown;
	private final String _targetFilter;

	private static class HttpServiceRegistrationBag {

		public HttpServiceRegistrationBag(
			Object serviceKey, ServiceRegistration<?> serviceRegistration,
			HttpContextHelperFactory factory) {

			_serviceKey = serviceKey;
			_serviceRegistration = serviceRegistration;
			_factory = factory;
		}

		private final HttpContextHelperFactory _factory;
		private final Object _serviceKey;
		private final ServiceRegistration<?> _serviceRegistration;

	}

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

	private class DefaultHttpContext implements HttpContext {

		@Override
		public String getMimeType(String name) {
			return null;
		}

		@Override
		public URL getResource(String name) {
			if (name != null) {
				if (name.startsWith("/")) {
					name = name.substring(1);
				}

				return _bundle.getEntry(name);
			}

			return null;
		}

		@Override
		public boolean handleSecurity(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			return true;
		}

	}

}