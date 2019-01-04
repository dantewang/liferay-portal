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

package com.liferay.portal.util;

import com.liferay.portal.kernel.security.auth.AlwaysAllowDoAsUser;
import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upload.LiferayServletRequest;
import com.liferay.portal.upload.UploadServletRequestImpl;
import com.liferay.portal.util.test.PortletContainerTestUtil;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.portlet.MockPortletRequest;

/**
 * @author Peter Fellwock
 */
public class PortalImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestAlwaysAllowDoAsUser testAlwaysAllowDoAsUser =
			new TestAlwaysAllowDoAsUser();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			AlwaysAllowDoAsUser.class, testAlwaysAllowDoAsUser, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetOriginalServletRequest() {
		HttpServletRequest request = new MockHttpServletRequest();

		Assert.assertSame(
			request, PortalUtil.getOriginalServletRequest(request));

		HttpServletRequestWrapper requestWrapper1 =
			new HttpServletRequestWrapper(request);

		Assert.assertSame(
			request, PortalUtil.getOriginalServletRequest(requestWrapper1));

		HttpServletRequestWrapper requestWrapper2 =
			new HttpServletRequestWrapper(requestWrapper1);

		Assert.assertSame(
			request, PortalUtil.getOriginalServletRequest(requestWrapper2));

		HttpServletRequestWrapper requestWrapper3 =
			new PersistentHttpServletRequestWrapper1(requestWrapper2);

		HttpServletRequest originalRequest =
			PortalUtil.getOriginalServletRequest(requestWrapper3);

		Assert.assertSame(
			PersistentHttpServletRequestWrapper1.class,
			originalRequest.getClass());
		Assert.assertNotSame(requestWrapper3, originalRequest);
		Assert.assertSame(request, getWrappedRequest(originalRequest));

		HttpServletRequestWrapper requestWrapper4 =
			new PersistentHttpServletRequestWrapper2(requestWrapper3);

		originalRequest = PortalUtil.getOriginalServletRequest(requestWrapper4);

		Assert.assertSame(
			PersistentHttpServletRequestWrapper2.class,
			originalRequest.getClass());
		Assert.assertNotSame(requestWrapper4, originalRequest);

		originalRequest = getWrappedRequest(originalRequest);

		Assert.assertSame(
			PersistentHttpServletRequestWrapper1.class,
			originalRequest.getClass());
		Assert.assertNotSame(requestWrapper3, originalRequest);
		Assert.assertSame(request, getWrappedRequest(originalRequest));
	}

	@Test
	public void testGetUploadPortletRequestWithInvalidHttpServletRequest() {
		try {
			PortalUtil.getUploadPortletRequest(new MockPortletRequest());

			Assert.fail();
		}
		catch (Exception e) {
			Assert.assertTrue(e instanceof RuntimeException);
			Assert.assertEquals(
				"Unable to unwrap the portlet request from " +
					MockPortletRequest.class,
				e.getMessage());
		}
	}

	@Test
	public void testGetUploadPortletRequestWithValidHttpServletRequest()
		throws Exception {

		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"/com/liferay/portal/util/dependencies/test.txt");

		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				"fileParameterName", FileUtil.getBytes(inputStream));

		UploadServletRequest uploadServletRequest =
			PortalUtil.getUploadServletRequest(
				(HttpServletRequest)liferayServletRequest.getRequest());

		Assert.assertTrue(
			uploadServletRequest instanceof UploadServletRequestImpl);
	}

	@Test
	public void testGetUserId() {
		_called = false;

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"_TestAlwaysAllowDoAsUser_actionName",
			TestAlwaysAllowDoAsUser.ACTION_NAME);
		mockHttpServletRequest.setParameter(
			"_TestAlwaysAllowDoAsUser_struts_action",
			TestAlwaysAllowDoAsUser.STRUTS_ACTION);
		mockHttpServletRequest.setParameter("doAsUserId", "0");
		mockHttpServletRequest.setParameter(
			"p_p_id", "TestAlwaysAllowDoAsUser");

		long userId = PortalUtil.getUserId(mockHttpServletRequest);

		Assert.assertEquals(0, userId);

		Assert.assertTrue(_called);

		_called = false;

		mockHttpServletRequest = new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("doAsUserId", "0");
		mockHttpServletRequest.setPathInfo(
			"/TestAlwaysAllowDoAsUser/" + RandomTestUtil.randomString());

		userId = PortalUtil.getUserId(mockHttpServletRequest);

		Assert.assertEquals(0, userId);

		Assert.assertTrue(_called);
	}

	@Test
	public void testIsValidResourceId() {
		Assert.assertTrue(PortalUtil.isValidResourceId("/view.jsp"));
		Assert.assertFalse(
			PortalUtil.isValidResourceId("/META-INF/MANIFEST.MF"));
		Assert.assertFalse(
			PortalUtil.isValidResourceId("/META-INF\\MANIFEST.MF"));
		Assert.assertFalse(
			PortalUtil.isValidResourceId("\\META-INF/MANIFEST.MF"));
		Assert.assertFalse(
			PortalUtil.isValidResourceId("\\META-INF\\MANIFEST.MF"));
		Assert.assertFalse(PortalUtil.isValidResourceId("/WEB-INF/web.xml"));
		Assert.assertFalse(PortalUtil.isValidResourceId("/WEB-INF\\web.xml"));
		Assert.assertFalse(PortalUtil.isValidResourceId("\\WEB-INF/web.xml"));
		Assert.assertFalse(PortalUtil.isValidResourceId("\\WEB-INF\\web.xml"));
	}

	protected HttpServletRequest getWrappedRequest(
		HttpServletRequest requestRequest) {

		HttpServletRequestWrapper requestWrapper =
			(HttpServletRequestWrapper)requestRequest;

		return (HttpServletRequest)requestWrapper.getRequest();
	}

	private static boolean _called;
	private static ServiceRegistration<AlwaysAllowDoAsUser>
		_serviceRegistration;

	private static class PersistentHttpServletRequestWrapper1
		extends PersistentHttpServletRequestWrapper {

		private PersistentHttpServletRequestWrapper1(
			HttpServletRequest request) {

			super(request);
		}

	}

	private static class PersistentHttpServletRequestWrapper2
		extends PersistentHttpServletRequestWrapper {

		private PersistentHttpServletRequestWrapper2(
			HttpServletRequest request) {

			super(request);
		}

	}

	private static class TestAlwaysAllowDoAsUser
		implements AlwaysAllowDoAsUser {

		public static final String ACTION_NAME =
			"/TestAlwaysAllowDoAsUser/action/name";

		public static final String MVC_RENDER_COMMMAND_NAME =
			"/TestAlwaysAllowDoAsUser/mvc/render/command/name";

		public static final String PATH = "/TestAlwaysAllowDoAsUser/";

		public static final String STRUTS_ACTION =
			"/TestAlwaysAllowDoAsUser/struts/action";

		@Override
		public Collection<String> getActionNames() {
			_called = true;

			Collection<String> actionNames = new ArrayList<>();

			actionNames.add(ACTION_NAME);

			return actionNames;
		}

		@Override
		public Collection<String> getMVCRenderCommandNames() {
			_called = true;

			Collection<String> mvcRenderCommandNames = new ArrayList<>();

			mvcRenderCommandNames.add(MVC_RENDER_COMMMAND_NAME);

			return mvcRenderCommandNames;
		}

		@Override
		public Collection<String> getPaths() {
			_called = true;

			Collection<String> paths = new ArrayList<>();

			paths.add(PATH);

			return paths;
		}

		@Override
		public Collection<String> getStrutsActions() {
			_called = true;

			Collection<String> strutsActions = new ArrayList<>();

			strutsActions.add(STRUTS_ACTION);

			return strutsActions;
		}

	}

}