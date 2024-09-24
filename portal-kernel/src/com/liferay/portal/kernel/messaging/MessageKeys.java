/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.messaging;

/**
 * @author Brian Wing Shun Chan
 * @author Michael C. Han
 */
public interface MessageKeys {

	public static final String CLUSTER_INVOKE = "clusterInvoke";

	public static final String COMPANY_ID = "companyId";

	public static final String DEFAULT_LOCALE = "defaultLocale";

	public static final String GROUP_ID = "groupId";

	public static final String PERMISSION_CHECKER = "permissionChecker";

	public static final String PRINCIPAL_NAME = "principalName";

	public static final String PRINCIPAL_PASSWORD = "principalPassword";

	public static final String SITE_DEFAULT_LOCALE = "siteDefaultLocale";

	public static final String THEME_DISPLAY_LOCALE = "themeDisplayLocale";

}