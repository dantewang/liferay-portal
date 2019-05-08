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

package com.liferay.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.InvokerFilterContainer;
import com.liferay.portal.kernel.portlet.PortletInstanceFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.ServletContextClassLoaderPool;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.PortletAppImpl;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.EventFilter;
import javax.portlet.filter.PortletFilter;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Leon Chi
 */
@RunWith(Arquillian.class)
public class InvokerFilterContainerImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws PortletException {
		String servletContextName =
			ServletContextClassLoaderPool.getServletContextName(
				PortalClassLoaderUtil.getClassLoader());

		PortletAppImpl portletAppImpl = new PortletAppImpl(servletContextName);

		portletAppImpl.setWARFile(false);

		_portlet = new PortletImpl();

		_portlet.setPortletApp(portletAppImpl);
		_portlet.setPortletClass(MVCPortlet.class.getName());
		_portlet.setPortletId("InvokerFilterContainerImplTest");

		_invokerFilterContainer =
			(InvokerFilterContainer)_portletInstanceFactory.create(
				_portlet, ServletContextPool.get(servletContextName), true);
	}

	@AfterClass
	public static void tearDownClass() {
		_portletInstanceFactory.destroy(_portlet);
	}

	@Test
	public void testGetEventFilters() {
		Bundle bundle = FrameworkUtil.getBundle(
			InvokerFilterContainerImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<PortletFilter> eventFilterServiceRegistration =
			bundleContext.registerService(
				PortletFilter.class,
				(EventFilter)ProxyUtil.newProxyInstance(
					EventFilter.class.getClassLoader(),
					new Class<?>[] {EventFilter.class},
					(proxy, method, args) -> {
						if ("equals".equals(method.getName())) {
							return proxy == args[0];
						}

						if ("hashCode".equals(method.getName())) {
							return hashCode();
						}

						return null;
					}),
				new HashMapDictionary<String, Object>() {
					{
						put(
							"javax.portlet.name",
							"InvokerFilterContainerImplTest");
						put("preinitialized.filter", "true");
						put("service.ranking", Integer.MAX_VALUE);
					}
				});

		try {
			List<EventFilter> eventFilters =
				_invokerFilterContainer.getEventFilters();

			Assert.assertTrue(
				"Target not found in " + eventFilters,
				eventFilters.removeIf(
					eventFilter ->
						bundleContext.getService(
							eventFilterServiceRegistration.getReference()) ==
								eventFilter));
		}
		finally {
			eventFilterServiceRegistration.unregister();
		}
	}

	@Test
	public void testGetRenderFilters() {
		Bundle bundle = FrameworkUtil.getBundle(
			InvokerFilterContainerImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<PortletFilter> renderFilterServiceRegistration =
			bundleContext.registerService(
				PortletFilter.class,
				(RenderFilter)ProxyUtil.newProxyInstance(
					RenderFilter.class.getClassLoader(),
					new Class<?>[] {RenderFilter.class},
					(proxy, method, args) -> {
						if ("equals".equals(method.getName())) {
							return proxy == args[0];
						}

						if ("hashCode".equals(method.getName())) {
							return hashCode();
						}

						return null;
					}),
				new HashMapDictionary<String, Object>() {
					{
						put(
							"javax.portlet.name",
							"InvokerFilterContainerImplTest");
						put("preinitialized.filter", "true");
						put("service.ranking", Integer.MAX_VALUE);
					}
				});

		try {
			List<RenderFilter> renderFilters =
				_invokerFilterContainer.getRenderFilters();

			Assert.assertTrue(
				"Target not found in " + renderFilters,
				renderFilters.removeIf(
					renderFilter ->
						bundleContext.getService(
							renderFilterServiceRegistration.getReference()) ==
								renderFilter));
		}
		finally {
			renderFilterServiceRegistration.unregister();
		}
	}

	@Test
	public void testGetResourceFilters() {
		Bundle bundle = FrameworkUtil.getBundle(
			InvokerFilterContainerImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<PortletFilter> resourceFilterServiceRegistration =
			bundleContext.registerService(
				PortletFilter.class,
				(ResourceFilter)ProxyUtil.newProxyInstance(
					ResourceFilter.class.getClassLoader(),
					new Class<?>[] {ResourceFilter.class},
					(proxy, method, args) -> {
						if ("equals".equals(method.getName())) {
							return proxy == args[0];
						}

						if ("hashCode".equals(method.getName())) {
							return hashCode();
						}

						return null;
					}),
				new HashMapDictionary<String, Object>() {
					{
						put(
							"javax.portlet.name",
							"InvokerFilterContainerImplTest");
						put("preinitialized.filter", "true");
						put("service.ranking", Integer.MAX_VALUE);
					}
				});

		try {
			List<ResourceFilter> resourceFilters =
				_invokerFilterContainer.getResourceFilters();

			Assert.assertTrue(
				"Target not found in " + resourceFilters,
				resourceFilters.removeIf(
					resourceFilter ->
						bundleContext.getService(
							resourceFilterServiceRegistration.getReference()) ==
								resourceFilter));
		}
		finally {
			resourceFilterServiceRegistration.unregister();
		}
	}

	@Test
	public void testInitAndGetActionFilters() {
		boolean[] calledInit = {false};

		Bundle bundle = FrameworkUtil.getBundle(
			InvokerFilterContainerImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<PortletFilter> actionFilterServiceRegistration =
			bundleContext.registerService(
				PortletFilter.class,
				(ActionFilter)ProxyUtil.newProxyInstance(
					ActionFilter.class.getClassLoader(),
					new Class<?>[] {ActionFilter.class},
					(proxy, method, args) -> {
						if ("init".equals(method.getName())) {
							calledInit[0] = true;
						}

						if ("equals".equals(method.getName())) {
							return proxy == args[0];
						}

						if ("hashCode".equals(method.getName())) {
							return hashCode();
						}

						return null;
					}),
				new HashMapDictionary<String, Object>() {
					{
						put(
							"javax.portlet.name",
							"InvokerFilterContainerImplTest");
						put("preinitialized.filter", "false");
						put("service.ranking", Integer.MAX_VALUE);
					}
				});

		try {
			Assert.assertTrue(calledInit[0]);

			List<ActionFilter> actionFilters =
				_invokerFilterContainer.getActionFilters();

			Assert.assertTrue(
				"Target not found in " + actionFilters,
				actionFilters.removeIf(
					actionFilter ->
						bundleContext.getService(
							actionFilterServiceRegistration.getReference()) ==
								actionFilter));
		}
		finally {
			actionFilterServiceRegistration.unregister();
		}
	}

	private static InvokerFilterContainer _invokerFilterContainer;
	private static Portlet _portlet;

	@Inject
	private static PortletInstanceFactory _portletInstanceFactory;

}