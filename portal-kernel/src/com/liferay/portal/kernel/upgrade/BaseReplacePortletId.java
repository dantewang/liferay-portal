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

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseReplacePortletId extends BaseUpgradePortletId {

	@Override
	protected void doUpgrade() throws Exception {
		Map<String, List<String>> renamePortletIdsMap = new HashMap<>();

		for (String[] renamePortletIds : getRenamePortletIdsArray()) {
			List<String> sourcePortletIds = renamePortletIdsMap.computeIfAbsent(
				renamePortletIds[1], key -> new ArrayList<>());

			sourcePortletIds.add(renamePortletIds[0]);
		}

		List<String> clauses = new ArrayList<>();

		for (Map.Entry<String, List<String>> entry :
				renamePortletIdsMap.entrySet()) {

			List<String> sourcePortletIds = entry.getValue();

			if (sourcePortletIds.size() > 1) {
				for (int i = 0; i < (sourcePortletIds.size() - 1); i++) {
					String portletId1 = sourcePortletIds.get(i);

					for (int j = i + 1; j < sourcePortletIds.size(); j++) {
						String portletId2 = sourcePortletIds.get(j);

						clauses.add(
							String.format(
								"((PP1.portletId = '%s') AND (PP2.portletId " +
									"= '%s'))",
								portletId1, portletId2));
					}
				}
			}
		}

		if (!clauses.isEmpty()) {
			String orClauses = StringUtil.merge(clauses, " OR ");

			_deleteConflictingPreferences(orClauses);
		}

		super.doUpgrade();
	}

	protected boolean hasResourceAction(String name) throws SQLException {
		return hasRow(
			"select count(*) from ResourceAction where name = ?", name);
	}

	@Override
	protected void updateResourceAction(String oldName, String newName)
		throws Exception {

		if (hasResourceAction(newName)) {
			StringBundler sb = new StringBundler(3);

			sb.append("select RA1.resourceActionId from ResourceAction RA1 ");
			sb.append("inner join ResourceAction RA2 on RA1.actionId = ");
			sb.append("RA2.actionId where RA1.name = ? and RA2.name = ?");

			try (PreparedStatement ps1 = connection.prepareStatement(
					sb.toString());
				PreparedStatement ps2 =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						"delete from ResourceAction where resourceActionId = " +
							"?")) {

				ps1.setString(1, oldName);
				ps1.setString(2, newName);

				ResultSet rs = ps1.executeQuery();

				int deleteCount = 0;

				while (rs.next()) {
					ps2.setLong(1, rs.getLong(1));

					ps2.addBatch();

					deleteCount++;
				}

				if (deleteCount > 0) {
					ps2.executeBatch();
				}
			}
		}

		super.updateResourceAction(oldName, newName);
	}

	@Override
	protected void updateResourcePermission(
			String oldRootPortletId, String newRootPortletId,
			boolean updateName)
		throws Exception {

		if (hasResourcePermission(newRootPortletId)) {
			try (PreparedStatement ps = connection.prepareStatement(
					"delete from ResourcePermission where name = ?")) {

				ps.setString(1, oldRootPortletId);

				ps.execute();
			}
		}
		else {
			super.updateResourcePermission(
				oldRootPortletId, newRootPortletId, updateName);
		}
	}

	private void _deleteConflictingPreferences(String orClauses)
		throws SQLException {

		StringBundler sb = new StringBundler(4);

		sb.append("select PP1.portletPreferencesId from PortletPreferences ");
		sb.append("PP1 inner join PortletPreferences PP2 on PP1.plid = ");
		sb.append("PP2.plid where ");
		sb.append(orClauses);

		try (PreparedStatement ps1 = connection.prepareStatement(sb.toString());
			PreparedStatement ps2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"delete from PortletPreferences where " +
						"portletPreferencesId = ?")) {

			ResultSet rs = ps1.executeQuery();

			int deleteCount = 0;

			while (rs.next()) {
				ps2.setLong(1, rs.getLong(1));

				ps2.addBatch();

				deleteCount++;
			}

			if (deleteCount > 0) {
				ps2.executeBatch();
			}
		}
	}

}