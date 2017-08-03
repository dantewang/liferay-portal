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

package com.liferay.portal.kernel.upgrade;

import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author Leon Chi
 */
public abstract class BaseUpgradeLayoutSetPrototype extends UpgradeProcess {

	protected void updateLayoutPrototype(
			String name, ResourceBundleLoader resourceBundleLoader,
			String nameKey, String descriptionKey)
		throws Exception {

		Long originalCompanyId = CompanyThreadLocal.getCompanyId();
		Long companyId = 0L;

		try (PreparedStatement ps = connection.prepareStatement(
				"select companyId from LayoutSetPrototype where name like ?")) {

			ps.setString(1, name);

			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				companyId = resultSet.getLong("companyId");
			}
		}

		CompanyThreadLocal.setCompanyId(companyId);

		Map<Locale, String> nameMap = ResourceBundleUtil.getLocalizationMap(
			resourceBundleLoader, nameKey);
		Map<Locale, String> descriptionMap =
			ResourceBundleUtil.getLocalizationMap(
				resourceBundleLoader, descriptionKey);

		CompanyThreadLocal.setCompanyId(originalCompanyId);

		String defaultLanguageId = UpgradeProcessUtil.getDefaultLanguageId(
			companyId);

		String nameXml = LocalizationUtil.updateLocalization(
			nameMap, "", "Name", defaultLanguageId);
		String descriptionXml = LocalizationUtil.updateLocalization(
			descriptionMap, "", "Description", defaultLanguageId);

		Date now = new Date();

		Timestamp modifiedDate = new Timestamp(now.getTime());

		try (PreparedStatement ps = connection.prepareStatement(
				"update LayoutSetPrototype set modifiedDate = ?, name = ?," +
					"description = ? where name like ?")) {

			ps.setTimestamp(1, modifiedDate);
			ps.setString(2, nameXml);
			ps.setString(3, descriptionXml);
			ps.setString(4, name);

			ps.executeUpdate();
		}
	}

}