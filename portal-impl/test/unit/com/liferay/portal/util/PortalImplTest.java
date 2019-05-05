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
import com.liferay.portal.kernel.upload.UploadServletRequestConfigurationHelper;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.upload.LiferayServletRequest;
import com.liferay.portal.upload.UploadServletRequestImpl;
import com.liferay.portal.util.test.PortletContainerTestUtil;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.io.InputStream;

import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.portlet.MockPortletRequest;

/**
 * @author Leon Chi
 */
public class PortalImplTest {

	@BeforeClass
	public static void setUpClass() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());
	}

	@Test
	public void testGetOriginalServletRequest() {
		HttpServletRequest request = new MockHttpServletRequest();

		Assert.assertSame(
			request, _portalImpl.getOriginalServletRequest(request));

		HttpServletRequestWrapper requestWrapper1 =
			new HttpServletRequestWrapper(request);

		Assert.assertSame(
			request, _portalImpl.getOriginalServletRequest(requestWrapper1));

		HttpServletRequestWrapper requestWrapper2 =
			new HttpServletRequestWrapper(requestWrapper1);

		Assert.assertSame(
			request, _portalImpl.getOriginalServletRequest(requestWrapper2));

		HttpServletRequestWrapper requestWrapper3 =
			new PersistentHttpServletRequestWrapper1(requestWrapper2);

		HttpServletRequest originalRequest =
			_portalImpl.getOriginalServletRequest(requestWrapper3);

		Assert.assertSame(
			PersistentHttpServletRequestWrapper1.class,
			originalRequest.getClass());
		Assert.assertNotSame(requestWrapper3, originalRequest);
		Assert.assertSame(request, getWrappedRequest(originalRequest));

		HttpServletRequestWrapper requestWrapper4 =
			new PersistentHttpServletRequestWrapper2(requestWrapper3);

		originalRequest = _portalImpl.getOriginalServletRequest(
			requestWrapper4);

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
			_portalImpl.getUploadPortletRequest(new MockPortletRequest());

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

		Registry registry = RegistryUtil.getRegistry();

		FileUtil fileUtil = new FileUtil();

		fileUtil.setFile(new FileImpl());

		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"/com/liferay/portal/util/dependencies/test.txt");

		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				"fileParameterName", FileUtil.getBytes(inputStream));

		ServiceRegistration<UploadServletRequestConfigurationHelper>
			serviceRegistration = registry.registerService(
				UploadServletRequestConfigurationHelper.class,
				new UploadServletRequestConfigurationHelper() {

					@Override
					public long getMaxSize() {
						return 0;
					}

					@Override
					public String getTempDir() {
						return System.getProperty(SystemProperties.TMP_DIR);
					}

				});

		try {
			UploadServletRequest uploadServletRequest =
				_portalImpl.getUploadServletRequest(
					(HttpServletRequest)liferayServletRequest.getRequest());

			Assert.assertTrue(
				uploadServletRequest instanceof UploadServletRequestImpl);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetUserId() {
		PropsUtil.setProps(new PropsImpl());

		Registry registry = RegistryUtil.getRegistry();

		boolean[] calledAlwaysAllowDoAsUser = {false};

		ServiceRegistration<AlwaysAllowDoAsUser> serviceRegistration =
			registry.registerService(
				AlwaysAllowDoAsUser.class,
				new AlwaysAllowDoAsUser() {

					@Override
					public Collection<String> getActionNames() {
						calledAlwaysAllowDoAsUser[0] = true;

						return Collections.singletonList(_ACTION_NAME);
					}

					@Override
					public Collection<String> getMVCRenderCommandNames() {
						calledAlwaysAllowDoAsUser[0] = true;

						return Collections.singletonList(
							_MVC_RENDER_COMMMAND_NAME);
					}

					@Override
					public Collection<String> getPaths() {
						calledAlwaysAllowDoAsUser[0] = true;

						return Collections.singletonList(_PATH);
					}

					@Override
					public Collection<String> getStrutsActions() {
						calledAlwaysAllowDoAsUser[0] = true;

						return Collections.singletonList(_STRUTS_ACTION);
					}

				});

		try {
			MockHttpServletRequest mockHttpServletRequest1 =
				new MockHttpServletRequest();

			mockHttpServletRequest1.setParameter(
				"_TestAlwaysAllowDoAsUser_actionName", _ACTION_NAME);
			mockHttpServletRequest1.setParameter(
				"_TestAlwaysAllowDoAsUser_struts_action", _STRUTS_ACTION);
			mockHttpServletRequest1.setParameter("doAsUserId", "1");
			mockHttpServletRequest1.setParameter(
				"p_p_id", "TestAlwaysAllowDoAsUser");

			Assert.assertEquals(
				0, _portalImpl.getUserId(mockHttpServletRequest1));

			Assert.assertTrue(
				"AlwaysAllowDoAsUser not called", calledAlwaysAllowDoAsUser[0]);

			calledAlwaysAllowDoAsUser[0] = false;

			MockHttpServletRequest mockHttpServletRequest2 =
				new MockHttpServletRequest();

			mockHttpServletRequest2.setParameter("doAsUserId", "1");
			mockHttpServletRequest2.setPathInfo(
				_PATH + RandomTestUtil.randomString());

			Assert.assertEquals(
				0, _portalImpl.getUserId(mockHttpServletRequest2));

			Assert.assertTrue(
				"AlwaysAllowDoAsUser not called", calledAlwaysAllowDoAsUser[0]);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testIsValidResourceId() {
		Assert.assertTrue(_portalImpl.isValidResourceId("/view.jsp"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("/META-INF/MANIFEST.MF"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("/META-INF\\MANIFEST.MF"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("\\META-INF/MANIFEST.MF"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("\\META-INF\\MANIFEST.MF"));
		Assert.assertFalse(_portalImpl.isValidResourceId("/WEB-INF/web.xml"));
		Assert.assertFalse(_portalImpl.isValidResourceId("/WEB-INF\\web.xml"));
		Assert.assertFalse(_portalImpl.isValidResourceId("\\WEB-INF/web.xml"));
		Assert.assertFalse(_portalImpl.isValidResourceId("\\WEB-INF\\web.xml"));
	}

	protected HttpServletRequest getWrappedRequest(
		HttpServletRequest httpServletRequest) {

		HttpServletRequestWrapper requestWrapper =
			(HttpServletRequestWrapper)httpServletRequest;

		return (HttpServletRequest)requestWrapper.getRequest();
	}

	private static final String _ACTION_NAME =
		"/TestAlwaysAllowDoAsUser/action/name";

	private static final String _MVC_RENDER_COMMMAND_NAME =
		"/TestAlwaysAllowDoAsUser/mvc/render/command/name";

	private static final String _PATH = "/TestAlwaysAllowDoAsUser/";

	private static final String _STRUTS_ACTION =
		"/TestAlwaysAllowDoAsUser/struts/action";

	private final PortalImpl _portalImpl = new PortalImpl();

	private class PersistentHttpServletRequestWrapper1
		extends PersistentHttpServletRequestWrapper {

		private PersistentHttpServletRequestWrapper1(
			HttpServletRequest httpServletRequest) {

			super(httpServletRequest);
		}

	}

	private class PersistentHttpServletRequestWrapper2
		extends PersistentHttpServletRequestWrapper {

		private PersistentHttpServletRequestWrapper2(
			HttpServletRequest httpServletRequest) {

			super(httpServletRequest);
		}

	}

}