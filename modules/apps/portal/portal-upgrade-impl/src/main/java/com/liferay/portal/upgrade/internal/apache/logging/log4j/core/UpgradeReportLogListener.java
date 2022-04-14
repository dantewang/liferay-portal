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

package com.liferay.portal.upgrade.internal.apache.logging.log4j.core;

import com.liferay.portal.kernel.log.LogListener;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.upgrade.internal.release.osgi.commands.ReleaseManagerOSGiCommands;
import com.liferay.portal.upgrade.internal.report.UpgradeReport;

import java.util.Objects;

import org.apache.felix.cm.PersistenceManager;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

/**
 * @author Sam Ziemer
 */
@Component(
	immediate = true, property = "log.listener.name=UpgradeReportLogListener",
	service = LogListener.class
)
public class UpgradeReportLogListener implements LogListener {

	@Override
	public boolean isStarted() {
		return _started;
	}

	@Override
	public void onLogged(
		String level, String loggerName, String formattedMessage) {

		if (level.equals("INFO")) {
			if (Objects.equals(loggerName, UpgradeProcess.class.getName()) &&
				formattedMessage.startsWith("Completed upgrade process ")) {

				_upgradeReport.addEventMessage(loggerName, formattedMessage);
			}
		}
		else if (level.equals("WARN")) {
			_upgradeReport.addWarningMessage(loggerName, formattedMessage);
		}
		else if (level.equals("ERROR")) {
			_upgradeReport.addErrorMessage(loggerName, formattedMessage);
		}
	}

	@Override
	public void start() {
		_started = true;

		_upgradeReport = new UpgradeReport();
	}

	@Override
	public void stop() {
		if (_started) {
			_upgradeReport.generateReport(
				_persistenceManager, _releaseManagerOSGiCommands);

			_upgradeReport = null;
		}

		_started = false;
	}

	@Reference
	private PersistenceManager _persistenceManager;

	@Reference(cardinality = ReferenceCardinality.OPTIONAL)
	private volatile ReleaseManagerOSGiCommands _releaseManagerOSGiCommands;

	private volatile boolean _started;
	private volatile UpgradeReport _upgradeReport;

}