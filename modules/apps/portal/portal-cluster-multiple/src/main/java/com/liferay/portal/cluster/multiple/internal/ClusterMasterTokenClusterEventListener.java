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

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.portal.kernel.cluster.ClusterEvent;
import com.liferay.portal.kernel.cluster.ClusterEventListener;
import com.liferay.portal.kernel.cluster.ClusterEventType;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jiaxu Wei
 */
@Component(service = ClusterEventListener.class)
public class ClusterMasterTokenClusterEventListener
	implements ClusterEventListener {

	@Override
	public void processClusterEvent(ClusterEvent clusterEvent) {
		ClusterEventType clusterEventType = clusterEvent.getClusterEventType();

		if (clusterEventType == ClusterEventType.COORDINATOR_ADDRESS_UPDATE) {
			ClusterMasterExecutorImpl clusterMasterExecutorImpl =
				(ClusterMasterExecutorImpl)_clusterMasterExecutor;

			clusterMasterExecutorImpl.getMasterClusterNodeId(true);
		}
	}

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

}