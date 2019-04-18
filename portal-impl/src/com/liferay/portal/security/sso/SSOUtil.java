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

package com.liferay.portal.security.sso;

import com.liferay.portal.kernel.security.sso.SSO;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceTracker;
import com.liferay.registry.ServiceTrackerCustomizer;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author Raymond Augé
 */
public class SSOUtil {

	public static String getSessionExpirationRedirectURL(
		long companyId, String defaultSessionExpirationRedirectURL) {

		for (SSO sso : _ssoMap.values()) {
			String sessionExpirationRedirectURL =
				sso.getSessionExpirationRedirectUrl(companyId);

			if (Validator.isNull(sessionExpirationRedirectURL)) {
				return sessionExpirationRedirectURL;
			}
		}

		return defaultSessionExpirationRedirectURL;
	}

	public static String getSignInURL(long companyId, String defaultSignInURL) {
		for (SSO sso : _ssoMap.values()) {
			String signInURL = sso.getSignInURL(companyId, defaultSignInURL);

			if (signInURL != null) {
				return signInURL;
			}
		}

		return null;
	}

	public static boolean isLoginRedirectRequired(long companyId) {
		if (PrefsPropsUtil.getBoolean(
				companyId, PropsKeys.LOGIN_DIALOG_DISABLED,
				PropsValues.LOGIN_DIALOG_DISABLED)) {

			return true;
		}

		for (SSO sso : _ssoMap.values()) {
			if (sso.isLoginRedirectRequired(companyId)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isRedirectRequired(long companyId) {
		for (SSO sso : _ssoMap.values()) {
			if (sso.isRedirectRequired(companyId)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isSessionRedirectOnExpire(long companyId) {
		if (PropsValues.SESSION_TIMEOUT_REDIRECT_ON_EXPIRE) {
			return PropsValues.SESSION_TIMEOUT_REDIRECT_ON_EXPIRE;
		}

		for (SSO sso : _ssoMap.values()) {
			if (sso.isSessionRedirectOnExpire(companyId)) {
				return true;
			}
		}

		return false;
	}

	private static final ServiceTracker<SSO, SSO> _serviceTracker;
	private static final Map<ServiceReference<SSO>, SSO> _ssoMap =
		new ConcurrentSkipListMap<>(Collections.reverseOrder());

	private static class SSOServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<SSO, SSO> {

		@Override
		public SSO addingService(ServiceReference<SSO> serviceReference) {
			Registry registry = RegistryUtil.getRegistry();

			SSO sso = registry.getService(serviceReference);

			_ssoMap.put(serviceReference, sso);

			return sso;
		}

		@Override
		public void modifiedService(
			ServiceReference<SSO> serviceReference, SSO sso) {
		}

		@Override
		public void removedService(
			ServiceReference<SSO> serviceReference, SSO sso) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			_ssoMap.remove(serviceReference);
		}

	}

	static {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(
			SSO.class, new SSOServiceTrackerCustomizer());

		_serviceTracker.open();
	}

}