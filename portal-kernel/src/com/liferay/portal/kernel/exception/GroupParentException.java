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

package com.liferay.portal.kernel.exception;

import com.liferay.petra.string.StringBundler;

/**
 * @author Brian Wing Shun Chan
 * @author Isaac Obrist
 */
public class GroupParentException extends PortalException {

	public static class MustNotBeOwnParent extends GroupParentException {

		public MustNotBeOwnParent(long groupId) {
			super(
				StringBundler.concat(
					"Site ", groupId, " cannot be its own parent site"));

			this.groupId = groupId;
		}

		public long groupId;

	}

	public static class MustNotHaveChildParent extends GroupParentException {

		public MustNotHaveChildParent(long groupId, long parentGroupId) {
			super(
				StringBundler.concat(
					"Site ", groupId, " cannot have a child site ",
					parentGroupId, " as its parent site"));

			this.groupId = groupId;
			this.parentGroupId = parentGroupId;
		}

		public long groupId;
		public long parentGroupId;

	}

	public static class MustNotHaveStagingParent extends GroupParentException {

		public MustNotHaveStagingParent(long groupId, long parentGroupId) {
			super(
				StringBundler.concat(
					"Site ", groupId, " cannot have a staging site ",
					parentGroupId, " as its parent site"));

			this.groupId = groupId;
			this.parentGroupId = parentGroupId;
		}

		public long groupId;
		public long parentGroupId;

	}

	private GroupParentException(String message) {
		super(message);
	}

}