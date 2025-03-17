/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Dante Wang
 */
public class EhcacheKey implements Serializable {

	public EhcacheKey(Object key) {
		_key = key;

		// See https://github.com/spring-projects/spring-framework/issues/34483

		int hashCode = _key.hashCode();

		hashCode = (hashCode ^ (hashCode >>> 16)) * 0x85ebca6b;
		hashCode = (hashCode ^ (hashCode >>> 13)) * 0xc2b2ae35;
		hashCode = hashCode ^ (hashCode >>> 16);

		_hashCode = hashCode;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (!(other instanceof EhcacheKey ehcacheKey)) {
			return false;
		}

		return Objects.equals(_key, ehcacheKey._key);
	}

	public Object getKey() {
		return _key;
	}

	@Override
	public int hashCode() {
		return _hashCode;
	}

	private final int _hashCode;
	private final Object _key;

}