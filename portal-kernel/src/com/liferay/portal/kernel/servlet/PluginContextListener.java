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

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.deploy.hot.HotDeployEvent;
import com.liferay.portal.kernel.deploy.hot.HotDeployUtil;
import com.liferay.portal.kernel.util.BasePortalLifecycle;
import com.liferay.portal.kernel.util.ServletContextClassLoaderPool;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Brian Wing Shun Chan
 */
public class PluginContextListener
	extends BasePortalLifecycle
	implements ServletContextAttributeListener, ServletContextListener {

	/**
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 *             ServletContextClassLoaderPool#getClassLoader(String)}
	 */
	@Deprecated
	public static final String PLUGIN_CLASS_LOADER = "PLUGIN_CLASS_LOADER";

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public void attributeAdded(
		ServletContextAttributeEvent servletContextAttributeEvent) {
	}

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public void attributeRemoved(
		ServletContextAttributeEvent servletContextAttributeEvent) {
	}

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public void attributeReplaced(
		ServletContextAttributeEvent servletContextAttributeEvent) {
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();

		ServletContextClassLoaderPool.unregister(
			servletContext.getServletContextName());

		portalDestroy();
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContext = servletContextEvent.getServletContext();

		Thread currentThread = Thread.currentThread();

		pluginClassLoader = currentThread.getContextClassLoader();

		ServletContextClassLoaderPool.register(
			servletContext.getServletContextName(), pluginClassLoader);

		ServletContextPool.put(
			servletContext.getServletContextName(), servletContext);

		registerPortalLifecycle();
	}

	@Override
	protected void doPortalDestroy() throws Exception {
		PluginContextLifecycleThreadLocal.setDestroying(true);

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		if (contextClassLoader != pluginClassLoader) {
			currentThread.setContextClassLoader(pluginClassLoader);
		}

		try {
			fireUndeployEvent();
		}
		finally {
			PluginContextLifecycleThreadLocal.setDestroying(false);

			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	@Override
	protected void doPortalInit() throws Exception {
		PluginContextLifecycleThreadLocal.setInitializing(true);

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		if (contextClassLoader != pluginClassLoader) {
			currentThread.setContextClassLoader(pluginClassLoader);
		}

		try {
			fireDeployEvent();
		}
		finally {
			PluginContextLifecycleThreadLocal.setInitializing(false);

			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	protected void fireDeployEvent() {
		HotDeployUtil.fireDeployEvent(
			new HotDeployEvent(servletContext, pluginClassLoader));
	}

	protected void fireUndeployEvent() {
		HotDeployUtil.fireUndeployEvent(
			new HotDeployEvent(servletContext, pluginClassLoader));
	}

	protected ClassLoader pluginClassLoader;
	protected ServletContext servletContext;

}