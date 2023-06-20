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

package com.liferay.feature.flag.web.internal.company.feature.flags;

import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = CompanyFeatureFlagsProvider.class)
public class CompanyFeatureFlagsProvider {

	public CompanyFeatureFlags getOrCreateCompanyFeatureFlags(long companyId) {
		return _companyFeatureFlagsMap.computeIfAbsent(
			companyId, _companyFeatureFlagsFactory::create);
	}

	public void removeCompanyFeatureFlags(long companyId) {
		_removeCompanyFeatureFlags(companyId);

		if (!_clusterExecutor.isEnabled()) {
			return;
		}

		MethodHandler methodHandler = new MethodHandler(
			_removeCompanyFeatureFlagsMethodKey, companyId);

		ClusterRequest clusterRequest = ClusterRequest.createMulticastRequest(
			methodHandler, true);

		clusterRequest.setFireAndForget(true);

		_clusterExecutor.execute(clusterRequest);
	}

	public <T> T withCompanyFeatureFlags(
		long companyId, Function<CompanyFeatureFlags, T> function) {

		return function.apply(getOrCreateCompanyFeatureFlags(companyId));
	}

	private static void _removeCompanyFeatureFlags(long companyId) {
		_companyFeatureFlagsMap.remove(companyId);
	}

	private static final Map<Long, CompanyFeatureFlags>
		_companyFeatureFlagsMap = new ConcurrentHashMap<>();
	private static final MethodKey _removeCompanyFeatureFlagsMethodKey =
		new MethodKey(
			CompanyFeatureFlagsProvider.class, "_removeCompanyFeatureFlags",
			long.class);

	@Reference
	private ClusterExecutor _clusterExecutor;

	@Reference
	private CompanyFeatureFlagsFactory _companyFeatureFlagsFactory;

}