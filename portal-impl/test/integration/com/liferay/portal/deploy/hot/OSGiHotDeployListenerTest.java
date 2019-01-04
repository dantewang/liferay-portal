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

package com.liferay.portal.deploy.hot;

import com.liferay.portal.kernel.deploy.hot.DependencyManagementThreadLocal;
import com.liferay.portal.kernel.deploy.hot.HotDeployEvent;
import com.liferay.portal.kernel.deploy.hot.HotDeployException;
import com.liferay.portal.kernel.deploy.hot.HotDeployListener;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import javax.servlet.ServletContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Adolfo Pérez
 */
public class OSGiHotDeployListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestHotDeployListener testHotDeployListener =
			new TestHotDeployListener();

		Registry registry = RegistryUtil.getRegistry();

		_serviceRegistration = registry.registerService(
			HotDeployListener.class, testHotDeployListener);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Before
	public void setUp() {
		_dependencyManagerEnabled = DependencyManagementThreadLocal.isEnabled();

		DependencyManagementThreadLocal.setEnabled(false);

		_called = false;
	}

	@After
	public void tearDown() {
		DependencyManagementThreadLocal.setEnabled(_dependencyManagerEnabled);
	}

	@Test
	public void testInvokeDeploy() throws HotDeployException {
		_hotDeployListener.invokeDeploy(
			new HotDeployEvent(_servletContext, null));

		Assert.assertTrue(_called);
	}

	@Test
	public void testInvokeUndeploy() throws HotDeployException {
		_hotDeployListener.invokeUndeploy(
			new HotDeployEvent(_servletContext, null));

		Assert.assertTrue(_called);
	}

	private static boolean _called;
	private static ServiceRegistration<HotDeployListener> _serviceRegistration;

	private boolean _dependencyManagerEnabled;
	private final HotDeployListener _hotDeployListener =
		new OSGiHotDeployListener();
	private final ServletContext _servletContext =
		ProxyFactory.newDummyInstance(ServletContext.class);

	private static class TestHotDeployListener implements HotDeployListener {

		@Override
		public void invokeDeploy(HotDeployEvent event) {
			_called = true;
		}

		@Override
		public void invokeUndeploy(HotDeployEvent event) {
			_called = true;
		}

	}

}