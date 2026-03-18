package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.rule.TomcatClusterTestRule;
import com.liferay.portal.license.test.util.LicenseTestUtil;
import com.liferay.portal.test.cluster.tomcat.TomcatCluster;
import com.liferay.portal.test.cluster.tomcat.TomcatNode;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.LicenseUtil;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ClusterLicenseTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@ClassRule
	public static final TomcatClusterTestRule tomcatClusterTestRule =
		new TomcatClusterTestRule();

	public static void assume() {
		Assume.assumeTrue(LicenseTestUtil.isReleaseBundle());
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		TomcatCluster.Builder builder1 =
			tomcatClusterTestRule.buildTomcatNode();

		builder1.setJpdaEnabled(true);

		_tomcatNode1 = builder1.build();

		_tomcatNode1.start(true);

		TomcatCluster.Builder builder2 =
			tomcatClusterTestRule.buildTomcatNode();

		_tomcatNode2 = builder2.build();

		_tomcatNode2.start(true);
	}

	@After
	public void tearDown() throws Exception {
		LicenseTestUtil.removeAllLicenseBinaryFiles();
		LicenseTestUtil.resetLifecycleAction();
	}

	@Test
	public void test() throws Exception {

		// Check This instance is not registered

		String path = _tomcatNode1.getLiferayHome(
		).concat(
			"/data/license"
		);
		int port = _tomcatNode1.getConnectorPort();

		_tomcatNode1.syncExecute(
			() -> {

				// Check This instance is not registered

				Map<String, String> licenseProperties =
					LicenseTestUtil.getPortalLicenseProperties();

				Assert.assertTrue(
					licenseProperties.toString(), licenseProperties.isEmpty());

				String response = LicenseTestUtil.hitHomePage(
					"localhost", 8080);

				Assert.assertTrue(
					response.contains("This instance is not registered."));

				ReflectionTestUtil.setFieldValue(
					LicenseUtil.class, "LICENSE_REPOSITORY_DIR", path);

				// Deploy free type license to the two new nodes

				LicenseTestUtil.deployFreeTierLicenseContent(
					"Monday, February 17, 2026 02:00:00 AM GMT",
					"Monday, March 1, 2027 12:00:00 AM GMT");

				//				Thread.sleep(10000);

				licenseProperties =
					LicenseTestUtil.getPortalLicenseProperties();

				Assert.assertFalse(
					licenseProperties.toString(), licenseProperties.isEmpty());

				response = LicenseTestUtil.hitHomePage("localhost", port);

				Assert.assertTrue(response.contains("Welcome to Liferay"));

				return null;
			});
	}

	private static final String _DXP_ONLY_MODULE_SYMBOLIC_NAME =
		"com.liferay.saml.api";

	private static final String _ENTERPRISE_APP_SYMBOLIC_NAME =
		"com.liferay.portal.license.enterprise.app";

	private static transient TomcatNode _tomcatNode1;
	private static transient TomcatNode _tomcatNode2;
	private static transient TomcatNode _tomcatNode3;
	private static transient TomcatNode _tomcatNode4;
	private static transient TomcatNode _tomcatNode5;

}