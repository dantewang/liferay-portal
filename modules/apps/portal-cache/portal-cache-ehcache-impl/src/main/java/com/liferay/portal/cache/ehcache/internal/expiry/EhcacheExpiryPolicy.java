/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal.expiry;

import java.time.Duration;

import java.util.Objects;
import java.util.function.Supplier;

import org.ehcache.expiry.ExpiryPolicy;

/**
 * @author Dante Wang
 */
public class EhcacheExpiryPolicy implements ExpiryPolicy<Object, Object> {

	public EhcacheExpiryPolicy(ExpiryPolicy<Object, Object> expiryPolicy) {
		_expiryPolicy = expiryPolicy;

		Class<?> clazz = expiryPolicy.getClass();

		_ttl = Objects.equals(_TTL_POLICY_CLASS_NAME, clazz.getName());
	}

	@Override
	public Duration getExpiryForAccess(Object key, Supplier<?> value) {
		return _expiryPolicy.getExpiryForAccess(key, value);
	}

	@Override
	public Duration getExpiryForCreation(Object key, Object value) {
		EhcacheExpiryValue ehcacheExpiryValue = (EhcacheExpiryValue)value;

		Duration duration = ehcacheExpiryValue.getTimeToLive();

		if (_ttl && duration.equals(ExpiryPolicy.INFINITE)) {
			return _expiryPolicy.getExpiryForCreation(key, value);
		}

		return duration;
	}

	@Override
	public Duration getExpiryForUpdate(
		Object key, Supplier<?> oldValue, Object newValue) {

		EhcacheExpiryValue ehcacheExpiryValue = (EhcacheExpiryValue)newValue;

		Duration duration = ehcacheExpiryValue.getTimeToLive();

		if (_ttl && duration.equals(ExpiryPolicy.INFINITE)) {
			return _expiryPolicy.getExpiryForUpdate(key, oldValue, newValue);
		}

		return duration;
	}

	private static final String _TTL_POLICY_CLASS_NAME =
		"org.ehcache.config.builders.ExpiryPolicyBuilder." +
			"TimeToLiveExpiryPolicy";

	private final ExpiryPolicy<Object, Object> _expiryPolicy;
	private final boolean _ttl;

}