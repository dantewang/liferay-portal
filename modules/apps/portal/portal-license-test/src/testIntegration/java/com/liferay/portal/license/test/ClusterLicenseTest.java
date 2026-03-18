package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.rule.TomcatClusterTestRule;
import com.liferay.portal.kernel.util.Time;
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
public class ClusterLicenseTest  extends BaseLicenseTestCase{

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
		Assume.assumeTrue(isReleaseBundle());
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		TomcatCluster.Builder builder1 =
			tomcatClusterTestRule.buildTomcatNode();

		// set this to true then we can debug with _tomcatNode1
//		builder1.setJpdaEnabled(true);

		_tomcatNode1 = builder1.build();

		_tomcatNode1.start(true);

		TomcatCluster.Builder builder2 =
			tomcatClusterTestRule.buildTomcatNode();

		_tomcatNode2 = builder2.build();

		_tomcatNode2.start(true);
	}

	@After
	public void tearDown() throws Exception {
		resetLicenseData();
		resetLifecycleAction();
	}

	@Test
	public void test() throws Exception {
		// need to make this path to make sure the data/license get deployed to the tomcatNode
		String path = _tomcatNode1.getLiferayHome(
		).concat(
			"/data/license"
		);
		int port = _tomcatNode1.getConnectorPort();

		_tomcatNode1.syncExecute(
			() -> {

				Map<String, String> licenseProperties =
					LicenseManagerUtil.getLicenseProperties("Portal");

				Assert.assertTrue(
					licenseProperties.toString(), licenseProperties.isEmpty());

				assertLicenseNotRegistered(port);

				ReflectionTestUtil.setFieldValue(
					LicenseUtil.class, "LICENSE_REPOSITORY_DIR", path);

				deployFreeTierLicense(Time.HOUR);

				assertLicensePropertiesExisted(getPortalProductId());

				assertLicenseRegistered(port);

				return null;
			});
	}


	private static transient TomcatNode _tomcatNode1;
	private static transient TomcatNode _tomcatNode2;
	private static transient TomcatNode _tomcatNode3;
	private static transient TomcatNode _tomcatNode4;
	private static transient TomcatNode _tomcatNode5;

}