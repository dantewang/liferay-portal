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

package com.liferay.portlet.extraPortletApp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Leon Chi
 */
public class ExtraPortletApps {

	public static ExtraPortletApp getExtraPortletApp(
		String servletContextName) {

		ExtraPortletApp extraPortletApp = _extraPortletApps.get(
			servletContextName);

		if (extraPortletApp == null) {
			extraPortletApp = new ExtraPortletApp();

			_extraPortletApps.put(servletContextName, extraPortletApp);
		}

		return extraPortletApp;
	}

	private static final ConcurrentMap<String, ExtraPortletApp>
		_extraPortletApps = new ConcurrentHashMap<>();

}