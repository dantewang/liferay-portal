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
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.ScreenNameValidator;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Brian Wing Shun Chan
 */
public class UserScreenNameException extends PortalException {

	public static class MustNotBeDuplicate extends UserScreenNameException {

		public MustNotBeDuplicate(long userId, String screenName) {
			super(
				StringBundler.concat(
					"Screen name ", screenName,
					" must not be duplicate but is already used by user ",
					userId));

			this.userId = userId;
			this.screenName = screenName;
		}

		public final String screenName;
		public final long userId;

	}

	public static class MustNotBeNull extends UserScreenNameException {

		public MustNotBeNull() {
			super("Screen name must not be null");
		}

		public MustNotBeNull(long userId) {
			super(
				"Screen name must not be null for user ".concat(
					String.valueOf(userId)));
		}

		public MustNotBeNull(String fullName) {
			super(
				"Screen name must not be null for the full name ".concat(
					fullName));
		}

	}

	public static class MustNotBeNumeric extends UserScreenNameException {

		public MustNotBeNumeric(long userId, String screenName) {
			super(
				StringBundler.concat(
					"Screen name ", screenName, " for user ", userId,
					"must not be numeric because the portal property \"",
					PropsKeys.USERS_SCREEN_NAME_ALLOW_NUMERIC,
					"\" is disabled"));

			this.userId = userId;
			this.screenName = screenName;
		}

		public final String screenName;
		public final long userId;

	}

	public static class MustNotBeReserved extends UserScreenNameException {

		public MustNotBeReserved(
			long userId, String screenName, String[] reservedScreenNames) {

			super(
				StringBundler.concat(
					"Screen name ", screenName, " for user ", userId,
					" must not be a reserved name such as: ",
					StringUtil.merge(reservedScreenNames)));

			this.userId = userId;
			this.screenName = screenName;
			this.reservedScreenNames = reservedScreenNames;
		}

		public final String[] reservedScreenNames;
		public final String screenName;
		public final long userId;

	}

	public static class MustNotBeReservedForAnonymous
		extends UserScreenNameException {

		public MustNotBeReservedForAnonymous(
			long userId, String screenName, String[] reservedScreenNames) {

			super(
				StringBundler.concat(
					"Screen name ", screenName, " for user ", userId,
					" must not be a reserved name for anonymous users such ",
					"as: ", StringUtil.merge(reservedScreenNames)));

			this.userId = userId;
			this.screenName = screenName;
			this.reservedScreenNames = reservedScreenNames;
		}

		public final String[] reservedScreenNames;
		public final String screenName;
		public final long userId;

	}

	public static class MustNotBeUsedByGroup extends UserScreenNameException {

		public MustNotBeUsedByGroup(
			long userId, String screenName, Group group) {

			super(
				StringBundler.concat(
					"Screen name ", screenName, " for user ", userId,
					" must not be used by group ", group.getGroupId()));

			this.userId = userId;
			this.screenName = screenName;
			this.group = group;
		}

		public final Group group;
		public final String screenName;
		public final long userId;

	}

	public static class MustProduceValidFriendlyURL
		extends UserScreenNameException {

		public MustProduceValidFriendlyURL(
			long userId, String screenName, int exceptionType) {

			super(
				StringBundler.concat(
					"Screen name ", screenName, " for user ", userId,
					" must produce a valid friendly URL"),
				new GroupFriendlyURLException(exceptionType));

			this.userId = userId;
			this.screenName = screenName;
			this.exceptionType = exceptionType;
		}

		public final int exceptionType;
		public final String screenName;
		public final long userId;

	}

	public static class MustValidate extends UserScreenNameException {

		public MustValidate(
			long userId, String screenName,
			ScreenNameValidator screenNameValidator) {

			super(
				StringBundler.concat(
					"Screen name ", screenName, " for user ", userId,
					" must validate with ",
					ClassUtil.getClassName(screenNameValidator), ": ",
					screenNameValidator.getDescription(
						LocaleUtil.getDefault())));

			this.userId = userId;
			this.screenName = screenName;
			this.screenNameValidator = screenNameValidator;
		}

		public final String screenName;
		public final ScreenNameValidator screenNameValidator;
		public final long userId;

	}

	private UserScreenNameException(String msg) {
		super(msg);
	}

	private UserScreenNameException(String msg, Throwable cause) {
		super(msg, cause);
	}

}