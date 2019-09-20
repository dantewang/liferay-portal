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

package com.liferay.portal.osgi.web.servlet.jsp.compiler.internal;

import com.liferay.petra.reflect.ReflectionUtil;

import java.lang.reflect.Field;

import java.net.URL;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.descriptor.DigesterFactory;
import org.apache.tomcat.util.descriptor.XmlIdentifiers;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Dante Wang
 */
@Component(immediate = true, service = {})
public class JspTaglibIDInitializer {

	@Activate
	public void activate() throws Exception {
		Map<String, String> publicIds = new HashMap<>();

		publicIds.putAll(DigesterFactory.SERVLET_API_PUBLIC_IDS);

		Map<String, String> systemIds = new HashMap<>();

		systemIds.putAll(DigesterFactory.SERVLET_API_SYSTEM_IDS);

		_resolveId(
			publicIds, XmlIdentifiers.TLD_11_PUBLIC,
			"web-jsptaglibrary_1_1.dtd", false);
		_resolveId(
			publicIds, XmlIdentifiers.TLD_12_PUBLIC,
			"web-jsptaglibrary_1_2.dtd", false);

		_resolveId(
			systemIds, XmlIdentifiers.TLD_20_XSD, "web-jsptaglibrary_2_0.xsd",
			false);
		_resolveId(
			systemIds, XmlIdentifiers.TLD_21_XSD, "web-jsptaglibrary_2_1.xsd",
			false);

		_resolveId(systemIds, "jsp_2_0.xsd", "jsp_2_0.xsd", true);
		_resolveId(systemIds, "jsp_2_1.xsd", "jsp_2_1.xsd", true);
		_resolveId(systemIds, "jsp_2_2.xsd", "jsp_2_2.xsd", true);
		_resolveId(systemIds, "jsp_2_3.xsd", "jsp_2_3.xsd", true);

		Field publicIDsField = ReflectionUtil.getDeclaredField(
			DigesterFactory.class, "SERVLET_API_PUBLIC_IDS");

		publicIDsField.set(null, Collections.unmodifiableMap(publicIds));

		Field systemIDsField = ReflectionUtil.getDeclaredField(
			DigesterFactory.class, "SERVLET_API_SYSTEM_IDS");

		systemIDsField.set(null, Collections.unmodifiableMap(systemIds));
	}

	private void _resolveId(
		Map<String, String> ids, String id, String name, boolean addSelf) {

		if (ids.containsKey(id)) {
			return;
		}

		Class<?> clazz = getClass();

		URL url = clazz.getResource(
			"/javax/servlet/jsp/resources/".concat(name));

		String location = null;

		if (url != null) {
			location = url.toExternalForm();
		}

		if (location != null) {
			ids.put(id, location);

			if (addSelf) {
				ids.put(location, location);
			}
		}
	}

}