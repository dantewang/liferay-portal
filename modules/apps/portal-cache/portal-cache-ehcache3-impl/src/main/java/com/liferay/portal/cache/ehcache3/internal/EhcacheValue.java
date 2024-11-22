/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache3.internal;

import java.time.Duration;

/**
 * @author Dante Wang
 */
public class EhcacheValue {

	public EhcacheValue(Object value, Duration timeToLive) {
		_value = value;
		_timeToLive = timeToLive;
	}

	public Duration getTimeToLive() {
		return _timeToLive;
	}

	public Object getValue() {
		return _value;
	}

	private final Duration _timeToLive;
	private final Object _value;

}