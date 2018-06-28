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

package com.liferay.portal.cache.cache2k.internal.event;

import com.liferay.portal.cache.PortalCacheManagerListenerFactory;
import com.liferay.portal.cache.cache2k.internal.Cache2kPortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerListener;

import java.util.Properties;

import org.osgi.service.component.annotations.Component;

/**
 * @author Xiangyue Cai
 */
@Component(immediate = true, service = PortalCacheManagerListenerFactory.class)
public class Cache2kPortalCacheManagerListenerFactory
	implements PortalCacheManagerListenerFactory
		<Cache2kPortalCacheManager<?, ?>> {

	@Override
	public PortalCacheManagerListener create(
			Cache2kPortalCacheManager<?, ?> portalCacheManager,
			Properties properties) {
		return null;
	}
}