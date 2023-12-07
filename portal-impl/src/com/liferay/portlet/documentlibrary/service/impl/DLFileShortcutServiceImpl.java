/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.document.library.kernel.exception.FileShortcutPermissionException;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portlet.documentlibrary.service.base.DLFileShortcutServiceBaseImpl;

/**
 * @author Brian Wing Shun Chan
 */
public class DLFileShortcutServiceImpl extends DLFileShortcutServiceBaseImpl {

	@Override
	public DLFileShortcut addFileShortcut(
			long groupId, long repositoryId, long folderId, long toFileEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), groupId, folderId, ActionKeys.ADD_SHORTCUT);

		try {
			ModelResourcePermissionRegistryUtil.
				<FileEntry>getModelResourcePermission(
					FileEntry.class.getName()
				).check(
					getPermissionChecker(), toFileEntryId, ActionKeys.VIEW
				);
		}
		catch (PrincipalException principalException) {
			throw new FileShortcutPermissionException(principalException);
		}

		return dlFileShortcutLocalService.addFileShortcut(
			getUserId(), groupId, repositoryId, folderId, toFileEntryId,
			serviceContext);
	}

	@Override
	public void deleteFileShortcut(long fileShortcutId) throws PortalException {
		ModelResourcePermissionRegistryUtil.
			<FileShortcut>getModelResourcePermission(
				FileShortcut.class.getName()
			).check(
				getPermissionChecker(), fileShortcutId, ActionKeys.DELETE
			);

		dlFileShortcutLocalService.deleteFileShortcut(fileShortcutId);
	}

	@Override
	public DLFileShortcut getFileShortcut(long fileShortcutId)
		throws PortalException {

		ModelResourcePermissionRegistryUtil.
			<FileShortcut>getModelResourcePermission(
				FileShortcut.class.getName()
			).check(
				getPermissionChecker(), fileShortcutId, ActionKeys.VIEW
			);

		return dlFileShortcutLocalService.getFileShortcut(fileShortcutId);
	}

	@Override
	public DLFileShortcut updateFileShortcut(
			long fileShortcutId, long repositoryId, long folderId,
			long toFileEntryId, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermissionRegistryUtil.
			<FileShortcut>getModelResourcePermission(
				FileShortcut.class.getName()
			).check(
				getPermissionChecker(), fileShortcutId, ActionKeys.UPDATE
			);

		try {
			ModelResourcePermissionRegistryUtil.
				<FileEntry>getModelResourcePermission(
					FileEntry.class.getName()
				).check(
					getPermissionChecker(), toFileEntryId, ActionKeys.VIEW
				);
		}
		catch (PrincipalException principalException) {
			throw new FileShortcutPermissionException(principalException);
		}

		return dlFileShortcutLocalService.updateFileShortcut(
			getUserId(), fileShortcutId, repositoryId, folderId, toFileEntryId,
			serviceContext);
	}

	@Override
	public void updateFileShortcuts(
			long oldToFileEntryId, long newToFileEntryId)
		throws PortalException {

		try {
			ModelResourcePermissionRegistryUtil.
				<FileEntry>getModelResourcePermission(
					FileEntry.class.getName()
				).check(
					getPermissionChecker(), oldToFileEntryId, ActionKeys.VIEW
				);

			ModelResourcePermissionRegistryUtil.
				<FileEntry>getModelResourcePermission(
					FileEntry.class.getName()
				).check(
					getPermissionChecker(), newToFileEntryId, ActionKeys.VIEW
				);
		}
		catch (PrincipalException principalException) {
			throw new FileShortcutPermissionException(principalException);
		}

		dlFileShortcutLocalService.updateFileShortcuts(
			oldToFileEntryId, newToFileEntryId);
	}

}