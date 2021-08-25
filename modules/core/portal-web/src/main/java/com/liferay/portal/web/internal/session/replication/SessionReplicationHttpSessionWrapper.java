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

package com.liferay.portal.web.internal.session.replication;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.servlet.HttpSessionWrapper;

import java.io.Serializable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

/**
 * @author Dante Wang
 */
public class SessionReplicationHttpSessionWrapper extends HttpSessionWrapper {

	public SessionReplicationHttpSessionWrapper(HttpSession session) {
		super(session);
	}

	@Override
	public Object getAttribute(String name) {
		Object value = super.getAttribute(name);

		if (Objects.equals(value, _PLACE_HOLDER_VALUE)) {
			return _portalCache.get(name);
		}

		return value;
	}

	@Override
	public void removeAttribute(String name) {
		super.removeAttribute(name);

		_portalCache.remove(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (value instanceof Serializable) {
			Class<?> clazz = value.getClass();

			if (!_safeClassLoaders.contains(clazz.getClassLoader())) {
				_portalCache.put(name, value);

				super.setAttribute(name, _PLACE_HOLDER_VALUE);

				return;
			}
		}

		super.setAttribute(name, value);
	}

	private static final String _PLACE_HOLDER_VALUE = "PLACE_HOLDER_VALUE";

	private static final Set<ClassLoader> _safeClassLoaders =
		new HashSet<ClassLoader>() {
			{
				add(String.class.getClassLoader());
				add(HttpSession.class.getClassLoader());
				add(Logger.class.getClassLoader());
			}
		};

	private final PortalCache<String, Object> _portalCache =
		PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM,
			SessionReplicationHttpSessionWrapper.class.getName());

}