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

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.test.util.PropsTestUtil;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Mariano Álvaro Sáiz
 * @author Dante Wang
 */
public class InetAddressUtilTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		PropsTestUtil.setProps(
			HashMapBuilder.<String, Object>put(
				PropsKeys.DNS_SECURITY_ADDRESS_TIMEOUT_SECONDS,
				String.valueOf(2)
			).put(
				PropsKeys.DNS_SECURITY_THREAD_LIMIT, String.valueOf(10)
			).build());
	}

	@Test
	public void testIPv4AddressDoesNotMatchIPv6Address() {
		Assert.assertFalse(
			InetAddressUtil.isHostAllowed(
				_ADDRESS_IP_V6, Collections.singleton(_ADDRESS_IP_V4)));
	}

	@Test
	public void testIPv4AddressMatchesIPv4Address() {
		Assert.assertTrue(
			InetAddressUtil.isHostAllowed(
				_ADDRESS_IP_V4, Collections.singleton(_ADDRESS_IP_V4)));
	}

	@Test
	public void testIPv4CIDRNetmaskValidatesCorrectly() {
		Assert.assertTrue(
			InetAddressUtil.isHostAllowed(
				_ADDRESS_IP_V4, Collections.singleton("192.168.1.0/24")));

		Assert.assertFalse(
			InetAddressUtil.isHostAllowed(
				_ADDRESS_IP_V4, Collections.singleton("192.168.1.128/25")));

		Assert.assertTrue(
			InetAddressUtil.isHostAllowed(
				"192.168.1.159", Collections.singleton("192.168.1.128/25")));
	}

	@Test
	public void testIPv4DotNotationNetmaskValidatesCorrectly() {
		Assert.assertTrue(
			InetAddressUtil.isHostAllowed(
				_ADDRESS_IP_V4,
				Collections.singleton("192.168.1.0/255.255.255.0")));

		Assert.assertFalse(
			InetAddressUtil.isHostAllowed(
				_ADDRESS_IP_V4,
				Collections.singleton("192.168.1.128/255.255.255.128")));

		Assert.assertTrue(
			InetAddressUtil.isHostAllowed(
				"192.168.1.159",
				Collections.singleton("192.168.1.128/255.255.255.128")));
	}

	@Test
	public void testIPv4InvalidConfigurationInvalidatesEverything() {
		Assert.assertFalse(
			InetAddressUtil.isHostAllowed(
				_ADDRESS_IP_V4, Collections.singleton("192.168.0/24")));

		Assert.assertFalse(
			InetAddressUtil.isHostAllowed(
				_ADDRESS_IP_V4, Collections.singleton("192.168.1.0/33")));
	}

	@Test
	public void testIPv6AddressDoesNotMatchIPv4Address() {
		Assert.assertFalse(
			InetAddressUtil.isHostAllowed(
				_ADDRESS_IP_V4, Collections.singleton(_ADDRESS_IP_V6)));
	}

	@Test
	public void testIPv6AddressMatchesIPv6Address() {
		Assert.assertTrue(
			InetAddressUtil.isHostAllowed(
				_ADDRESS_IP_V6, Collections.singleton(_ADDRESS_IP_V6)));
	}

	@Test
	public void testIPv6CIDRNetmaskValidatesCorrectly() {
		Set<String> allowedIPs = Collections.singleton("2001:AB9::/48");

		Assert.assertTrue(
			InetAddressUtil.isHostAllowed("2001:AB9:0:0:0:0:0:0", allowedIPs));

		Assert.assertTrue(
			InetAddressUtil.isHostAllowed("2001:AB9:0:0:0:0:0:1", allowedIPs));

		Assert.assertTrue(
			InetAddressUtil.isHostAllowed(
				"2001:AB9:0:FFFF:FFFF:FFFF:FFFF:FFFF", allowedIPs));

		Assert.assertFalse(
			InetAddressUtil.isHostAllowed("2001:AB9:1:0:0:0:0:0", allowedIPs));
	}

	@Test
	public void testIPv6InvalidConfigurationInvalidatesEverything() {
		Assert.assertFalse(
			InetAddressUtil.isHostAllowed(
				_ADDRESS_IP_V6, Collections.singleton("2001:AB9::/48")));

		Assert.assertFalse(
			InetAddressUtil.isHostAllowed(
				_ADDRESS_IP_V6, Collections.singleton("\"2001:DB8::/130\"")));
	}

	@Test
	public void testZeroNetmaskValidatesEveryIP() {
		Assert.assertTrue(
			InetAddressUtil.isHostAllowed(
				"1.2.3.4", Collections.singleton("0.0.0.0/0")));

		Assert.assertTrue(
			InetAddressUtil.isHostAllowed(
				"192.168.0.159", Collections.singleton("0.0.0.0/0")));

		Assert.assertTrue(
			InetAddressUtil.isHostAllowed(
				"1.2.3.4", Collections.singleton("192.168.0.159/0")));

		Assert.assertTrue(
			InetAddressUtil.isHostAllowed(
				"192.168.0.159", Collections.singleton("192.168.0.159/0")));
	}

	private static final String _ADDRESS_IP_V4 = "192.168.1.104";

	private static final String _ADDRESS_IP_V6 =
		"2001:AC8:1234:0000:0000:C1C0:ABCD:0876";

}