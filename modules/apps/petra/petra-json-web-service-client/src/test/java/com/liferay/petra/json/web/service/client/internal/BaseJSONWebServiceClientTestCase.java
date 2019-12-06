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

package com.liferay.petra.json.web.service.client.internal;

import com.liferay.petra.json.web.service.client.server.simulator.HTTPServerSimulator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author Igor Beslic
 */
public abstract class BaseJSONWebServiceClientTestCase {

	protected Map<String, Object> getBaseProperties() {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("hostName", HTTPServerSimulator.HOST_ADDRESS);
		properties.put("hostPort", HTTPServerSimulator.HOST_PORT);
		properties.put("protocol", "http");

		return properties;
	}

	protected List<NameValuePair> toNameValuePairs(
		Map<String, String> parameters) {

		List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();

		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			String key = entry.getKey();

			String value = entry.getValue();

			if (value == null) {
				key = "-" + key;

				value = "";
			}

			NameValuePair nameValuePair = new BasicNameValuePair(key, value);

			nameValuePairs.add(nameValuePair);
		}

		return nameValuePairs;
	}

}