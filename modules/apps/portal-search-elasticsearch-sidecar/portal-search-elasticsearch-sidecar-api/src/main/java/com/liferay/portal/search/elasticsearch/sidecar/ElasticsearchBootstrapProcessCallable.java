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

package com.liferay.portal.search.elasticsearch.sidecar;

import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.local.LocalProcessLauncher;

import java.lang.reflect.Method;

/**
 * @author Dante Wang
 */
public class ElasticsearchBootstrapProcessCallable
	implements ProcessCallable<String> {

	public ElasticsearchBootstrapProcessCallable(String[] args) {
		_args = args;
	}

	@Override
	public String call() throws ProcessException {
		LocalProcessLauncher.ProcessContext.attach(
			"Elasticsearch", 10000,
			(shutdownCode, shutdownThrowable) -> {
				System.exit(shutdownCode);

				return true;
			});

		Thread currentThread = Thread.currentThread();

		ClassLoader classLoader = currentThread.getContextClassLoader();

		// ES entry point, the Elasticsearch class, is package private

		try {
			Class<?> esMainClass = classLoader.loadClass(
				"org.elasticsearch.bootstrap.Elasticsearch");

			Method method = esMainClass.getMethod("main", String[].class);

			method.setAccessible(true);

			// TODO: Hard code as no command line args for now

			method.invoke(null, new Object[] {_args});
		}
		catch (Exception e) {
			throw new ProcessException(e);
		}

		return null;
	}

	private static final long serialVersionUID = 1L;

	private final String[] _args;

}