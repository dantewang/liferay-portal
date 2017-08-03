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

package com.liferay.message.boards.layout.set.prototype.internal.upgrade.v1_0_0;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.util.LocalizationImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author Leon Chi
 */
public class UpgradeLayoutPrototype extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_updateLayoutPrototype();
	}

	private LocalizedValuesMap _getLocalizedValuesMap(
		Map<Locale, String> localizationMap) {

		LocalizedValuesMap localizedValuesMap = new LocalizedValuesMap();

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			localizedValuesMap.put(locale, localizationMap.get(locale));
		}

		return localizedValuesMap;
	}

	private void _updateLayoutPrototype() throws Exception {
		try (PreparedStatement ps = connection.prepareStatement(
				"select companyId from LayoutSetPrototype where name like ?")) {

			ps.setString(1, "%Community Site%");

			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				CompanyThreadLocal.setCompanyId(resultSet.getLong("companyId"));
			}
		}
		catch (SQLException sqle) {
		}

		Class<?> clazz = getClass();

		ResourceBundleLoader resourceBundleLoader =
			new AggregateResourceBundleLoader(
				ResourceBundleUtil.getResourceBundleLoader(
					"content.Language", clazz.getClassLoader()),
				LanguageResources.RESOURCE_BUNDLE_LOADER);

		Map<Locale, String> nameMap = ResourceBundleUtil.getLocalizationMap(
			resourceBundleLoader, "layout-set-prototype-community-site-title");
		Map<Locale, String> descriptionMap =
			ResourceBundleUtil.getLocalizationMap(
				resourceBundleLoader,
				"layout-set-prototype-community-site-description");

		Localization localization = new LocalizationImpl();

		String nameXml = localization.getXml(
			_getLocalizedValuesMap(nameMap), "Name");
		String descriptionXml = localization.getXml(
			_getLocalizedValuesMap(descriptionMap), "Description");

		Date now = new Date();

		Timestamp modifiedDate = new Timestamp(now.getTime());

		try (PreparedStatement ps = connection.prepareStatement(
				"update LayoutSetPrototype set modifiedDate = ?, name = ?," +
					"description = ? where name like ?")) {

			ps.setTimestamp(1, modifiedDate);
			ps.setString(2, nameXml);
			ps.setString(3, descriptionXml);
			ps.setString(4, "%Community Site%");

			ps.executeUpdate();
		}
		catch (SQLException sqle) {
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeLayoutPrototype.class);

}