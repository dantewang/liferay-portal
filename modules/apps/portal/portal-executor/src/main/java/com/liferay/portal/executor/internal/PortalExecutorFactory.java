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

package com.liferay.portal.executor.internal;

import com.liferay.petra.concurrent.ThreadPoolHandlerAdapter;
import com.liferay.petra.executor.PortalExecutorConfig;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.concurrent.AbortPolicy;
import com.liferay.portal.kernel.concurrent.ClearThreadLocalThreadPoolHandler;
import com.liferay.portal.kernel.concurrent.ThreadPoolExecutor;
import com.liferay.portal.kernel.util.NamedThreadFactory;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Shuyang Zhou
 */
@Component(service = PortalExecutorFactory.class)
public class PortalExecutorFactory {

	public static final String DEFAULT_CONFIG_NAME = "default";

	public ThreadPoolExecutor createPortalExecutor(String executorName) {
		PortalExecutorConfig portalExecutorConfig = getPortalExecutorConfig(
			executorName);

		return new ThreadPoolExecutor(
			portalExecutorConfig.getCorePoolSize(),
			portalExecutorConfig.getMaxPoolSize(),
			portalExecutorConfig.getKeepAliveTime(),
			portalExecutorConfig.getTimeUnit(), true,
			portalExecutorConfig.getMaxQueueSize(), new AbortPolicy(),
			portalExecutorConfig.getThreadFactory(),
			new ClearThreadLocalThreadPoolHandler());
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void addPortalExecutorConfig(
		PortalExecutorConfig portalExecutorConfig) {

		_portalExecutorConfigs.putIfAbsent(
			portalExecutorConfig.getName(), portalExecutorConfig);
	}

	protected PortalExecutorConfig getPortalExecutorConfig(String name) {
		PortalExecutorConfig portalExecutorConfig = _portalExecutorConfigs.get(
			name);

		if (portalExecutorConfig != null) {
			return portalExecutorConfig;
		}

		portalExecutorConfig = _portalExecutorConfigs.get(DEFAULT_CONFIG_NAME);

		if (portalExecutorConfig != null) {
			return portalExecutorConfig;
		}

		return _defaultPortalExecutorConfig;
	}

	protected void removePortalExecutorConfig(
		PortalExecutorConfig portalExecutorConfig) {

		_portalExecutorConfigs.remove(
			portalExecutorConfig.getName(), portalExecutorConfig);
	}

	private final PortalExecutorConfig _defaultPortalExecutorConfig =
		new PortalExecutorConfig(
			DEFAULT_CONFIG_NAME, 0, 10, 60, TimeUnit.SECONDS, Integer.MAX_VALUE,
			new NamedThreadFactory(
				DEFAULT_CONFIG_NAME, Thread.NORM_PRIORITY,
				PortalClassLoaderUtil.getClassLoader()),
			new java.util.concurrent.ThreadPoolExecutor.AbortPolicy(),
			new ThreadPoolHandlerAdapter() {

				@Override
				public void afterExecute(
					Runnable runnable, Throwable throwable) {

					CentralizedThreadLocal.clearShortLivedThreadLocals();
				}

			});

	private final ConcurrentMap<String, PortalExecutorConfig>
		_portalExecutorConfigs = new ConcurrentHashMap<>();

}