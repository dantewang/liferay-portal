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

import java.util.List;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.config.composite.DefaultMergeStrategy;
import org.apache.logging.log4j.core.config.composite.MergeStrategy;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.script.ScriptManager;

/**
 * @author Dante Wang
 */
public class CentralizedConfigurator implements Reconfigurable {

	public CentralizedConfigurator(LoggerContext loggerContext) {
		_loggerContext = loggerContext;

		_rootNode = new Node();

		DefaultConfiguration defaultConfiguration = new DefaultConfiguration();

		defaultConfiguration.initialize();

		_pluginManager = defaultConfiguration.getPluginManager();

		_scriptManager = defaultConfiguration.getScriptManager();
	}

	public void addConfiguration(AbstractConfiguration configuration) {
		_mergeConfiguration(configuration);

		_loggerContext.onChange(this);
	}

	public void addConfigurations(List<AbstractConfiguration> configurations) {
		for (AbstractConfiguration configuration : configurations) {
			_mergeConfiguration(configuration);
		}

		_loggerContext.onChange(this);
	}

	@Override
	public Configuration reconfigure() {
		return new CentralizedConfiguration(
			_loggerContext, new Node(_rootNode));
	}

	private void _mergeConfiguration(AbstractConfiguration configuration) {
		_mergeStrategy.mergeRootProperties(_rootNode, configuration);

		configuration.setPluginManager(_pluginManager);

		configuration.setScriptManager(_scriptManager);

		configuration.setup();

		final Node sourceRootNode = configuration.getRootNode();

		_mergeStrategy.mergConfigurations(
			_rootNode, sourceRootNode, _pluginManager);
	}

	private final LoggerContext _loggerContext;
	private final MergeStrategy _mergeStrategy = new DefaultMergeStrategy();
	private final PluginManager _pluginManager;
	private final Node _rootNode;
	private final ScriptManager _scriptManager;

}