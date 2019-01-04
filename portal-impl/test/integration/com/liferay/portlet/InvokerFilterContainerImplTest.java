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

package com.liferay.portlet;

import com.liferay.portal.internal.servlet.MainServlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletContextFactory;
import com.liferay.portal.kernel.portlet.PortletContextFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.model.impl.PortletAppImpl;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.callback.MainServletTestCallback;
import com.liferay.portlet.internal.InvokerFilterContainerImpl;
import com.liferay.portlet.internal.PortletContextFactoryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.EventFilter;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.PortletFilter;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;

import javax.servlet.ServletContext;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Philip Jones
 * @author Peter Fellwock
 */
public class InvokerFilterContainerImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Registry registry = RegistryUtil.getRegistry();

		TestActionFilter testActionFilter = new TestActionFilter();

		Map<String, Object> properties1 = new HashMap<>();

		properties1.put("javax.portlet.name", "InvokerFilterContainerImplTest");
		properties1.put("preinitialized.filter", "false");
		properties1.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration1 = registry.registerService(
			PortletFilter.class, testActionFilter, properties1);

		TestEventFilter testEventFilter = new TestEventFilter();

		Map<String, Object> properties2 = new HashMap<>();

		properties2.put("javax.portlet.name", "InvokerFilterContainerImplTest");
		properties2.put("preinitialized.filter", "true");
		properties2.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration2 = registry.registerService(
			PortletFilter.class, testEventFilter, properties2);

		TestRenderFilter testRenderFilter = new TestRenderFilter();

		Map<String, Object> properties3 = new HashMap<>();

		properties3.put("javax.portlet.name", "InvokerFilterContainerImplTest");
		properties3.put("preinitialized.filter", "true");
		properties3.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration3 = registry.registerService(
			PortletFilter.class, testRenderFilter, properties3);

		TestResourceFilter testResourceFilter = new TestResourceFilter();

		Map<String, Object> properties4 = new HashMap<>();

		properties4.put("javax.portlet.name", "InvokerFilterContainerImplTest");
		properties4.put("preinitialized.filter", "true");
		properties4.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration4 = registry.registerService(
			PortletFilter.class, testResourceFilter, properties4);

		PortletContextFactory portletContextFactory =
			new PortletContextFactoryImpl();

		PortletContextFactoryUtil portletContextFactoryUtil =
			new PortletContextFactoryUtil();

		portletContextFactoryUtil.setPortletContextFactory(
			portletContextFactory);

		MainServlet mainServlet = MainServletTestCallback.getMainServlet();

		ServletContext servletContext = mainServlet.getServletContext();

		String servletContextName = servletContext.getServletContextName();

		PortletAppImpl portletAppImpl = new PortletAppImpl(servletContextName);

		portletAppImpl.setWARFile(false);

		Portlet portlet = new PortletImpl();

		portlet.setPortletApp(portletAppImpl);
		portlet.setPortletClass(MVCPortlet.class.getName());
		portlet.setPortletId("InvokerFilterContainerImplTest");

		PortletContext portletContext = PortletContextFactoryUtil.create(
			portlet, servletContext);

		try {
			_invokerFilterContainerImpl = new InvokerFilterContainerImpl(
				portlet, portletContext);
		}
		catch (PortletException pe) {
		}
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration1.unregister();
		_serviceRegistration2.unregister();
		_serviceRegistration3.unregister();
		_serviceRegistration4.unregister();
	}

	@Test
	public void testGetActionFilters() {
		boolean found = false;

		List<ActionFilter> actionFilters =
			_invokerFilterContainerImpl.getActionFilters();

		for (ActionFilter actionFilter : actionFilters) {
			Class<?> clazz = actionFilter.getClass();

			String className = clazz.getName();

			if (className.equals(TestActionFilter.class.getName())) {
				found = true;

				break;
			}
		}

		Assert.assertTrue("Not found " + TestActionFilter.class, found);
	}

	@Test
	public void testGetEventFilters() {
		boolean found = false;

		List<EventFilter> eventFilters =
			_invokerFilterContainerImpl.getEventFilters();

		for (EventFilter eventFilter : eventFilters) {
			Class<?> clazz = eventFilter.getClass();

			String className = clazz.getName();

			if (className.equals(TestEventFilter.class.getName())) {
				found = true;

				break;
			}
		}

		Assert.assertTrue("Not found " + TestEventFilter.class, found);
	}

	@Test
	public void testGetRenderFilters() {
		boolean found = false;

		List<RenderFilter> renderFilters =
			_invokerFilterContainerImpl.getRenderFilters();

		for (RenderFilter renderFilter : renderFilters) {
			Class<?> clazz = renderFilter.getClass();

			String className = clazz.getName();

			if (className.equals(TestRenderFilter.class.getName())) {
				found = true;

				break;
			}
		}

		Assert.assertTrue("Not found " + TestRenderFilter.class, found);
	}

	@Test
	public void testGetResourceFilters() {
		boolean found = false;

		List<ResourceFilter> resourceFilters =
			_invokerFilterContainerImpl.getResourceFilters();

		for (ResourceFilter resourceFilter : resourceFilters) {
			Class<?> clazz = resourceFilter.getClass();

			String className = clazz.getName();

			if (className.equals(TestResourceFilter.class.getName())) {
				found = true;

				break;
			}
		}

		Assert.assertTrue("Not found " + TestResourceFilter.class, found);
	}

	@Test
	public void testInit() {
		Assert.assertTrue(_called);
	}

	private static boolean _called;
	private static InvokerFilterContainerImpl _invokerFilterContainerImpl;
	private static ServiceRegistration<PortletFilter> _serviceRegistration1;
	private static ServiceRegistration<PortletFilter> _serviceRegistration2;
	private static ServiceRegistration<PortletFilter> _serviceRegistration3;
	private static ServiceRegistration<PortletFilter> _serviceRegistration4;

	private static class TestActionFilter implements ActionFilter {

		@Override
		public void destroy() {
		}

		@Override
		public void doFilter(
			ActionRequest actionRequest, ActionResponse actionResponse,
			FilterChain filterChain) {
		}

		@Override
		public void init(FilterConfig filterConfig) {
			_called = true;
		}

	}

	private static class TestEventFilter implements EventFilter {

		@Override
		public void destroy() {
		}

		@Override
		public void doFilter(
			EventRequest eventRequest, EventResponse eventResponse,
			FilterChain filterChain) {
		}

		@Override
		public void init(FilterConfig filterConfig) {
		}

	}

	private static class TestRenderFilter implements RenderFilter {

		@Override
		public void destroy() {
		}

		@Override
		public void doFilter(
			RenderRequest renderRequest, RenderResponse renderResponse,
			FilterChain filterChain) {
		}

		@Override
		public void init(FilterConfig filterConfig) {
		}

	}

	private static class TestResourceFilter implements ResourceFilter {

		@Override
		public void destroy() {
		}

		@Override
		public void doFilter(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			FilterChain filterChain) {
		}

		@Override
		public void init(FilterConfig filterConfig) {
		}

	}

}