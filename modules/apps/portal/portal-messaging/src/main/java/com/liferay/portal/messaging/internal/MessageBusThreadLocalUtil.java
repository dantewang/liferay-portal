/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.messaging.internal;

import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageKeys;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;

/**
 * @author Tina Tian
 */
public class MessageBusThreadLocalUtil {

	public static void populateMessageFromThreadLocals(Message message) {
		if (!message.contains(MessageKeys.COMPANY_ID)) {
			message.put(
				MessageKeys.COMPANY_ID, CompanyThreadLocal.getCompanyId());
		}

		if (!ClusterInvokeThreadLocal.isEnabled()) {
			message.put(MessageKeys.CLUSTER_INVOKE, Boolean.FALSE);
		}
	}

	public static void populateThreadLocalsFromMessage(
		Message message, PermissionCheckerFactory permissionCheckerFactory,
		UserLocalService userLocalService) {

		long companyId = message.getLong(MessageKeys.COMPANY_ID);

		if (companyId > 0) {
			CompanyThreadLocal.setCompanyId(companyId);
		}

		Boolean clusterInvoke = (Boolean)message.get(
			MessageKeys.CLUSTER_INVOKE);

		if (clusterInvoke != null) {
			ClusterInvokeThreadLocal.setEnabled(clusterInvoke);
		}

		Locale defaultLocale = (Locale)message.get(MessageKeys.DEFAULT_LOCALE);

		if (defaultLocale != null) {
			LocaleThreadLocal.setDefaultLocale(defaultLocale);
		}

		long groupId = message.getLong(MessageKeys.GROUP_ID);

		if (groupId > 0) {
			GroupThreadLocal.setGroupId(groupId);
		}

		PermissionChecker permissionChecker = _getPermissionChecker(
			message.get(MessageKeys.PERMISSION_CHECKER));

		String principalName = message.getString(MessageKeys.PRINCIPAL_NAME);

		if (Validator.isNotNull(principalName)) {
			PrincipalThreadLocal.setName(principalName);
		}

		if ((permissionChecker == null) && Validator.isNotNull(principalName)) {
			try {
				permissionChecker = permissionCheckerFactory.create(
					userLocalService.fetchUser(
						PrincipalThreadLocal.getUserId()));
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		if (permissionChecker != null) {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}

		String principalPassword = message.getString(
			MessageKeys.PRINCIPAL_PASSWORD);

		if (Validator.isNotNull(principalPassword)) {
			PrincipalThreadLocal.setPassword(principalPassword);
		}

		Locale siteDefaultLocale = (Locale)message.get(
			MessageKeys.SITE_DEFAULT_LOCALE);

		if (siteDefaultLocale != null) {
			LocaleThreadLocal.setSiteDefaultLocale(siteDefaultLocale);
		}

		Locale themeDisplayLocale = (Locale)message.get(
			MessageKeys.THEME_DISPLAY_LOCALE);

		if (themeDisplayLocale != null) {
			LocaleThreadLocal.setThemeDisplayLocale(themeDisplayLocale);
		}
	}

	private static PermissionChecker _getPermissionChecker(Object object) {

		// LPS-139811

		if (object instanceof PermissionChecker) {
			return (PermissionChecker)object;
		}

		return null;
	}

}