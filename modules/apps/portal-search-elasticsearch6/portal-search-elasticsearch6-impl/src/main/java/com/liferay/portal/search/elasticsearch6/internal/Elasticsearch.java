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

package com.liferay.portal.search.elasticsearch6.internal;

import com.liferay.petra.process.ProcessExecutor;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.elasticsearch.sidecar.ElasticsearchBootstrap;
import com.liferay.portal.util.PropsValues;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dante Wang
 */
@Component(immediate = true, service = Elasticsearch.class)
public class Elasticsearch {

	@Activate
	protected void activate() throws Exception {
		List<String> additionalArguments = new ArrayList<>();

		additionalArguments.add("-Des.distribution.flavor=default");
		additionalArguments.add("-Des.distribution.type=zip");

		_elasticBootstrap = new ElasticsearchBootstrap(
			_processExecutor,
			new File(PropsValues.LIFERAY_HOME, "elasticsearch-6.8.2-1"),
			additionalArguments, _portal.getComputerName());

		_elasticBootstrap.start();
	}

	private ElasticsearchBootstrap _elasticBootstrap;

	@Reference
	private Portal _portal;

	@Reference
	private ProcessExecutor _processExecutor;

}