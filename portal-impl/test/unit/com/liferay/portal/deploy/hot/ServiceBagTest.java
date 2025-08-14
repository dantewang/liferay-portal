/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.deploy.hot;

import com.liferay.portal.kernel.bean.ClassLoaderBeanHandler;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceWrapper;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.service.impl.LayoutLocalServiceImpl;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Kevin Lee
 */
public class ServiceBagTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testReplace() {
		LayoutLocalService service = new LayoutLocalServiceImpl();

		TestAopInvocationHandler testAopInvocationHandler =
			new TestAopInvocationHandler();

		ServiceBag<LayoutLocalService> serviceBag = new ServiceBag<>(
			testAopInvocationHandler, LayoutLocalService.class,
			new LayoutLocalServiceWrapper(service),
			Mockito.mock(BundleContext.class),
			Mockito.mock(ServiceReference.class));

		testAopInvocationHandler.setTarget(
			ProxyUtil.newProxyInstance(
				ServiceBagTest.class.getClassLoader(),
				new Class<?>[] {LayoutLocalService.class},
				new ClassLoaderBeanHandler(
					testAopInvocationHandler.getTarget(),
					ServiceBagTest.class.getClassLoader())));

		serviceBag.replace();

		Assert.assertSame(service, testAopInvocationHandler.getTarget());
	}

	@Test
	public void testReplaceWithComplexChain() {
		LayoutLocalService service = new LayoutLocalServiceImpl();

		TestAopInvocationHandler testAopInvocationHandler =
			new TestAopInvocationHandler();

		ServiceBag<LayoutLocalService> serviceBag = new ServiceBag<>(
			testAopInvocationHandler, LayoutLocalService.class,
			new LayoutLocalServiceWrapper(service),
			Mockito.mock(BundleContext.class),
			Mockito.mock(ServiceReference.class));

		Object proxy = ProxyUtil.newProxyInstance(
			ServiceBagTest.class.getClassLoader(),
			new Class<?>[] {LayoutLocalService.class},
			new ClassLoaderBeanHandler(
				testAopInvocationHandler.getTarget(),
				ServiceBagTest.class.getClassLoader()));

		testAopInvocationHandler.setTarget(proxy);

		new ServiceBag<>(
			testAopInvocationHandler, LayoutLocalService.class,
			new LayoutLocalServiceWrapper((LayoutLocalService)proxy),
			Mockito.mock(BundleContext.class),
			Mockito.mock(ServiceReference.class));

		serviceBag.replace();

		Object target = testAopInvocationHandler.getTarget();

		Assert.assertTrue(target instanceof ServiceWrapper);

		ServiceWrapper<LayoutLocalService> serviceWrapper =
			(ServiceWrapper<LayoutLocalService>)target;

		LayoutLocalService wrappedService = serviceWrapper.getWrappedService();

		Assert.assertSame(proxy, wrappedService);

		ClassLoaderBeanHandler classLoaderBeanHandler =
			ProxyUtil.fetchInvocationHandler(
				wrappedService, ClassLoaderBeanHandler.class);

		Assert.assertSame(service, classLoaderBeanHandler.getTarget());
	}

	private static class TestAopInvocationHandler extends AopInvocationHandler {

		public TestAopInvocationHandler() {
			super(null, null, null);
		}

	}

}