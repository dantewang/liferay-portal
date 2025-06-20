/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.sidecar;

import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessException;

/**
 * @author Tina Tian
 */
public class StartSidecarProcessCallable implements ProcessCallable<String> {

	public StartSidecarProcessCallable(SidecarServerArgs sidecarServerArgs) {
		_sidecarServerArgs = sidecarServerArgs;
	}

	@Override
	public String call() throws ProcessException {
		Object nodeObject = ElasticsearchServerUtil.start(_sidecarServerArgs);

		try {
			Object injectorObject = ElasticsearchServerUtil.Reflection.invoke(
				"org.elasticsearch.node.Node", nodeObject, "injector");

			ClassLoader classLoader =
				StartSidecarProcessCallable.class.getClassLoader();

			Object httpServerTransportObject =
				ElasticsearchServerUtil.Reflection.invoke(
					"org.elasticsearch.injection.guice.Injector",
					injectorObject, "getInstance", new Class<?>[] {Class.class},
					classLoader.loadClass(
						"org.elasticsearch.http.HttpServerTransport"));

			Object boundAddressObject =
				ElasticsearchServerUtil.Reflection.invoke(
					"org.elasticsearch.http.HttpServerTransport",
					httpServerTransportObject, "boundAddress");

			Object publishAddressObject =
				ElasticsearchServerUtil.Reflection.invoke(
					boundAddressObject, "publishAddress");

			return publishAddressObject.toString();
		}
		catch (Exception exception) {
			throw new ProcessException(exception);
		}
	}

	private static final long serialVersionUID = 1L;

	private final SidecarServerArgs _sidecarServerArgs;

}