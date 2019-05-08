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

package com.liferay.portal.portlet.bridge.mvc.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.IOException;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import javax.portlet.ActionParameters;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletResponse;
import javax.portlet.RenderParameters;
import javax.portlet.WindowState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import javax.xml.namespace.QName;

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

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.portlet.MockActionRequest;
import org.springframework.mock.web.portlet.MockActionResponse;
import org.springframework.mock.web.portlet.MockPortletConfig;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class MVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(MVCActionCommandTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_portletServiceRegistration = bundleContext.registerService(
			javax.portlet.Portlet.class, new TestPortlet(),
			new HashMapDictionary<String, Object>() {
				{
					put(
						"javax.portlet.init-param.copy-request-parameters",
						"false");
					put("javax.portlet.name", TestPortlet.PORTLET_NAME);
				}
			});

		_mvcActionCommandServiceRegistration1 = bundleContext.registerService(
			MVCActionCommand.class, new TestMVCActionCommand1(),
			new HashMapDictionary<String, Object>() {
				{
					put("javax.portlet.name", TestPortlet.PORTLET_NAME);
					put(
						"mvc.command.name",
						TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_NAME);
				}
			});

		_mvcActionCommandServiceRegistration2 = bundleContext.registerService(
			MVCActionCommand.class, new TestMVCActionCommand2(),
			new HashMapDictionary<String, Object>() {
				{
					put("javax.portlet.name", TestPortlet.PORTLET_NAME);
					put(
						"mvc.command.name",
						TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_NAME);
				}
			});
	}

	@AfterClass
	public static void tearDownClass() {
		_portletServiceRegistration.unregister();
		_mvcActionCommandServiceRegistration1.unregister();
		_mvcActionCommandServiceRegistration2.unregister();
	}

	@Test
	public void testMultipleMVCActionCommandsWithMultipleParameters()
		throws Exception {

		MockActionRequest mockActionRequest = new MockLiferayPortletRequest();

		mockActionRequest.addParameter(
			ActionRequest.ACTION_NAME,
			TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_NAME);
		mockActionRequest.addParameter(
			ActionRequest.ACTION_NAME,
			TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_NAME);

		_portlet.processAction(mockActionRequest, new MockActionResponse());

		Assert.assertNotNull(
			mockActionRequest.getAttribute(
				TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
		Assert.assertEquals(
			TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE,
			mockActionRequest.getAttribute(
				TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
		Assert.assertNotNull(
			mockActionRequest.getAttribute(
				TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
		Assert.assertEquals(
			TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_ATTRIBUTE,
			mockActionRequest.getAttribute(
				TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
	}

	@Test
	public void testMultipleMVCActionCommandsWithSingleParameter()
		throws Exception {

		MockActionRequest mockActionRequest = new MockLiferayPortletRequest();

		mockActionRequest.addParameter(
			ActionRequest.ACTION_NAME,
			TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_NAME +
				StringPool.COMMA +
					TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_NAME);

		_portlet.processAction(mockActionRequest, new MockActionResponse());

		Assert.assertNotNull(
			mockActionRequest.getAttribute(
				TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
		Assert.assertEquals(
			TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE,
			mockActionRequest.getAttribute(
				TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
		Assert.assertNotNull(
			mockActionRequest.getAttribute(
				TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
		Assert.assertEquals(
			TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_ATTRIBUTE,
			mockActionRequest.getAttribute(
				TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
	}

	@Test
	public void testSingleMVCActionCommand() throws Exception {
		MockActionRequest mockActionRequest = new MockLiferayPortletRequest();

		mockActionRequest.addParameter(
			ActionRequest.ACTION_NAME,
			TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_NAME);

		_portlet.processAction(mockActionRequest, new MockActionResponse());

		Assert.assertNotNull(
			mockActionRequest.getAttribute(
				TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
		Assert.assertEquals(
			TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE,
			mockActionRequest.getAttribute(
				TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
	}

	private static ServiceRegistration<MVCActionCommand>
		_mvcActionCommandServiceRegistration1;
	private static ServiceRegistration<MVCActionCommand>
		_mvcActionCommandServiceRegistration2;
	private static ServiceRegistration<javax.portlet.Portlet>
		_portletServiceRegistration;

	@Inject(filter = "javax.portlet.name=" + TestPortlet.PORTLET_NAME)
	private final javax.portlet.Portlet _portlet = null;

	private static class MockLiferayPortletConfig
		extends MockPortletConfig implements LiferayPortletConfig {

		@Override
		public Portlet getPortlet() {
			return null;
		}

		@Override
		public String getPortletId() {
			return "testPortlet";
		}

		@Override
		public Enumeration<PortletMode> getPortletModes(String mimeType) {
			return null;
		}

		@Override
		public Map<String, QName> getPublicRenderParameterDefinitions() {
			return null;
		}

		@Override
		public Enumeration<WindowState> getWindowStates(String mimeType) {
			return null;
		}

		@Override
		public boolean isCopyRequestParameters() {
			return false;
		}

		@Override
		public boolean isWARFile() {
			return false;
		}

	}

	private static class MockLiferayPortletRequest
		extends MockActionRequest implements LiferayPortletRequest {

		@Override
		public void addParameter(String name, String value) {
			_mockHttpServletRequest.addParameter(name, value);

			super.addParameter(name, value);
		}

		@Override
		public void cleanUp() {
		}

		@Override
		public Map<String, String[]> clearRenderParameters() {
			return null;
		}

		@Override
		public void defineObjects(
			PortletConfig portletConfig, PortletResponse portletResponse) {
		}

		@Override
		public ActionParameters getActionParameters() {
			return null;
		}

		@Override
		public Object getAttribute(String name) {
			if (name.equals(JavaConstants.JAVAX_PORTLET_CONFIG)) {
				return new MockLiferayPortletConfig();
			}

			return super.getAttribute(name);
		}

		@Override
		public long getContentLengthLong() {
			return 0;
		}

		@Override
		public HttpServletRequest getHttpServletRequest() {
			return _mockHttpServletRequest;
		}

		@Override
		public String getLifecycle() {
			return null;
		}

		@Override
		public HttpServletRequest getOriginalHttpServletRequest() {
			return _mockHttpServletRequest;
		}

		@Override
		public Part getPart(String name) throws IOException, PortletException {
			return null;
		}

		@Override
		public Collection<Part> getParts()
			throws IOException, PortletException {

			return null;
		}

		@Override
		public long getPlid() {
			return 0;
		}

		@Override
		public Portlet getPortlet() {
			return null;
		}

		@Override
		public PortletContext getPortletContext() {
			return null;
		}

		@Override
		public String getPortletName() {
			return null;
		}

		@Override
		public HttpServletRequest getPortletRequestDispatcherRequest() {
			return null;
		}

		@Override
		public RenderParameters getRenderParameters() {
			return null;
		}

		@Override
		public String getUserAgent() {
			return null;
		}

		@Override
		public void invalidateSession() {
		}

		@Override
		public void setPortletRequestDispatcherRequest(
			HttpServletRequest httpServletRequest) {
		}

		private final MockHttpServletRequest _mockHttpServletRequest =
			new MockHttpServletRequest();

	}

	private static class TestMVCActionCommand1 implements MVCActionCommand {

		public static final String TEST_MVC_ACTION_COMMAND_ATTRIBUTE =
			"TEST_MVC_ACTION_COMMAND_ATTRIBUTE";

		public static final String TEST_MVC_ACTION_COMMAND_NAME =
			"TEST_MVC_ACTION_COMMAND_NAME";

		@Override
		public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse) {

			actionRequest.setAttribute(
				TEST_MVC_ACTION_COMMAND_ATTRIBUTE,
				TEST_MVC_ACTION_COMMAND_ATTRIBUTE);

			return true;
		}

	}

	private static class TestMVCActionCommand2 implements MVCActionCommand {

		public static final String TEST_MVC_ACTION_COMMAND_ATTRIBUTE =
			"TEST_MVC_ACTION_COMMAND_ATTRIBUTE";

		public static final String TEST_MVC_ACTION_COMMAND_NAME =
			"TEST_MVC_ACTION_COMMAND_NAME";

		@Override
		public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse) {

			actionRequest.setAttribute(
				TEST_MVC_ACTION_COMMAND_ATTRIBUTE,
				TEST_MVC_ACTION_COMMAND_ATTRIBUTE);

			return true;
		}

	}

	private static class TestPortlet extends MVCPortlet {

		public static final String PORTLET_NAME =
			"com_liferay_portal_kernel_portlet_bridges_mvc_test_" +
				"MVCActionCommandTest_TestPortlet";

	}

}