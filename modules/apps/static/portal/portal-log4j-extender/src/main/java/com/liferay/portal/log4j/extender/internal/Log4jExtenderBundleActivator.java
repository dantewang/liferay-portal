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
import java.io.InputStream;

import java.lang.reflect.Method;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.composite.CompositeConfiguration;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.wiring.BundleWire;
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
					_configureLog4j(bundle);
				}
				catch (Exception exception) {
					_logger.error(
						"Unable to configure Log4j for bundle " +
							bundle.getSymbolicName(),
						exception);
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

	private void _configureLog4j(Bundle bundle) throws Exception {
		File configFile = new File(
			StringBundler.concat(
				PropsValues.MODULE_FRAMEWORK_BASE_DIR, "/log4j/",
				bundle.getSymbolicName(), "-log4j-ext.xml"));

		if (!configFile.exists()) {
			return;
		}

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		_configureLog4j(
			bundleWiring.getClassLoader(), configFile,
			bundle.getSymbolicName());
	}

	private void _configureLog4j(Bundle bundle, String resourcePath)
		throws Exception {

		Enumeration<URL> enumeration = bundle.findEntries(
			"META-INF", resourcePath, false);

		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				Path path = Files.createTempFile(null, ".xml");

				String urlContent = _getURLContent(url);

				Files.write(path, urlContent.getBytes());

				BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

				ClassLoader bundleClassLoader = bundleWiring.getClassLoader();

				String symbolicName = bundle.getSymbolicName();

				if (bundleClassLoader == null) {
					List<BundleWire> bundleWire = bundleWiring.getRequiredWires(
						"osgi.wiring.host");

					BundleWire hostBundleWire = bundleWire.get(0);

					BundleWiring hostBundleWiring =
						hostBundleWire.getProviderWiring();

					bundleClassLoader = hostBundleWiring.getClassLoader();

					Bundle hostBundle = hostBundleWiring.getBundle();

					symbolicName = hostBundle.getSymbolicName();
				}

				_configureLog4j(bundleClassLoader, path.toFile(), symbolicName);
			}
		}
	}

	private void _configureLog4j(
			ClassLoader bundleClassLoader, File configFile, String symbolicName)
		throws Exception {

		List<XmlConfiguration> xmlConfigurationList = _xmlConfigurationsMap.get(
			bundleClassLoader);

		if (xmlConfigurationList == null) {
			xmlConfigurationList = new ArrayList<>();
		}
		else {
			List<XmlConfiguration> newXmlConfigurationList = new ArrayList<>();

			for (XmlConfiguration xmlConfiguration : xmlConfigurationList) {
				newXmlConfigurationList.add(
					(XmlConfiguration)xmlConfiguration.reconfigure());
			}

			xmlConfigurationList = newXmlConfigurationList;
		}

		LoggerContext loggerContext = null;

		if (xmlConfigurationList.isEmpty()) {
			loggerContext = Configurator.initialize(
				null, bundleClassLoader, configFile.toURI());

			xmlConfigurationList.add(
				(XmlConfiguration)loggerContext.getConfiguration());

			_loggerContextsMap.put(bundleClassLoader, loggerContext);
		}
		else {
			ConfigurationSource configurationSource = new ConfigurationSource(
				new FileInputStream(configFile), configFile);

			XmlConfigurationFactory xmlConfigurationFactory =
				new XmlConfigurationFactory();

			loggerContext = _loggerContextsMap.get(bundleClassLoader);

			XmlConfiguration xmlConfiguration =
				(XmlConfiguration)xmlConfigurationFactory.getConfiguration(
					loggerContext, configurationSource);

			xmlConfigurationList.add(xmlConfiguration);

			loggerContext.setConfiguration(
				new CompositeConfiguration(xmlConfigurationList));
		}

		Log4JUtil.setLoggerContexts(symbolicName, loggerContext);

		_xmlConfigurationsMap.put(bundleClassLoader, xmlConfigurationList);

		Configuration configuration = loggerContext.getConfiguration();

		LoggerConfig currentBundleRootLogger = configuration.getRootLogger();

		Method method = ReflectionUtil.getDeclaredMethod(
			LoggerConfig.class, "clearAppenders");

		method.invoke(currentBundleRootLogger);

		LoggerConfig portalRootLoggerConfig = Log4JUtil.getRootLogger();

		currentBundleRootLogger.setLevel(portalRootLoggerConfig.getLevel());

		Map<String, Appender> appenders = portalRootLoggerConfig.getAppenders();

		for (AppenderRef appenderRef :
				portalRootLoggerConfig.getAppenderRefs()) {

			Appender appender = appenders.get(appenderRef.getRef());

			currentBundleRootLogger.addAppender(
				appender, appenderRef.getLevel(), appenderRef.getFilter());
		}
	}

	private static final Logger _logger = LogManager.getLogger(
		Log4jExtenderBundleActivator.class);

	private static String _liferayHome;
	private static final Map<ClassLoader, LoggerContext> _loggerContextsMap =
		new ConcurrentHashMap<>();
	private static final Map<ClassLoader, List<XmlConfiguration>>
		_xmlConfigurationsMap = new ConcurrentHashMap<>();

	private volatile BundleTracker<Bundle> _bundleTracker;

}