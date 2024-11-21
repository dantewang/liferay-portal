/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache3.internal;

import org.ehcache.expiry.ExpiryPolicy;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * @author Dante Wang
 */
public class EhcacheExpiryPolicy implements ExpiryPolicy<Object, Object> {

	@Override
	public Duration getExpiryForCreation(Object key, Object value) {
		EhcacheValue ehcacheValue = (EhcacheValue)value;
		return ehcacheValue.getTimeToLive();
	}

	@Override
	public Duration getExpiryForAccess(Object key, Supplier<?> value) {
		return null;
	}

	@Override
	public Duration getExpiryForUpdate(Object key, Supplier<?> oldValue, Object newValue) {
		EhcacheValue ehcacheValue = (EhcacheValue)newValue;
		return ehcacheValue.getTimeToLive();
	}

}