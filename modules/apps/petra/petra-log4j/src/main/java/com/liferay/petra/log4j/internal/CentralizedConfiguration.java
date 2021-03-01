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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.filter.AbstractFilterable;

/**
 * @author Dante Wang
 * @see org.apache.logging.log4j.core.config.composite.DefaultMergeStrategy
 */
public class CentralizedConfiguration extends AbstractConfiguration {

	public CentralizedConfiguration() {
		super(
			(LoggerContext)LogManager.getContext(),
			ConfigurationSource.COMPOSITE_SOURCE);

		PluginManager.addPackage("com.liferay.petra.log4j");

		LoggerContext loggerContext = getLoggerContext();

		loggerContext.setConfiguration(this);
	}

	public void addConfiguration(AbstractConfiguration abstractConfiguration) {
		if (abstractConfiguration.getState() != State.INITIALIZING) {
			return;
		}

		abstractConfiguration.initialize();

		Map<String, String> properties = getProperties();

		properties.putAll(abstractConfiguration.getProperties());

		_aggregateAppenders(abstractConfiguration);

		_aggregateFilter(this, abstractConfiguration);

		_aggregateLoggerConfigs(abstractConfiguration);

		LoggerContext loggerContext = getLoggerContext();

		loggerContext.updateLoggers();
	}

	@Override
	public void start() {
		LoggerConfig rootLoggerConfig = getRootLogger();

		rootLoggerConfig.start();

		addLogger(LogManager.ROOT_LOGGER_NAME, rootLoggerConfig);

		setStarted();
	}

	private void _aggregateAppenders(
		AbstractConfiguration abstractConfiguration) {

		Map<String, Appender> currentAppenders = getAppenders();

		Map<String, Appender> newAppenders =
			abstractConfiguration.getAppenders();

		for (Map.Entry<String, Appender> newAppenderEntry :
				newAppenders.entrySet()) {

			Appender newAppender = newAppenderEntry.getValue();

			newAppender.start();

			String appenderName = newAppenderEntry.getKey();

			if (!currentAppenders.containsKey(appenderName)) {
				addAppender(newAppender);

				continue;
			}

			Map<String, LoggerConfig> loggerConfigMap = getLoggers();

			for (LoggerConfig loggerConfig : loggerConfigMap.values()) {
				loggerConfig.removeAppender(appenderName);

				for (AppenderRef appenderRef : loggerConfig.getAppenderRefs()) {
					if (Objects.equals(appenderRef.getRef(), appenderName)) {
						loggerConfig.addAppender(
							newAppender, appenderRef.getLevel(),
							appenderRef.getFilter());

						break;
					}
				}
			}

			Appender oldAppender = currentAppenders.put(
				appenderName, newAppender);

			oldAppender.stop();
		}
	}

	private void _aggregateFilter(
		AbstractFilterable currentAbstractFilterable,
		AbstractFilterable newAbstractFilterable) {

		Filter newFilter = newAbstractFilterable.getFilter();

		if (newFilter != null) {
			newFilter.start();

			currentAbstractFilterable.addFilter(newFilter);
		}
	}

	private void _aggregateLoggerConfigs(
		AbstractConfiguration abstractConfiguration) {

		_mergeLoggerConfig(
			getRootLogger(),
			abstractConfiguration.getLogger(LogManager.ROOT_LOGGER_NAME));

		Map<String, LoggerConfig> newLoggerConfigs =
			abstractConfiguration.getLoggers();

		for (Map.Entry<String, LoggerConfig> newLoggerConfigEntry :
				newLoggerConfigs.entrySet()) {

			String name = newLoggerConfigEntry.getKey();

			if (Objects.equals(name, LogManager.ROOT_LOGGER_NAME)) {
				continue;
			}

			LoggerConfig currentLoggerConfig = getLogger(name);

			LoggerConfig newLoggerConfig = newLoggerConfigEntry.getValue();

			if (currentLoggerConfig != null) {
				_mergeLoggerConfig(currentLoggerConfig, newLoggerConfig);

				continue;
			}

			addLogger(name, newLoggerConfig);

			newLoggerConfig.start();
		}
	}

	private void _mergeLoggerConfig(
		LoggerConfig currentLoggerConfig, LoggerConfig newLoggerConfig) {

		if (newLoggerConfig == null) {
			return;
		}

		currentLoggerConfig.setLevel(newLoggerConfig.getLevel());
		currentLoggerConfig.setAdditive(newLoggerConfig.isAdditive());

		_aggregateFilter(currentLoggerConfig, newLoggerConfig);

		Map<String, Appender> currentLoggerConfigAppenders =
			currentLoggerConfig.getAppenders();

		Map<String, Appender> newLoggerConfigAppenders =
			newLoggerConfig.getAppenders();

		List<AppenderRef> currentAppenderRefs =
			currentLoggerConfig.getAppenderRefs();

		for (AppenderRef newAppenderRef : newLoggerConfig.getAppenderRefs()) {
			Appender appender = currentLoggerConfigAppenders.get(
				newAppenderRef.getRef());

			if (appender != null) {
				currentLoggerConfig.removeAppender(newAppenderRef.getRef());

				Iterator<AppenderRef> currentAppenderRefIterator =
					currentAppenderRefs.iterator();

				while (currentAppenderRefIterator.hasNext()) {
					AppenderRef currentAppenderRef =
						currentAppenderRefIterator.next();

					if (Objects.equals(
							currentAppenderRef.getRef(),
							newAppenderRef.getRef())) {

						currentAppenderRefIterator.remove();

						break;
					}
				}
			}

			currentLoggerConfig.addAppender(
				newLoggerConfigAppenders.get(newAppenderRef.getRef()),
				newAppenderRef.getLevel(), newAppenderRef.getFilter());

			currentAppenderRefs.add(newAppenderRef);
		}
	}

}