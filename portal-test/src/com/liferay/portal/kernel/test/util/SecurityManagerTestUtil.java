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

package com.liferay.portal.kernel.test.util;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.test.SwappableSecurityManager;

import java.security.Permission;

/**
 * @author Xiangyue Cai
 */
public class SecurityManagerTestUtil {

	public static final String ACCESS_PERMISSION = "suppressAccessChecks";

	public static SwappableSecurityManager installForCheckPermission(
			String permissionName, Class<?> callerClass, Throwable throwable)
		throws ClassNotFoundException {

		Class.forName(ReflectionUtil.class.getName());

		SwappableSecurityManager swappableSecurityManager =
			new SwappableSecurityManager() {

				@Override
				public void checkPermission(Permission permission) {
					if (!permissionName.equals(permission.getName())) {
						return;
					}

					for (Class<?> clazz : getClassContext()) {
						if (clazz == callerClass) {
							ReflectionUtil.throwException(throwable);
						}
					}
				}

			};

		swappableSecurityManager.install();

		return swappableSecurityManager;
	}

}