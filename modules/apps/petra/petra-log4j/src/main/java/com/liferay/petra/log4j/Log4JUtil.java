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

package com.liferay.petra.log4j;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.LogFactory;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.composite.CompositeConfiguration;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author Brian Wing Shun Chan
 * @author Tomas Polesovsky
 */
public class Log4JUtil {

	public static void configureLog4J(ClassLoader classLoader) {
		configureLog4J(classLoader.getResource("META-INF/portal-log4j.xml"));

		try {
			Enumeration<URL> enumeration = classLoader.getResources(
				"META-INF/portal-log4j-ext.xml");

			while (enumeration.hasMoreElements()) {
				configureLog4J(enumeration.nextElement());
			}
		}
		catch (IOException ioException) {
			java.util.logging.Logger logger =
				java.util.logging.Logger.getLogger(Log4JUtil.class.getName());

			logger.log(
				java.util.logging.Level.WARNING,
				"Unable to load portal-log4j-ext.xml", ioException);
		}
	}

	public static void configureLog4J(URL url) {
		if (url == null) {
			return;
		}

		String urlContent = _getURLContent(url);

		if (urlContent == null) {
			return;
		}

		try {
			if (_xmlConfigurationList == null) {
				_xmlConfigurationList = new ArrayList<>();
			}
			else {
				List<XmlConfiguration> newXmlConfigurationList =
					new ArrayList<>();

				for (XmlConfiguration xmlConfiguration :
						_xmlConfigurationList) {

					newXmlConfigurationList.add(
						(XmlConfiguration)xmlConfiguration.reconfigure());
				}

				_xmlConfigurationList = newXmlConfigurationList;
			}

			Path path = Files.createTempFile(null, ".xml");

			Files.write(path, urlContent.getBytes());

			if (_loggerContext == null) {
				ClassLoader portalClassLoader =
					Log4JUtil.class.getClassLoader();

				_loggerContext = Configurator.initialize(
					null, portalClassLoader.getParent(), path.toUri());

				_xmlConfigurationList.add(
					(XmlConfiguration)_loggerContext.getConfiguration());
			}
			else {
				File file = path.toFile();

				ConfigurationSource configurationSource =
					new ConfigurationSource(new FileInputStream(file), file);

				XmlConfigurationFactory xmlConfigurationFactory =
					new XmlConfigurationFactory();

				XmlConfiguration xmlConfiguration =
					(XmlConfiguration)xmlConfigurationFactory.getConfiguration(
						_loggerContext, configurationSource);

				_xmlConfigurationList.add(xmlConfiguration);

				CompositeConfiguration compositeConfiguration =
					new CompositeConfiguration(_xmlConfigurationList);

				_loggerContext.setConfiguration(compositeConfiguration);
			}

			Configuration configuration = _loggerContext.getConfiguration();

			_rootLoggerConfig = configuration.getRootLogger();

			_loggerContexts.put(_PORTAT_SYMBOLICNAME, _loggerContext);

			SAXReader saxReader = new SAXReader();

			Document document = saxReader.read(
				new UnsyncStringReader(urlContent), url.toExternalForm());

			Element rootElement = document.getRootElement();

			Element loggersElement = rootElement.element("Loggers");

			List<Element> loggerElements = loggersElement.elements("Logger");

			for (Element loggerElement : loggerElements) {
				String name = loggerElement.attributeValue("name");

				String priority = loggerElement.attributeValue("level");

				java.util.logging.Logger jdkLogger =
					java.util.logging.Logger.getLogger(name);

				jdkLogger.setLevel(_getJdkLevel(priority));
			}
		}
		catch (Exception exception) {
			_logger.error(exception, exception);
		}
	}

	public static List<org.apache.logging.log4j.core.Logger> getAllLoggers() {
		List<org.apache.logging.log4j.core.Logger> allLoggers =
			new ArrayList<>();

		Collection<LoggerContext> loggerContexts = _loggerContexts.values();

		Iterator<LoggerContext> iterator = loggerContexts.iterator();

		while (iterator.hasNext()) {
			LoggerContext loggerContext = iterator.next();

			allLoggers.addAll(loggerContext.getLoggers());
		}

		return ListUtil.sort(allLoggers, new LoggerNameComparator());
	}

	public static Map<String, String> getCustomLogSettings() {
		return new HashMap<>(_customLogSettings);
	}

	public static String getOriginalLevel(String className) {
		Set<String> symbolicNames = _loggerContexts.keySet();

		Iterator<String> iterator = symbolicNames.iterator();

		while (iterator.hasNext()) {
			String symbolicName = iterator.next();

			if (symbolicName.equals(_PORTAT_SYMBOLICNAME)) {
				continue;
			}

			if (className.startsWith(symbolicName)) {
				return _getLog4jLevel(className, symbolicName);
			}
		}

		return _getLog4jLevel(className, _PORTAT_SYMBOLICNAME);
	}

	public static LoggerConfig getRootLogger() {
		return _rootLoggerConfig;
	}

	public static void initLog4J(
		String serverId, String liferayHome, ClassLoader classLoader,
		LogFactory logFactory, Map<String, String> customLogSettings) {

		System.setProperty(
			ServerDetector.SYSTEM_PROPERTY_KEY_SERVER_DETECTOR_SERVER_ID,
			serverId);

		_liferayHome = _escapeXMLAttribute(liferayHome);

		configureLog4J(classLoader);

		try {
			LogFactoryUtil.setLogFactory(logFactory);
		}
		catch (Exception exception) {
			_logger.error(exception, exception);
		}

		for (Map.Entry<String, String> entry : customLogSettings.entrySet()) {
			setLevel(entry.getKey(), entry.getValue(), false);
		}
	}

	public static void setLevel(String name, String priority, boolean custom) {
		org.apache.logging.log4j.core.Logger logger = null;

		LoggerContext loggerContext = null;

		Set<String> symbolicNames = _loggerContexts.keySet();

		Iterator<String> iterator = symbolicNames.iterator();

		while (iterator.hasNext()) {
			String symbolicName = iterator.next();

			if (symbolicName.equals(_PORTAT_SYMBOLICNAME)) {
				continue;
			}

			if (name.startsWith(symbolicName)) {
				loggerContext = _loggerContexts.get(symbolicName);

				logger = loggerContext.getLogger(name);

				break;
			}
		}

		if (logger == null) {
			loggerContext = _loggerContexts.get(_PORTAT_SYMBOLICNAME);

			logger = loggerContext.getLogger(name);
		}

		logger.setLevel(Level.toLevel(priority));

		java.util.logging.Logger jdkLogger = java.util.logging.Logger.getLogger(
			name);

		jdkLogger.setLevel(_getJdkLevel(priority));

		if (custom) {
			_customLogSettings.put(name, priority);
		}
	}

	public static void setLoggerContexts(
		String symbolicName, LoggerContext loggerContext) {

		_loggerContexts.put(symbolicName, loggerContext);
	}

	public static void shutdownLog4J() {
		Collection<LoggerContext> loggerContexts = _loggerContexts.values();

		Iterator<LoggerContext> iterator = loggerContexts.iterator();

		while (iterator.hasNext()) {
			LoggerContext loggerContext = iterator.next();

			LogManager.shutdown(loggerContext);
		}
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

	/**
	 * @see com.liferay.portal.util.FileImpl#getBytes(InputStream, int, boolean)
	 */
	private static byte[] _getBytes(InputStream inputStream)
		throws IOException {

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		StreamUtil.transfer(inputStream, unsyncByteArrayOutputStream, -1, true);

		return unsyncByteArrayOutputStream.toByteArray();
	}

	private static java.util.logging.Level _getJdkLevel(String priority) {
		if (StringUtil.equalsIgnoreCase(priority, Level.DEBUG.toString())) {
			return java.util.logging.Level.FINE;
		}
		else if (StringUtil.equalsIgnoreCase(
					priority, Level.ERROR.toString())) {

			return java.util.logging.Level.SEVERE;
		}
		else if (StringUtil.equalsIgnoreCase(priority, Level.WARN.toString())) {
			return java.util.logging.Level.WARNING;
		}

		return java.util.logging.Level.INFO;
	}

	private static String _getLiferayHome() {
		if (_liferayHome == null) {
			_liferayHome = _escapeXMLAttribute(
				PropsUtil.get(PropsKeys.LIFERAY_HOME));
		}

		return _liferayHome;
	}

	private static String _getLog4jLevel(
		String className, String symbolicName) {

		LoggerContext loggerContext = _loggerContexts.get(symbolicName);

		org.apache.logging.log4j.core.Logger logger = loggerContext.getLogger(
			className);

		Level level = logger.getLevel();

		return level.toString();
	}

	private static String _getURLContent(URL url) {
		String urlContent = null;

		try (InputStream inputStream = url.openStream()) {
			byte[] bytes = _getBytes(inputStream);

			urlContent = new String(bytes, StringPool.UTF8);
		}
		catch (Exception exception) {
			_logger.error(exception, exception);

			return null;
		}

		urlContent = StringUtil.replace(
			urlContent, "@liferay.home@", _getLiferayHome());

		if (ServerDetector.getServerId() != null) {
			return urlContent;
		}

		urlContent = _removeAppender(urlContent, "TEXT_FILE");

		return _removeAppender(urlContent, "XML_FILE");
	}

	private static String _removeAppender(String content, String appenderName) {
		int x = content.indexOf("<appender name=\"" + appenderName + "\"");

		int y = content.indexOf("</appender>", x);

		if (y != -1) {
			y = content.indexOf("<", y + 1);
		}

		if ((x != -1) && (y != -1)) {
			content = content.substring(0, x) + content.substring(y);
		}

		return StringUtil.removeSubstring(
			content, "<appender-ref ref=\"" + appenderName + "\" />");
	}

	private static final String _PORTAT_SYMBOLICNAME = "portal.symbolicname";

	private static final Logger _logger = LogManager.getRootLogger();

	private static final Map<String, String> _customLogSettings =
		new ConcurrentHashMap<>();
	private static String _liferayHome;
	private static LoggerContext _loggerContext;
	private static final Map<String, LoggerContext> _loggerContexts =
		new ConcurrentHashMap<>();
	private static LoggerConfig _rootLoggerConfig;
	private static List<XmlConfiguration> _xmlConfigurationList;

	private static class LoggerNameComparator
		implements Comparator<org.apache.logging.log4j.core.Logger> {

		@Override
		public int compare(
			org.apache.logging.log4j.core.Logger logger1,
			org.apache.logging.log4j.core.Logger logger2) {

			String name1 = logger1.getName();
			String name2 = logger2.getName();

			return name1.compareTo(name2);
		}

	}

}