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

package com.liferay.portal.poller.comet;

import com.liferay.portal.kernel.util.ServiceProxyFactory;

/**
 * @author Edward Han
 */
public class PollerCometDelayedJobUtil {

	public static void addPollerCometDelayedTask(
		PollerCometDelayedTask pollerCometDelayedTask) {

		getPollerCometDelayedJob().addPollerCometDelayedTask(
			pollerCometDelayedTask);
	}

	public static PollerCometDelayedJob getPollerCometDelayedJob() {
		return _pollerCometDelayedJob;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public void setPollerCometDelayedJob(
		PollerCometDelayedJob pollerCometDelayedJob) {

		_pollerCometDelayedJob = pollerCometDelayedJob;
	}

	private static volatile PollerCometDelayedJob _pollerCometDelayedJob =
		ServiceProxyFactory.newServiceTrackedInstance(
			PollerCometDelayedJob.class, PollerCometDelayedJobUtil.class,
			"_pollerCometDelayedJob", false);

}