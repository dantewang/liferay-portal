/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.internal.configuration.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class PortalInstancesConfigurationFactoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@After
	public void tearDown() throws Exception {
		if (_configuration != null) {
			ConfigurationTestUtil.deleteConfiguration(_configuration);
		}
	}

	@Test
	public void testCreateCompany() throws Exception {
		String webId = RandomTestUtil.randomString();

		_configuration = _configurationAdmin.getFactoryConfiguration(
			"com.liferay.portal.instances.internal.configuration." +
				"PortalInstancesConfiguration",
			webId, StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"mx", webId.concat(".foo.bar")
			).put(
				"virtualHostname", webId.concat(".foo.bar")
			).build());

		_company = _companyLocalService.getCompanyByWebId(webId);

		Assert.assertNotNull(_company);

		Assert.assertEquals(
			1, _userLocalService.getCompanyUsersCount(_company.getCompanyId()));
	}

	@Test
	public void testCreateCompanyWithoutDefaultAdmin() throws Exception {
		String webId = RandomTestUtil.randomString();

		_configuration = _configurationAdmin.getFactoryConfiguration(
			"com.liferay.portal.instances.internal.configuration." +
				"PortalInstancesConfiguration",
			webId, StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"createDefaultAdmin", "false"
			).put(
				"mx", webId.concat(".foo.bar")
			).put(
				"virtualHostname", webId.concat(".foo.bar")
			).build());

		_company = _companyLocalService.getCompanyByWebId(webId);

		Assert.assertNotNull(_company);

		Assert.assertEquals(
			0, _userLocalService.getCompanyUsersCount(_company.getCompanyId()));
	}

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Configuration _configuration;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private UserLocalService _userLocalService;

}