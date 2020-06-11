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

package com.liferay.portal.log4j.extender.internal;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.log4j.Log4JUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Field;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.composite.CompositeConfiguration;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.util.tracker.BundleTracker;

/**
 * @author Shuyang Zhou
 */
public class Log4jExtenderBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		_bundleTracker = new BundleTracker<Bundle>(
			bundleContext, ~(Bundle.INSTALLED | Bundle.UNINSTALLED), null) {

			@Override
			public Bundle addingBundle(Bundle bundle, BundleEvent bundleEvent) {
				try {
					_configureLog4j(bundle, "module-log4j.xml");
					_configureLog4j(bundle, "module-log4j-ext.xml");
					_configureLog4j(bundle.getSymbolicName());
				}
				catch (IOException ioException) {
					_logger.error(
						"Unable to configure Log4j for bundle " +
							bundle.getSymbolicName(),
						ioException);
				}

				return bundle;
			}

		};

		_bundleTracker.open();
	}

	@Override
	public void stop(BundleContext context) {
		_bundleTracker.close();
	}

	private static String _escapeXMLAttribute(String s) {
		return StringUtil.replace(
			s,
			new char[] {
				CharPool.AMPERSAND, CharPool.APOSTROPHE, CharPool.LESS_THAN,
				CharPool.QUOTE
			},
			new String[] {"&amp;", "&apos;", "&lt;", "&quot;"});
	}

	private static String _getLiferayHome() {
		if (_liferayHome == null) {
			_liferayHome = _escapeXMLAttribute(
				PropsUtil.get(PropsKeys.LIFERAY_HOME));
		}

		return _liferayHome;
	}

	private static String _getURLContent(URL url) {
		Map<String, String> variables = HashMapBuilder.put(
			"@liferay.home@", _getLiferayHome()
		).put(
			"@spi.id@",
			() -> {
				String spiId = System.getProperty("spi.id");

				if (spiId != null) {
					return spiId;
				}

				return StringPool.BLANK;
			}
		).build();

		String urlContent = null;

		try (InputStream inputStream = url.openStream()) {
			UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream();

			StreamUtil.transfer(
				inputStream, unsyncByteArrayOutputStream, -1, true);

			byte[] bytes = unsyncByteArrayOutputStream.toByteArray();

			urlContent = new String(bytes, StringPool.UTF8);
		}
		catch (Exception exception) {
			_logger.error(exception, exception);

			return null;
		}

		for (Map.Entry<String, String> variable : variables.entrySet()) {
			urlContent = StringUtil.replace(
				urlContent, variable.getKey(), variable.getValue());
		}

		return urlContent;
	}

	private synchronized void _configureLog4j(
			Bundle bundle, String resourcePath)
		throws IOException {

		Enumeration<URL> enumeration = bundle.findEntries(
			"META-INF", resourcePath, false);

		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

				ConfigurationSource configurationSource = null;

				URL url = enumeration.nextElement();

				try {
					Path path = Files.createTempFile(null, ".xml");

					String urlContent = _getURLContent(url);

					Files.write(path, urlContent.getBytes());

					configurationSource = new ConfigurationSource(
						url.openStream(), path.toFile());
				}
				catch (Exception exception) {
					exception.printStackTrace();
				}

				// version1 use one loggerContext.

				LoggerContext loggerContext = Configurator.initialize(
					bundleWiring.getClassLoader(), configurationSource);

				XmlConfigurationFactory xmlConfigurationFactory =
					new XmlConfigurationFactory();

				XmlConfiguration xmlConfiguration =
					(XmlConfiguration)xmlConfigurationFactory.getConfiguration(
						loggerContext, configurationSource);

				_configurations.add(xmlConfiguration);

				_compositeConfiguration =
					(CompositeConfiguration)loggerContext.getConfiguration();

				try {
					Field field = ReflectionUtil.getDeclaredField(
						CompositeConfiguration.class, "configurations");

					field.set(_compositeConfiguration, _configurations);

					loggerContext.setConfiguration(
						_compositeConfiguration.reconfigure());
				}
				catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	private void _configureLog4j(String symbolicName) throws IOException {
		File configFile = new File(
			StringBundler.concat(
				PropsValues.MODULE_FRAMEWORK_BASE_DIR, "/log4j/", symbolicName,
				"-log4j-ext.xml"));

		if (!configFile.exists()) {
			return;
		}

		ConfigurationSource configurationSource = new ConfigurationSource(
			new FileInputStream(configFile), configFile);

		LoggerContext loggerContext = Configurator.initialize(
			null, configurationSource);

		XmlConfigurationFactory xmlConfigurationFactory =
			new XmlConfigurationFactory();

		XmlConfiguration xmlConfiguration =
			(XmlConfiguration)xmlConfigurationFactory.getConfiguration(
				loggerContext, configurationSource);

		_configurations.add(xmlConfiguration);

		_compositeConfiguration =
			(CompositeConfiguration)loggerContext.getConfiguration();

		try {
			Field field = ReflectionUtil.getDeclaredField(
				CompositeConfiguration.class, "configurations");

			field.set(_compositeConfiguration, _configurations);

			loggerContext.setConfiguration(
				_compositeConfiguration.reconfigure());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final Logger _logger = LogManager.getLogger(
		Log4jExtenderBundleActivator.class);

	private static CompositeConfiguration _compositeConfiguration;
	private static final List<XmlConfiguration> _configurations =
		Log4JUtil.getConfigurations();
	private static String _liferayHome;

	private volatile BundleTracker<Bundle> _bundleTracker;

}