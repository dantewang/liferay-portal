/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.verify;

import com.liferay.feature.flag.web.internal.util.DeprecationFeatureFlagUtil;
import com.liferay.feature.flag.web.internal.feature.flag.FeatureFlagsBagProvider;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.verify.VerifyProcess;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Buarque
 */
@Component(property = "initial.deployment=true", service = VerifyProcess.class)
public class SystemDeprecationFeatureFlagVerifyProcess extends VerifyProcess {

	@Override
	protected void doVerify() {
		DeprecationFeatureFlagUtil.processDeprecationFeatureFlags(
			CompanyConstants.SYSTEM, _featureFlagsBagProvider,
			_portalPreferencesLocalService);
	}

	@Reference
	private FeatureFlagsBagProvider _featureFlagsBagProvider;

	@Reference
	private PortalPreferencesLocalService _portalPreferencesLocalService;

}