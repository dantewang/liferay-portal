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

package com.liferay.portal.tools.sample.sql.builder.fragment;

import com.liferay.fragment.model.FragmentEntryLinkModel;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.lang.reflect.Field;

import java.util.Objects;

/**
 * @author Dante Wang
 */
public class FragmentEntryLinkLocalServiceUtilDependency {

	public static SafeCloseable withFragmentEntryLinkModel(
		FragmentEntryLinkModel fragmentEntryLinkModel) {

		_currentFragmentEntryLinkModel = fragmentEntryLinkModel;

		return () -> _currentFragmentEntryLinkModel = null;
	}

	private static FragmentEntryLinkModel _currentFragmentEntryLinkModel;

	static {
		try {
			Field field = ReflectionUtil.getDeclaredField(
				FragmentEntryLinkLocalServiceUtil.class, "_service");

			field.set(
				null,
				ProxyUtil.newProxyInstance(
					FragmentEntryLinkLocalService.class.getClassLoader(),
					new Class<?>[] {FragmentEntryLinkLocalService.class},
					(proxy, method, args) -> {
						String methodName = method.getName();

						if (Objects.equals(
								methodName, "fetchFragmentEntryLink")) {

							// Convert FragmentEntryLinkModelImpl into
							// FragmentEntryLinkImpl by invoking clone()

							return _currentFragmentEntryLinkModel.clone();
						}

						return null;
					}));
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

}