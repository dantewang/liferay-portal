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

/**
 * @author Dante Wang
 */
public class CentralizedConfiguration extends AbstractConfiguration {

	public CentralizedConfiguration(LoggerContext loggerContext) {
		super(loggerContext, ConfigurationSource.COMPOSITE_SOURCE);
	}

	public void addConfiguration(AbstractConfiguration configuration) {
		if (configuration.getState() != State.INITIALIZING) {
			return;
		}

		configuration.initialize();

		// DefaultMergeStrategy:
		// Properties from all configurations are aggregated.
		// Duplicate properties replace those in previous configurations.

		Map<String, String> properties = getProperties();

		properties.putAll(configuration.getProperties());

		_aggregateAppenders(configuration);
		_aggregateFilters(configuration);
		_aggregateLoggerConfigs(configuration);

		_updateLoggers();
	}

	@Override
	public void start() {
		LoggerConfig rootLoggerConfig = getRootLogger();

		rootLoggerConfig.start();

		addLogger("", rootLoggerConfig);

		setStarted();
	}

	private void _aggregateAppenders(AbstractConfiguration configuration) {

		// DefaultMergeStrategy:
		// Appenders are aggregated.
		// Appenders with the same name are replaced by those in later
		// configurations, including all of the Appender's subcomponents.

		Map<String, Appender> newAppenders = configuration.getAppenders();

		for (Map.Entry<String, Appender> newAppenderEntry :
				newAppenders.entrySet()) {

			// Always call removeAppender(String) to try to remove the Appender
			// with the same name from logger configs and stop it

			removeAppender(newAppenderEntry.getKey());

			Appender newAppender = newAppenderEntry.getValue();

			newAppender.start();

			addAppender(newAppender);
		}
	}

	private void _aggregateFilters(AbstractConfiguration configuration) {

		// DefaultMergeStrategy:
		// Filters are aggregated under a CompositeFilter if more than one
		// Filter is defined. Since Filters are not named duplicates may be
		// present.

		Filter newFilter = configuration.getFilter();

		if (newFilter != null) {
			newFilter.start();
		}

		addFilter(newFilter);
	}

	private void _aggregateLoggerConfigContent(
		LoggerConfig currentLoggerConfig, LoggerConfig newLoggerConfig) {

		if (newLoggerConfig == null) {
			return;
		}

		// Logger attributes are individually merged with duplicates being
		// replaced by those in later configurations.

		currentLoggerConfig.setLevel(newLoggerConfig.getLevel());
		currentLoggerConfig.setAdditive(newLoggerConfig.isAdditive());

		// Filters on a Logger are aggregated under a CompositeFilter if more
		// than one Filter is defined. Since Filters are not named duplicates
		// may be present.

		Filter newFilter = newLoggerConfig.getFilter();

		if (newFilter != null) {
			newFilter.start();
		}

		currentLoggerConfig.addFilter(newFilter);

		// Appender references on a Logger are aggregated with duplicates being
		// replaced by those in later configurations.
		// Filters under Appender references included or discarded depending on
		// whether their parent Appender reference is kept or discarded.

		Map<String, Appender> currentLoggerConfigAppenders =
			currentLoggerConfig.getAppenders();

		Map<String, Appender> newLoggerConfigAppenders =
			newLoggerConfig.getAppenders();

		for (AppenderRef appenderRef : newLoggerConfig.getAppenderRefs()) {
			Appender appender = currentLoggerConfigAppenders.get(
				appenderRef.getRef());

			// Existing appender must be removed first as the internal data
			// structure holding appenders does not allow replacing an existing
			// appender

			if (appender != null) {
				currentLoggerConfig.removeAppender(appenderRef.getRef());
			}

			currentLoggerConfig.addAppender(
				newLoggerConfigAppenders.get(appenderRef.getRef()),
				appenderRef.getLevel(), appenderRef.getFilter());
		}
	}

	private void _aggregateLoggerConfigs(AbstractConfiguration configuration) {

		// DefaultMergeStrategy:
		// Loggers are all aggregated.
		// See _aggregateLoggerConfigContent(LoggerConfig, LoggerConfig)

		_aggregateLoggerConfigContent(
			getRootLogger(),
			configuration.getLogger(LogManager.ROOT_LOGGER_NAME));

		Map<String, LoggerConfig> newLoggerConfigs = configuration.getLoggers();

		for (Map.Entry<String, LoggerConfig> newLoggerConfigEntry :
				newLoggerConfigs.entrySet()) {

			String name = newLoggerConfigEntry.getKey();

			// Skip root logger

			if (Objects.equals(name, LogManager.ROOT_LOGGER_NAME)) {
				continue;
			}

			LoggerConfig currentLoggerConfig = getLogger(name);

			LoggerConfig newLoggerConfig = newLoggerConfigEntry.getValue();

			if (currentLoggerConfig != null) {
				_aggregateLoggerConfigContent(
					currentLoggerConfig, newLoggerConfig);

				continue;
			}

			addLogger(name, newLoggerConfig);

			newLoggerConfig.start();
		}
	}

	private void _updateLoggers() {

		// TODO: lock the configLock in LoggerContext

		LoggerContext loggerContext = getLoggerContext();

		loggerContext.updateLoggers();
	}

}