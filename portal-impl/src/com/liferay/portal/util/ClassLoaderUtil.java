/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.util;

import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.security.lang.SecurityManagerUtil;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author Raymond Augé
 * @author Shuyang Zhou
 */
public class ClassLoaderUtil {

	public static ClassLoader getClassLoader(Class<?> clazz) {
		return _classLoaderUtilProvider.getClassLoader(clazz);
	}

	public static ClassLoader getContextClassLoader() {
		return _classLoaderUtilProvider.getContextClassLoader();
	}

	public static ClassLoader getPortalClassLoader() {
		return _classLoaderUtilProvider.getPortalClassLoader();
	}

	public static void setContextClassLoader(ClassLoader classLoader) {
		_classLoaderUtilProvider.setContextClassLoader(classLoader);
	}

	private static final ClassLoaderUtilProvider _classLoaderUtilProvider;

	static {
		if (SecurityManagerUtil.isNone()) {
			_classLoaderUtilProvider = new ClassLoaderUtilProvider();
		}
		else {
			_classLoaderUtilProvider = new PACLClassLoaderUtilProvider();
		}
	}

	private static class ClassLoaderUtilProvider {

		public ClassLoader getClassLoader(Class<?> clazz) {
			return clazz.getClassLoader();
		}

		public ClassLoader getContextClassLoader() {
			Thread thread = Thread.currentThread();

			return thread.getContextClassLoader();
		}

		public ClassLoader getPortalClassLoader() {
			return PortalClassLoaderUtil.getClassLoader();
		}

		public void setContextClassLoader(ClassLoader classLoader) {
			Thread thread = Thread.currentThread();

			thread.setContextClassLoader(classLoader);
		}

	}

	private static class PACLClassLoaderUtilProvider
		extends ClassLoaderUtilProvider {

		@Override
		public ClassLoader getClassLoader(final Class<?> clazz) {
			if (!SecurityManagerUtil.isPACLDisabled()) {
				return super.getClassLoader(clazz);
			}

			return AccessController.doPrivileged(
				new PrivilegedAction<ClassLoader>() {

					public ClassLoader run() {
						return PACLClassLoaderUtilProvider.super.getClassLoader(
							clazz);
					}

				}
			);
		}

		@Override
		public ClassLoader getContextClassLoader() {
			if (!SecurityManagerUtil.isPACLDisabled()) {
				return super.getContextClassLoader();
			}

			return AccessController.doPrivileged(
				new PrivilegedAction<ClassLoader>() {

					public ClassLoader run() {
						return PACLClassLoaderUtilProvider.super.getContextClassLoader();
					}

				}
			);
		}

		@Override
		public ClassLoader getPortalClassLoader() {
			if (!SecurityManagerUtil.isPACLDisabled()) {
				return super.getPortalClassLoader();
			}

			return AccessController.doPrivileged(
				new PrivilegedAction<ClassLoader>() {

					public ClassLoader run() {
						return PACLClassLoaderUtilProvider.super.getPortalClassLoader();
					}

				}
			);
		}

		@Override
		public void setContextClassLoader(final ClassLoader classLoader) {
			if (!SecurityManagerUtil.isPACLDisabled()) {
				super.setContextClassLoader(classLoader);
			}

			AccessController.doPrivileged(
				new PrivilegedAction<Void>() {

					public Void run() {
						PACLClassLoaderUtilProvider.super.setContextClassLoader(
							classLoader);

						return null;
					}

				}
			);
		}

	}

}