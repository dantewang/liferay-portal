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
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.InputStream;

import java.lang.reflect.Method;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.composite.CompositeConfiguration;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.selector.ContextSelector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.util.tracker.BundleTracker;

/**
 * @author Shuyang Zhou
 */
public class Log4jExtenderBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		_bundleContext = bundleContext;

		_bundleTracker = new BundleTracker<LoggerContext>(
			bundleContext, ~(Bundle.INSTALLED | Bundle.UNINSTALLED), null) {

			@Override
			public LoggerContext addingBundle(
				Bundle bundle, BundleEvent bundleEvent) {

				List<Bundle> bundles = new ArrayList<>();

				bundles.add(bundle);

				BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

				ClassLoader bundleClassLoader = bundleWiring.getClassLoader();

				if (bundleClassLoader == null) {
					List<BundleWire> bundleWire = bundleWiring.getRequiredWires(
						"osgi.wiring.host");

					BundleWire hostBundleWire = bundleWire.get(0);

					BundleWiring hostBundleWiring =
						hostBundleWire.getProviderWiring();

					bundleClassLoader = hostBundleWiring.getClassLoader();

					bundles.add(0, hostBundleWiring.getBundle());
				}

				try {
					return _configureLog4J(
						bundleClassLoader, _collectURLs(bundles));
				}
				catch (Exception exception) {
					_logger.error(
						"Unable to configure Log4j for bundle " +
							bundle.getSymbolicName(),
						exception);
				}

				return null;
			}

			@Override
			public void removedBundle(
				Bundle bundle, BundleEvent event, LoggerContext loggerContext) {

				BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

				ClassLoader bundleClassLoader = bundleWiring.getClassLoader();

				if ((loggerContext != null) && (bundleClassLoader != null)) {
					ServiceRegistration<LoggerConfig> serviceRegistration =
						_serviceRegistrations.remove(bundleClassLoader);

					serviceRegistration.unregister();

					LogManager.shutdown(loggerContext);
				}
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

	private List<URL> _collectURLs(List<Bundle> bundles)
		throws MalformedURLException {

		List<URL> urls = new ArrayList<>();

		for (Bundle bundle : bundles) {
			Enumeration<URL> enumeration = bundle.findEntries(
				"META-INF", "module-log4j.xml", false);

			if (enumeration != null) {
				while (enumeration.hasMoreElements()) {
					urls.add(enumeration.nextElement());
				}
			}

			enumeration = bundle.findEntries(
				"META-INF", "module-log4j-ext.xml", false);

			if (enumeration != null) {
				while (enumeration.hasMoreElements()) {
					urls.add(enumeration.nextElement());
				}
			}

			File configFile = new File(
				StringBundler.concat(
					PropsValues.MODULE_FRAMEWORK_BASE_DIR, "/log4j/",
					bundle.getSymbolicName(), "-log4j-ext.xml"));

			if (configFile.exists()) {
				Path path = configFile.toPath();

				URI uri = path.toUri();

				urls.add(uri.toURL());
			}
		}

		return urls;
	}

	private LoggerContext _configureLog4J(
			ClassLoader bundleClassLoader, List<URL> urls)
		throws Exception {

		if (urls.isEmpty()) {
			return null;
		}

		Log4jContextFactory loggerContextFactory =
			(Log4jContextFactory)LogManager.getFactory();

		ContextSelector contextSelector = loggerContextFactory.getSelector();

		URL configLocationURL = urls.get(0);

		LoggerContext loggerContext = contextSelector.getContext(
			null, bundleClassLoader, false, configLocationURL.toURI());

		List<XmlConfiguration> configurations = new ArrayList<>();

		for (URL url : urls) {
			String urlContent = _getURLContent(url);

			if (urlContent == null) {
				continue;
			}

			ConfigurationSource configurationSource = new ConfigurationSource(
				new UnsyncByteArrayInputStream(
					urlContent.getBytes(StringPool.UTF8)));

			XmlConfigurationFactory xmlConfigurationFactory =
				new XmlConfigurationFactory();

			configurations.add(
				(XmlConfiguration)xmlConfigurationFactory.getConfiguration(
					loggerContext, configurationSource));
		}

		if (configurations.isEmpty()) {
			return null;
		}

		CompositeConfiguration compositeConfiguration =
			new CompositeConfiguration(configurations);

		if (loggerContext.getState() == LifeCycle.State.INITIALIZED) {
			loggerContext.start(compositeConfiguration);
		}
		else {
			loggerContext.setConfiguration(compositeConfiguration);
		}

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

		_registerLoggerConfigService(
			currentBundleRootLogger, bundleClassLoader);

		return loggerContext;
	}

	private void _registerLoggerConfigService(
		LoggerConfig currentBundleRootLogger, ClassLoader bundleClassLoader) {

		ServiceRegistration<LoggerConfig> serviceRegistration =
			_serviceRegistrations.get(bundleClassLoader);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}

		serviceRegistration = _bundleContext.registerService(
			LoggerConfig.class, currentBundleRootLogger,
			new HashMapDictionary<>());

		_serviceRegistrations.put(bundleClassLoader, serviceRegistration);
	}

	private static final Logger _logger = LogManager.getLogger(
		Log4jExtenderBundleActivator.class);

	private static BundleContext _bundleContext;
	private static String _liferayHome;
	private static final Map<ClassLoader, ServiceRegistration<LoggerConfig>>
		_serviceRegistrations = new ConcurrentHashMap<>();

	private volatile BundleTracker<LoggerContext> _bundleTracker;

}