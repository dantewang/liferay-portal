/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal.event;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.cache.AggregatedPortalCacheListener;
import com.liferay.portal.cache.ehcache.internal.BaseEhcachePortalCache;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.log.Log;

import java.io.Serializable;

/**
 * @author Dante Wang
 */
public class PortalCacheEventUtil {

	public static <K extends Serializable, V> void notifyRemoved(
		Log log,
		AggregatedPortalCacheListener<K, V> aggregatedPortalCacheListener,
		PortalCache<K, V> portalCache, K key, V value) {

		if (log.isDebugEnabled()) {
			log.debug(
				StringBundler.concat(
					"Removed ", key, " from ",
					portalCache.getPortalCacheName()));
		}

		aggregatedPortalCacheListener.notifyEntryRemoved(
			portalCache, key, value,
			BaseEhcachePortalCache.DEFAULT_TIME_TO_LIVE);
	}

}