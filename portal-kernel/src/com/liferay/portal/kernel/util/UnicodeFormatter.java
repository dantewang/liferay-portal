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

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class UnicodeFormatter {

	public static final String UNICODE_PREFIX = "\\u";

	public static String bytesToHexString(byte[] bytes) {
		char[] array = new char[bytes.length * 2];

		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];

			array[(i * 2) + 0] = _HEX_DIGITS[(b >> 4) & 0x0f];
			array[(i * 2) + 1] = _HEX_DIGITS[b & 0x0f];
		}

		return new String(array);
	}

	public static char[] byteToHexChars(byte b) {
		return byteToHexChars(b, false);
	}

	public static char[] byteToHexChars(byte b, boolean upperCase) {
		if (upperCase) {
			return _byteToHexChars(b, _HEX_DIGITS_UPPER_CASE);
		}
		else {
			return _byteToHexChars(b, _HEX_DIGITS);
		}
	}

	public static String byteToHexString(byte b) {
		char[] array = {_HEX_DIGITS[(b >> 4) & 0x0f], _HEX_DIGITS[b & 0x0f]};

		return new String(array);
	}

	public static String charToHexString(char c) {
		byte hi = (byte)(c >>> 8);
		byte lo = (byte)(c & 0xff);

		char[] array = {
			_HEX_DIGITS[(hi >> 4) & 0x0f], _HEX_DIGITS[hi & 0x0f],
			_HEX_DIGITS[(lo >> 4) & 0x0f], _HEX_DIGITS[lo & 0x0f]
		};

		return new String(array);
	}

	public static byte[] hexToBytes(String hexString) {
		if ((hexString == null) || (hexString.length() % 2) != 0) {
			return new byte[0];
		}

		byte[] bytes = new byte[hexString.length() / 2];

		for (int i = 0; i < hexString.length(); i = i + 2) {
			String s = hexString.substring(i, i + 2);

			try {
				bytes[i / 2] = (byte)Integer.parseInt(s, 16);
			}
			catch (NumberFormatException nfe) {
				return new byte[0];
			}
		}

		return bytes;
	}

	public static String parseString(String hexString) {
		if (hexString == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		char[] array = hexString.toCharArray();

		if ((array.length % 6) != 0) {
			_log.error("String is not in hex format");

			return hexString;
		}

		for (int i = 2; i < hexString.length(); i = i + 6) {
			String s = hexString.substring(i, i + 4);

			try {
				char c = (char)Integer.parseInt(s, 16);

				sb.append(c);
			}
			catch (Exception e) {
				_log.error(e, e);

				return hexString;
			}
		}

		return sb.toString();
	}

	public static String toString(char[] array) {
		if (array == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder(array.length * 6);

		for (char c : array) {
			sb.append(UNICODE_PREFIX);
			sb.append(_charToHexChars(c));
		}

		return sb.toString();
	}

	public static String toString(String s) {
		if (s == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder(s.length() * 6);

		for (int i = 0; i < s.length(); i++) {
			sb.append(UNICODE_PREFIX);
			sb.append(_charToHexChars(s.charAt(i)));
		}

		return sb.toString();
	}

	private static char[] _byteToHexChars(byte b, char[] table) {
		char[] hexes = new char[2];

		hexes[0] = table[(b >> 4) & 0x0f];
		hexes[1] = table[b & 0x0f];

		return hexes;
	}

	private static char[] _charToHexChars(char c) {
		byte hi = (byte)(c >>> 8);
		byte lo = (byte)(c & 0xff);

		char[] hexes = new char[4];

		hexes[0] = _HEX_DIGITS[(hi >> 4) & 0x0f];
		hexes[1] = _HEX_DIGITS[hi & 0x0f];
		hexes[2] = _HEX_DIGITS[(lo >> 4) & 0x0f];
		hexes[3] = _HEX_DIGITS[lo & 0x0f];

		return hexes;
	}

	private static final char[] _HEX_DIGITS = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
		'e', 'f'
	};

	private static final char[] _HEX_DIGITS_UPPER_CASE = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
		'E', 'F'
	};

	private static final Log _log = LogFactoryUtil.getLog(
		UnicodeFormatter.class);

}