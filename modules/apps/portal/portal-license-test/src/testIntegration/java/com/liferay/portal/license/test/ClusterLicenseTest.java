/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.rule.TomcatClusterTestRule;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.cluster.tomcat.TomcatCluster;
import com.liferay.portal.test.cluster.tomcat.TomcatNode;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.LicenseUtil;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dante Wang
 * @author Jiefeng Wu
 */
@RunWith(Arquillian.class)
public class ClusterLicenseTest extends BaseLicenseTestCase {

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
	public static void setUpClass() {
		_originalSystemErrPrintStream = System.err;
		_originalSystemOutPrintStream = System.out;

		System.setErr(
			new TestPrintStream(System.err, _testConsoleMessageListener));
		System.setOut(
			new TestPrintStream(System.out, _testConsoleMessageListener));
	}

	@AfterClass
	public static void tearDownClass() {
		System.setErr(_originalSystemErrPrintStream);
		System.setOut(_originalSystemOutPrintStream);
	}

	@After
	public void tearDown() throws Exception {
		resetLicenseData();
		resetLifecycleAction();
	}

	@Test
	public void test() throws Exception {
		TomcatNode tomcatNode1 = _startTomcatNode();

		int port1 = tomcatNode1.getConnectorPort();

		tomcatNode1.syncExecute(() -> _test(port1));

		TomcatNode tomcatNode2 = _startTomcatNode();

		int port2 = tomcatNode2.getConnectorPort();

		tomcatNode2.syncExecute(() -> _test(port2));

		TomcatNode tomcatNode3 = _startTomcatNode();

		int port3 = tomcatNode3.getConnectorPort();

		tomcatNode3.syncExecute(() -> _test(port3));

		TomcatNode tomcatNode4 = _startTomcatNode();
		TomcatNode tomcatNode5 = _startTomcatNode();
		TomcatNode tomcatNode6 = _startTomcatNode();

		_testConsoleMessageListener.assertMessage(
			tomcatNode1.getNodeId(), _CONSOLE_KEY_LICENSED_NODE);
		_testConsoleMessageListener.assertMessage(
			tomcatNode1.getNodeId(), _CONSOLE_KEY_NODE_EXCEEDED);

		_testConsoleMessageListener.assertMessage(
			tomcatNode2.getNodeId(), _CONSOLE_KEY_LICENSED_NODE);
		_testConsoleMessageListener.assertMessage(
			tomcatNode2.getNodeId(), _CONSOLE_KEY_NODE_EXCEEDED);

		_testConsoleMessageListener.assertMessage(
			tomcatNode3.getNodeId(), _CONSOLE_KEY_LICENSED_NODE);
		_testConsoleMessageListener.assertMessage(
			tomcatNode3.getNodeId(), _CONSOLE_KEY_NODE_EXCEEDED);

		int port4 = tomcatNode4.getConnectorPort();

		tomcatNode4.syncExecute(() -> _test(port4));

		_testConsoleMessageListener.assertMessage(
			tomcatNode4.getNodeId(), _CONSOLE_KEY_TEMPORARY_NODE);
		_testConsoleMessageListener.assertMessage(
			tomcatNode4.getNodeId(), _CONSOLE_KEY_NODE_EXCEEDED);

		int port5 = tomcatNode5.getConnectorPort();

		tomcatNode4.syncExecute(() -> _test(port5));

		_testConsoleMessageListener.assertMessage(
			tomcatNode5.getNodeId(), _CONSOLE_KEY_TEMPORARY_NODE);
		_testConsoleMessageListener.assertMessage(
			tomcatNode5.getNodeId(), _CONSOLE_KEY_NODE_EXCEEDED);

		int port6 = tomcatNode6.getConnectorPort();

		tomcatNode4.syncExecute(() -> _test(port6));

		_testConsoleMessageListener.assertMessage(
			tomcatNode6.getNodeId(), _CONSOLE_KEY_TEMPORARY_NODE);
		_testConsoleMessageListener.assertMessage(
			tomcatNode6.getNodeId(), _CONSOLE_KEY_NODE_EXCEEDED);

		TomcatNode tomcatNode7 = _startTomcatNode();

		int port7 = tomcatNode7.getConnectorPort();

		tomcatNode7.syncExecute(
			() -> {
				Map<String, String> licenseProperties =
					LicenseManagerUtil.getLicenseProperties("Portal");

				Assert.assertTrue(
					licenseProperties.toString(), licenseProperties.isEmpty());

				assertLicenseNotRegistered(port7);

				deployFreeTierLicense(Time.HOUR);

				String response = hitHomePage("localhost", port7);

				Assert.assertTrue(response.contains(_EXCEEDED_LICENSE_KEY));

				return null;
			});

		_testConsoleMessageListener.assertMessage(
			tomcatNode7.getNodeId(), _CONSOLE_KEY_BEYOND_TEMPORARY_NODE);
		_testConsoleMessageListener.assertMessage(
			tomcatNode7.getNodeId(), _CONSOLE_KEY_NODE_EXCEEDED);

		// TODO: wait for grace period expiration and assert nodes shutdown

		// TODO: check "Finished shutting down overloaded nodes"

	}

	private TomcatNode _startTomcatNode() throws Exception {
		TomcatCluster.Builder builder = tomcatClusterTestRule.buildTomcatNode();

		TomcatNode tomcatNode = builder.build();

		tomcatNode.start(true);

		String path = tomcatNode.getLiferayHome(
		).concat(
			"/data/license"
		);

		tomcatNode.execute(
			() -> {
				disableValidate();
				setVersion("2026.Q1.0");

				ReflectionTestUtil.setFieldValue(
					LicenseUtil.class, "LICENSE_REPOSITORY_DIR", path);

				return null;
			});

		return tomcatNode;
	}

	private Serializable _test(int port) throws Exception {
		Map<String, String> licenseProperties =
			LicenseManagerUtil.getLicenseProperties("Portal");

		Assert.assertTrue(
			licenseProperties.toString(), licenseProperties.isEmpty());

		assertLicenseNotRegistered(port);

		deployFreeTierLicense(Time.HOUR);

		assertLicensePropertiesExisted(getPortalProductId());

		assertLicenseRegistered(port);

		return null;
	}

	private static final String _CONSOLE_KEY_BEYOND_TEMPORARY_NODE =
		"This current node is beyond the temporarily permitted node count " +
			"and is deactivated and will automatically shut down after the " +
				"grace period expires";

	private static final String _CONSOLE_KEY_LICENSED_NODE =
		"This current node is within the licensed node count and will not be " +
			"automatically deactivated nor shut down after the grace period " +
				"expires";

	private static final String _CONSOLE_KEY_NODE_EXCEEDED =
		"The maximum number of 3 nodes licensed for this cluster has been " +
			"exceeded";

	private static final String _CONSOLE_KEY_TEMPORARY_NODE =
		"This current node is within the temporarily permitted node count " +
			"and will be automatically deactivated and shut down after the " +
				"grace period expires";

	private static final String _EXCEEDED_LICENSE_KEY =
		"You have exceeded the developer mode connection limit";

	private static PrintStream _originalSystemErrPrintStream;
	private static PrintStream _originalSystemOutPrintStream;
	private static final TestConsoleMessageListener
		_testConsoleMessageListener = new TestConsoleMessageListener();

	private static class TestConsoleMessageListener {

		public void addMessage(String messageLine) {
			if (!messageLine.startsWith(_PREFIX)) {
				return;
			}

			int index = messageLine.indexOf(CharPool.CLOSE_BRACKET);

			if (index == -1) {
				return;
			}

			String nodeName = messageLine.substring(0, index + 1);

			List<String> lines = _messages.computeIfAbsent(
				nodeName, key -> new ArrayList<>());

			lines.add(messageLine);
		}

		public void assertMessage(int nodeId, String expectedMessage) {
			List<String> lines = _messages.get(
				_PREFIX + nodeId + StringPool.CLOSE_BRACKET);

			Assert.assertTrue(
				lines.removeIf(line -> line.contains(expectedMessage)));
		}

		private static final String _PREFIX = "[TomcatNode-";

		private final Map<String, List<String>> _messages = new HashMap<>();

	}

	private static class TestPrintStream extends PrintStream {

		public TestPrintStream(
			OutputStream outputStream,
			TestConsoleMessageListener testConsoleMessageListener) {

			super(outputStream);

			_testConsoleMessageListener = testConsoleMessageListener;
		}

		@Override
		public void print(String message) {
			super.print(message);

			for (String line : StringUtil.split(message, CharPool.NEW_LINE)) {
				_testConsoleMessageListener.addMessage(line);
			}
		}

		private final TestConsoleMessageListener _testConsoleMessageListener;

	}

}