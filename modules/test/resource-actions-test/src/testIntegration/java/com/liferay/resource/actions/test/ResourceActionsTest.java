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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.net.URL;

import java.util.Dictionary;
import java.util.List;

import org.junit.Assert;
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
	public void testAllXmlsAreRead() throws Exception {
		Bundle currentBundle = FrameworkUtil.getBundle(
			ResourceActionsDefinitionTest.class);

		BundleContext bundleContext = currentBundle.getBundleContext();

		Bundle[] bundles = bundleContext.getBundles();

		StringBundler sb = new StringBundler();

		List<String> modelNamesList = _resourceActions.getModelNames();

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
				_log.info(
					"\n\t\t" + bundle.getSymbolicName() +
						": resource-actions/default.xml is not found");

				continue;
			}

			Document document = null;

			try (InputStream inputStream = url.openStream()) {
				document = UnsecureSAXReaderUtil.read(inputStream, false);
			}

			Element rootElement = document.getRootElement();

			for (Element resourceElement :
					rootElement.elements("model-resource")) {

				for (Element modelNameElement :
						resourceElement.elements("model-name")) {

					String modelName = modelNameElement.getTextTrim();

					if (!modelNamesList.contains(modelName)) {
						sb.append("\n\t\t");
						sb.append(bundle.getSymbolicName());
						sb.append(" ");
						sb.append(modelName);
						sb.append(": is not found in the modelNamesList.");
					}
				}

				for (Element compositeModelNameElement :
						resourceElement.elements("composite-model-name")) {

					String compositeModelName = ReflectionTestUtil.invoke(
						_resourceActions, "_getCompositeModelName",
						new Class<?>[] {Element.class},
						compositeModelNameElement);

					if (!modelNamesList.contains(compositeModelName)) {
						sb.append("\n\t\t");
						sb.append(bundle.getSymbolicName());
						sb.append(" ");
						sb.append(compositeModelName);
						sb.append(": is not found in the modelNamesList.");
					}
				}
			}

			String errors = sb.toString();

			Assert.assertTrue(
				"The following resource action issues are found:".concat(
					errors),
				errors.isEmpty());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceActionsTest.class.getName());

	@Inject
	private ResourceActions _resourceActions;

}