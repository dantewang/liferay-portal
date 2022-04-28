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

package com.liferay.client.extension.web.internal.upgrade;

import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.upgrade.BasePortletIdUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.vulcan.util.TransformUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 * @author Brian Wing Shun Chan
 */
@Component(immediate = true, service = UpgradeStepRegistrator.class)
public class ClientExtensionWebUpgrade implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		try {
			UpgradeProcess upgradeProcess = new UpgradeProcess() {

				@Override
				protected void doUpgrade() throws Exception {
					runSQL(
						StringBundler.concat(
							"update Release_ set servletContextName = ",
							"'com.liferay.client.extension.web' where ",
							"servletContextName = ",
							"'com.liferay.remote.app.web'"));
				}

			};

			upgradeProcess.upgrade();
		}
		catch (UpgradeException upgradeException) {
			throw new RuntimeException(upgradeException);
		}

		registry.register(
			"0.0.0", "1.0.0",
			new BasePortletIdUpgradeProcess() {

				@Override
				protected String[][] getRenamePortletIdsArray() {
					return _getRenamePortletIdsArray(
						"remote_app_",
						"com_liferay_remote_app_web_internal_portlet_" +
							"RemoteAppEntryPortlet_");
				}

			});

		registry.register(
			"1.0.0", "2.0.0",
			new BasePortletIdUpgradeProcess() {

				@Override
				protected String[][] getRenamePortletIdsArray() {
					return new String[][] {
						{
							"com_liferay_remote_app_admin_web_portlet_" +
								"RemoteAppAdminPortlet",
							"com_liferay_client_extension_web_internal_" +
								"portlet_ClientExtensionAdminPortlet"
						}
					};
				}

			});

		registry.register(
			"2.0.0", "2.0.1",
			new BasePortletIdUpgradeProcess() {

				@Override
				protected String[][] getRenamePortletIdsArray() {
					return _getRenamePortletIdsArray(
						"com_liferay_remote_app_web_internal_portlet_" +
							"RemoteAppEntryPortlet_",
						"com_liferay_client_extension_web_internal_portlet_" +
							"ClientExtensionEntryPortlet_");
				}

			});
	}

	private String[][] _getRenamePortletIdsArray(
		String oldPortletIdPrefix, String newPortletIdPrefix) {

		return TransformUtil.transformToArray(
			_clientExtensionEntryLocalService.getClientExtensionEntries(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			clientExtensionEntry -> new String[] {
				oldPortletIdPrefix +
					clientExtensionEntry.getClientExtensionEntryId(),
				newPortletIdPrefix +
					clientExtensionEntry.getClientExtensionEntryId()
			},
			String[].class);
	}

	@Reference
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.client.extension.service)(release.schema.version>=3.0.0))"
	)
	private Release _release;

}