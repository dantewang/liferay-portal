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

package com.liferay.petra.log4j.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;

/**
 * @author Hai Yu
 */
public class ConfigurationFactory {

	public static AbstractConfiguration getConfiguration(String xmlContent)
		throws Exception {

		LoggerContext loggerContext = (LoggerContext)LogManager.getContext();

		ConfigurationSource configurationSource = new ConfigurationSource(
			new UnsyncByteArrayInputStream(
				xmlContent.getBytes(StringPool.UTF8)));

		AbstractConfiguration abstractConfiguration = null;

		if (xmlContent.contains(
				"<!DOCTYPE log4j:configuration SYSTEM \"log4j.dtd\">")) {

			abstractConfiguration = new org.apache.log4j.xml.XmlConfiguration(
				loggerContext, configurationSource, 0);
		}
		else {
			abstractConfiguration = new XmlConfiguration(
				loggerContext, configurationSource);
		}

		return abstractConfiguration;
	}

}