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

package com.liferay.portal.security.auth;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Peter Fellwock
 */
public class AuthVerifierPipelineTest {

	@BeforeClass
	public static void setUpClass() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		Registry registry = RegistryUtil.getRegistry();

		_authVerifierResult = new AuthVerifierResult();

		_authVerifierResult.setSettings(new HashMap<>());
		_authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);

		_serviceRegistration = registry.registerService(
			AuthVerifier.class,
			(AuthVerifier)ProxyUtil.newProxyInstance(
				AuthVerifier.class.getClassLoader(),
				new Class<?>[] {AuthVerifier.class},
				(proxy, method, args) -> {
					if ("getAuthType".equals(method.getName())) {
						return HttpServletRequest.BASIC_AUTH;
					}

					if ("verify".equals(method.getName())) {
						return _authVerifierResult;
					}

					return null;
				}),
			new HashMap<String, Object>() {
				{
					put(
						"urls.includes",
						"/TestAuthVerifier/*,/TestAuthVerifierTest/*");
				}
			});
	}

	@Test
	public void testVerifyRequest() throws PortalException {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(new MockServletContext());

		mockHttpServletRequest.setRequestURI("/TestAuthVerifier/Hello");

		AccessControlContext accessControlContext = new AccessControlContext();

		accessControlContext.setRequest(mockHttpServletRequest);

		Assert.assertSame(
			_authVerifierResult,
			AuthVerifierPipeline.verifyRequest(accessControlContext));
	}

	private static AuthVerifierResult _authVerifierResult;
	private static ServiceRegistration<AuthVerifier> _serviceRegistration;

}