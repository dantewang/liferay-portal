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

package com.liferay.portal.cluster.multiple.internal.jgroups;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.Map;
import java.util.Properties;

import org.jgroups.conf.ConfiguratorFactory;
import org.jgroups.conf.ProtocolStackConfigurator;

/**
 * @author Dante Wang
 */
public class JGroupsConfiguratorFactory {

	public static ProtocolStackConfigurator create(String configXMLFile)
		throws Exception {

		try (InputStream inputStream = ConfiguratorFactory.getConfigStream(
				configXMLFile)) {

			if (inputStream == null) {
				throw new FileNotFoundException(
					"Config file not found: ".concat(configXMLFile));
			}

			String configXML = StreamUtil.toString(inputStream);

			Properties properties = PropsUtil.getProperties();

			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				if (!(entry.getValue() instanceof String)) {
					continue;
				}

				configXML = StringUtil.replace(
					configXML, _getKey((String)entry.getKey()),
					HtmlUtil.escapeAttribute((String)entry.getValue()));
			}

			return ConfiguratorFactory.getStackConfigurator(
				new UnsyncByteArrayInputStream(
					configXML.getBytes(StringPool.UTF8)));
		}
	}

	private static String _getKey(String key) {
		return "${".concat(
			HtmlUtil.escapeAttribute(key)
		).concat(
			"}"
		);
	}

}