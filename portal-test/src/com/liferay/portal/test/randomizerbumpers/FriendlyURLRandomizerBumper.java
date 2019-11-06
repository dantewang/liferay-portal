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

package com.liferay.portal.test.randomizerbumpers;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LayoutFriendlyURLException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.randomizerbumpers.RandomizerBumper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Shuyang Zhou
 */
public class FriendlyURLRandomizerBumper implements RandomizerBumper<String> {

	public static final FriendlyURLRandomizerBumper INSTANCE =
		new FriendlyURLRandomizerBumper();

	@Override
	public boolean accept(String randomValue) {
		if ((randomValue == null) || randomValue.isEmpty()) {
			return false;
		}

		if (randomValue.charAt(0) != CharPool.SLASH) {
			randomValue = StringPool.SLASH.concat(randomValue);
		}

		try {
			Class<?> clazz = Class.forName(
				"com.liferay.portal.model.impl.LayoutImpl");

			if ((Integer)ReflectionTestUtil.invoke(
					clazz, "validateFriendlyURL", new Class<?>[] {String.class},
					randomValue) != -1) {

				return false;
			}

			Method method = ReflectionTestUtil.getMethod(
				clazz, "validateFriendlyURLKeyword",
				new Class<?>[] {String.class});

			method.invoke(clazz, randomValue);

			return true;
		}
		catch (ClassNotFoundException cnfe) {
			throw new RuntimeException(cnfe);
		}
		catch (IllegalAccessException iae) {
			throw new RuntimeException(iae);
		}
		catch (InvocationTargetException ite) {
			Throwable targetException = ite.getTargetException();

			if (targetException.getClass() ==
					LayoutFriendlyURLException.class) {

				return false;
			}

			throw new RuntimeException(ite);
		}
	}

}