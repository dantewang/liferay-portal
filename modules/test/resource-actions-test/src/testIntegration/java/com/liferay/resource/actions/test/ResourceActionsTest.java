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

package com.liferay.resource.actions.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.net.URL;

import java.util.Dictionary;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class ResourceActionsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAllXmlsAreRead() {
		Bundle currentBundle = FrameworkUtil.getBundle(
			ResourceActionsDefinitionTest.class);

		BundleContext bundleContext = currentBundle.getBundleContext();

		Bundle[] bundles = bundleContext.getBundles();

		for (Bundle bundle : bundles) {
			Dictionary<String, String> headers = bundle.getHeaders(
				StringPool.BLANK);

			if (headers.get("Liferay-Service") == null) {
				continue;
			}

			BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

			ClassLoader classLoader = bundleWiring.getClassLoader();

			URL url = classLoader.getResource("resource-actions/default.xml");

			if (url == null) {
				continue;
			}

			// Read from default.xml, collect model-names, and assert that
			// every model name you read from the file is in the list returned
			// by _resourceActions.getModelNames()

		}
	}

	@Inject
	private ResourceActions _resourceActions;

}