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
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.EmailAddressValidator;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
public class UserEmailAddressException extends PortalException {

	public static class MustBeEqual extends UserEmailAddressException {

		public MustBeEqual(
			User user, String emailAddress1, String emailAddress2) {

			super(
				StringBundler.concat(
					"Email address 1 ", emailAddress1, " and email address 2 ",
					emailAddress2, " for user ", user.getUserId(),
					" must be equal"));

			this.user = user;
			this.emailAddress1 = emailAddress1;
			this.emailAddress2 = emailAddress2;
		}

		public final String emailAddress1;
		public final String emailAddress2;
		public final User user;

	}

	public static class MustNotBeDuplicate extends UserEmailAddressException {

		public MustNotBeDuplicate(
			long companyId, long userId, String emailAddress) {

			super(
				StringBundler.concat(
					"User ", userId,
					" cannot be created because a user with company ",
					emailAddress, " and email address ", companyId,
					" is already in use"));

			this.companyId = companyId;
			this.userId = userId;
			this.emailAddress = emailAddress;
		}

		public MustNotBeDuplicate(long companyId, String emailAddress) {
			super(
				StringBundler.concat(
					"A user with company ", companyId, " and email address ",
					emailAddress, " is already in use"));

			this.companyId = companyId;
			this.emailAddress = emailAddress;
			userId = 0;
		}

		public final long companyId;
		public String emailAddress;
		public final long userId;

	}

	public static class MustNotBeNull extends UserEmailAddressException {

		public MustNotBeNull() {
			super("Email address must not be null");
		}

		public MustNotBeNull(String fullName) {
			super(
				"Email address must not be null for the full name ".concat(
					fullName));
		}

	}

	public static class MustNotBePOP3User extends UserEmailAddressException {

		public MustNotBePOP3User(String emailAddress) {
			super(
				StringBundler.concat(
					"Email address ", emailAddress,
					" must not be the one used to connect to the POP3 server"));

			this.emailAddress = emailAddress;
		}

		public final String emailAddress;

	}

	public static class MustNotBeReserved extends UserEmailAddressException {

		public MustNotBeReserved(
			String emailAddress, String[] reservedEmailAddresses) {

			super(
				StringBundler.concat(
					"Email address ", emailAddress,
					" must not be a reserved one such as: ",
					StringUtil.merge(reservedEmailAddresses)));

			this.emailAddress = emailAddress;
			this.reservedEmailAddresses = reservedEmailAddresses;
		}

		public final String emailAddress;
		public final String[] reservedEmailAddresses;

	}

	public static class MustNotUseCompanyMx extends UserEmailAddressException {

		public MustNotUseCompanyMx(String emailAddress) {
			super(
				StringBundler.concat(
					"Email address ", emailAddress,
					" must not use the MX of the company or one of the " +
						"associated mail host names"));

			this.emailAddress = emailAddress;
		}

		public final String emailAddress;

	}

	public static class MustValidate extends UserEmailAddressException {

		public MustValidate(
			String emailAddress, EmailAddressValidator emailAddressValidator) {

			super(
				StringBundler.concat(
					"Email name address ", emailAddress, " must validate with ",
					ClassUtil.getClassName(emailAddressValidator)));

			this.emailAddress = emailAddress;
			this.emailAddressValidator = emailAddressValidator;
		}

		public String emailAddress;
		public final EmailAddressValidator emailAddressValidator;

	}

	private UserEmailAddressException(String msg) {
		super(msg);
	}

}