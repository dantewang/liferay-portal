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

package com.liferay.portal.kernel.security.permission;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hai Yu
 */
public class ResourceActionsBagPool {

	public static boolean containsKey(String name) {
		return _resourceActionsBagPool.containsKey(name);
	}

	public static ResourceActionsBag get(String name) {
		return _resourceActionsBagPool.get(name);
	}

	public static Set<String> keySet() {
		return _resourceActionsBagPool.keySet();
	}

	public static ResourceActionsBag put(
		String name, ResourceActionsBag resourceActionsBag, boolean force) {

		synchronized (_resourceActionsBagPool) {
			if (!force && (_resourceActionsBagPool.get(name) != null)) {
				return _resourceActionsBagPool.get(name);
			}

			_resourceActionsBagPool.put(name, resourceActionsBag);
		}

		return resourceActionsBag;
	}

	public static ResourceActionsBag remove(String name) {
		return _resourceActionsBagPool.remove(name);
	}

	private static final Map<String, ResourceActionsBag>
		_resourceActionsBagPool = new ConcurrentHashMap<>();

}