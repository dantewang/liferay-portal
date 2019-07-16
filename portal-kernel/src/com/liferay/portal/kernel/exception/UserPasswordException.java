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
import com.liferay.petra.string.StringPool;

import java.util.Date;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Preston Crary
 */
public class UserPasswordException extends PortalException {

	public static class MustBeLonger extends UserPasswordException {

		public MustBeLonger(long userId, int minLength) {
			super(
				StringBundler.concat(
					"Password for user ", userId, " must be at least ",
					minLength, " characters"));

			this.minLength = minLength;
			this.userId = userId;
		}

		public final int minLength;
		public final long userId;

	}

	public static class MustComplyWithModelListeners
		extends UserPasswordException {

		public MustComplyWithModelListeners(
			long userId, ModelListenerException modelListenerException) {

			super(
				"Password must comply with model listeners: ".concat(
					modelListenerException.getMessage()));

			this.userId = userId;
			this.modelListenerException = modelListenerException;
		}

		public final ModelListenerException modelListenerException;
		public final long userId;

	}

	public static class MustComplyWithRegex extends UserPasswordException {

		public MustComplyWithRegex(long userId, String regex) {
			super("Password must comply with regex: ".concat(regex));

			this.regex = regex;
			this.userId = userId;
		}

		public final String regex;
		public final long userId;

	}

	public static class MustHaveMoreAlphanumeric extends UserPasswordException {

		public MustHaveMoreAlphanumeric(long minAlphanumeric) {
			super(
				StringBundler.concat(
					"Password must have at least ", minAlphanumeric,
					" alphanumeric characters"));

			this.minAlphanumeric = minAlphanumeric;
		}

		public final long minAlphanumeric;

	}

	public static class MustHaveMoreLowercase extends UserPasswordException {

		public MustHaveMoreLowercase(long minLowercase) {
			super(
				StringBundler.concat(
					"Password must have at least ", minLowercase,
					" lowercase characters"));

			this.minLowercase = minLowercase;
		}

		public final long minLowercase;

	}

	public static class MustHaveMoreNumbers extends UserPasswordException {

		public MustHaveMoreNumbers(long minNumbers) {
			super(
				StringBundler.concat(
					"Password must have at least ", minNumbers, " numbers"));

			this.minNumbers = minNumbers;
		}

		public final long minNumbers;

	}

	public static class MustHaveMoreSymbols extends UserPasswordException {

		public MustHaveMoreSymbols(long minSymbols) {
			super(
				StringBundler.concat(
					"Password must have at least ", minSymbols, " symbols"));

			this.minSymbols = minSymbols;
		}

		public final long minSymbols;

	}

	public static class MustHaveMoreUppercase extends UserPasswordException {

		public MustHaveMoreUppercase(long minUppercase) {
			super(
				StringBundler.concat(
					"Password must have at least ", minUppercase,
					" uppercase characters"));

			this.minUppercase = minUppercase;
		}

		public final long minUppercase;

	}

	public static class MustMatch extends UserPasswordException {

		public MustMatch(long userId) {
			super(
				StringBundler.concat(
					"Passwords for user ", userId, " must match"));

			this.userId = userId;
		}

		public final long userId;

	}

	public static class MustMatchCurrentPassword extends UserPasswordException {

		public MustMatchCurrentPassword(long userId) {
			super(
				StringBundler.concat(
					"Password for user ", userId,
					" does not match the current password"));

			this.userId = userId;
		}

		public final long userId;

	}

	public static class MustNotBeChanged extends UserPasswordException {

		public MustNotBeChanged(long userId) {
			super(
				StringBundler.concat(
					"Password for user ", userId,
					" must not be changed under the current password policy"));

			this.userId = userId;
		}

		public final long userId;

	}

	public static class MustNotBeChangedYet extends UserPasswordException {

		public MustNotBeChangedYet(long userId, Date changeableDate) {
			super(
				StringBundler.concat(
					"Password for user ", userId, " must not be changed until ",
					changeableDate));

			this.userId = userId;
			this.changeableDate = changeableDate;
		}

		public final Date changeableDate;
		public long userId;

	}

	public static class MustNotBeEqualToCurrent extends UserPasswordException {

		public MustNotBeEqualToCurrent(long userId) {
			super(
				StringBundler.concat(
					"Password for user ", userId,
					" must not be equal to their current password"));

			this.userId = userId;
		}

		public final long userId;

	}

	public static class MustNotBeNull extends UserPasswordException {

		public MustNotBeNull(long userId) {
			super(
				StringBundler.concat(
					"Password for user ", userId, " must not be null"));

			this.userId = userId;
		}

		public long userId;

	}

	public static class MustNotBeRecentlyUsed extends UserPasswordException {

		public MustNotBeRecentlyUsed(long userId) {
			super(
				StringBundler.concat(
					"Password for user ", userId, " was used too recently"));

			this.userId = userId;
		}

		public long userId;

	}

	public static class MustNotBeTrivial extends UserPasswordException {

		public MustNotBeTrivial(long userId) {
			super(
				StringBundler.concat(
					"Password for user ", userId, " must not be too trivial"));

			this.userId = userId;
		}

		public long userId;

	}

	public static class MustNotContainDictionaryWords
		extends UserPasswordException {

		public MustNotContainDictionaryWords(
			long userId, List<String> dictionaryWords) {

			super(
				StringBundler.concat(
					"Password for user ", userId,
					" must not contain dictionary words such as: ",
					_getDictionaryWordsString(dictionaryWords)));

			this.userId = userId;
			this.dictionaryWords = dictionaryWords;
		}

		public final List<String> dictionaryWords;
		public long userId;

	}

	private static String _getDictionaryWordsString(
		List<String> dictionaryWords) {

		if (dictionaryWords.size() <= 10) {
			return dictionaryWords.toString();
		}

		List<String> sampleDictionaryWords = dictionaryWords.subList(0, 10);

		return sampleDictionaryWords.toString() + StringPool.SPACE +
			StringPool.TRIPLE_PERIOD;
	}

	private UserPasswordException(String message) {
		super(message);
	}

}