/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.deploy.hot;

import com.liferay.portal.kernel.bean.ClassLoaderBeanHandler;
import com.liferay.portal.kernel.proxy.TargetInvocationHandler;
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

import java.lang.reflect.Method;

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
			new TestAopInvocationHandler(service);

		ServiceBag<LayoutLocalService> serviceBag = new ServiceBag<>(
			testAopInvocationHandler, LayoutLocalService.class,
			new LayoutLocalServiceWrapper(service),
			Mockito.mock(BundleContext.class),
			Mockito.mock(ServiceReference.class));

		TestTargetInvocationHandler testTargetInvocationHandler =
			new TestTargetInvocationHandler(
				testAopInvocationHandler.getTarget());

		Object proxy = ProxyUtil.newProxyInstance(
			ServiceBagTest.class.getClassLoader(),
			new Class<?>[] {LayoutLocalService.class},
			testTargetInvocationHandler);

		testAopInvocationHandler.setTarget(proxy);

		serviceBag.replace();

		Assert.assertSame(proxy, testAopInvocationHandler.getTarget());
		Assert.assertSame(service, testTargetInvocationHandler.getTarget());
	}

	@Test
	public void testReplaceWithComplexChain() {
		LayoutLocalService service = new LayoutLocalServiceImpl();

		TestAopInvocationHandler testAopInvocationHandler =
			new TestAopInvocationHandler(service);

		ServiceBag<LayoutLocalService> serviceBag = new ServiceBag<>(
			testAopInvocationHandler, LayoutLocalService.class,
			new LayoutLocalServiceWrapper(service),
			Mockito.mock(BundleContext.class),
			Mockito.mock(ServiceReference.class));

		TestTargetInvocationHandler testTargetInvocationHandler =
			new TestTargetInvocationHandler(
				testAopInvocationHandler.getTarget());

		Object proxy = ProxyUtil.newProxyInstance(
			ServiceBagTest.class.getClassLoader(),
			new Class<?>[] {LayoutLocalService.class},
			testTargetInvocationHandler);

		testAopInvocationHandler.setTarget(proxy);

		new ServiceBag<>(
			testAopInvocationHandler, LayoutLocalService.class,
			new LayoutLocalServiceWrapper(
				(LayoutLocalService)testAopInvocationHandler.getTarget()),
			Mockito.mock(BundleContext.class),
			Mockito.mock(ServiceReference.class));

		serviceBag.replace();

		Object target = testAopInvocationHandler.getTarget();

		Assert.assertTrue(target instanceof ServiceWrapper);

		ServiceWrapper<LayoutLocalService> serviceWrapper =
			(ServiceWrapper<LayoutLocalService>)target;

		LayoutLocalService wrappedService = serviceWrapper.getWrappedService();

		ClassLoaderBeanHandler classLoaderBeanHandler =
			ProxyUtil.fetchInvocationHandler(
				wrappedService, ClassLoaderBeanHandler.class);

		Assert.assertNotEquals(null, classLoaderBeanHandler);

		Assert.assertSame(proxy, classLoaderBeanHandler.getBean());

		classLoaderBeanHandler =
			ProxyUtil.fetchInvocationHandler(
				testTargetInvocationHandler.getTarget(),
				ClassLoaderBeanHandler.class);

		Assert.assertSame(service, classLoaderBeanHandler.getBean());
	}

	private static class TestAopInvocationHandler extends AopInvocationHandler {

		public TestAopInvocationHandler(Object target) {
			super(target, null, null);
		}

	}

	private static class TestTargetInvocationHandler implements TargetInvocationHandler {

		public TestTargetInvocationHandler(Object target) {
			_target = target;
		}

		@Override
		public Object getTarget() {
			return _target;
		}

		@Override
		public void setTarget(Object target) {
			_target = target;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}

		private Object _target;

	}

}