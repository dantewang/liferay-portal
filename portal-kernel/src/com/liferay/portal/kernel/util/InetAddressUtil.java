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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.concurrent.DefaultNoticeableFuture;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Michael C. Han
 * @author Shuyang Zhou
 * @author Marta Medio
 */
public class InetAddressUtil {

	public static InetAddress getInetAddressByName(String domain)
		throws UnknownHostException {

		try {
			if (_atomicInteger.getAndDecrement() <= 0) {
				_log.error(
					"Thread limit exceeded to resolve domain: " + domain);

				return null;
			}

			DefaultNoticeableFuture<InetAddress> defaultNoticeableFuture =
				new DefaultNoticeableFuture<>(
					() -> InetAddress.getByName(domain));

			Thread thread = new Thread(
				defaultNoticeableFuture, "Inet Address Util");

			thread.setDaemon(true);

			thread.start();

			return defaultNoticeableFuture.get(
				_DNS_SECURITY_ADDRESS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		catch (ExecutionException | InterruptedException | TimeoutException
					exception) {

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new UnknownHostException(
				"Unable to resolve domain: " + domain);
		}
		finally {
			_atomicInteger.incrementAndGet();
		}
	}

	public static InetAddress getLoopbackInetAddress()
		throws UnknownHostException {

		return InetAddress.getByName("127.0.0.1");
	}

	public static boolean isHostAllowed(String host, Set<String> allowedHosts) {
		if (allowedHosts.isEmpty()) {
			return true;
		}

		if (allowedHosts.contains(host) ||
			(allowedHosts.contains("SERVER_IP") &&
			 _localHosts.containsKey(host))) {

			return true;
		}

		InetAddress inetAddress = _localHosts.get(host);

		if (inetAddress == null) {
			try {
				inetAddress = getInetAddressByName(host);

				if (inetAddress == null) {
					return false;
				}
			}
			catch (UnknownHostException unknownHostException) {
				return false;
			}
		}

		if (allowedHosts.contains(inetAddress.getHostAddress())) {
			return true;
		}

		for (String allowedHost : allowedHosts) {
			try {
				if (_isAllowedIPAddress(allowedHost, inetAddress)) {
					return true;
				}
			}
			catch (UnknownHostException unknownHostException) {
				return false;
			}
		}

		return false;
	}

	public static boolean isLocalHost(String host) throws UnknownHostException {
		if (_localHosts.containsKey(host)) {
			return true;
		}

		InetAddress inetAddress = getInetAddressByName(host);

		if (inetAddress.isAnyLocalAddress() ||
			inetAddress.isLinkLocalAddress() ||
			inetAddress.isLoopbackAddress() ||
			inetAddress.isSiteLocalAddress()) {

			return true;
		}

		return false;
	}

	private static boolean _isAllowedIPAddress(
			String allowedIP, InetAddress inetAddress)
		throws UnknownHostException {

		if (Validator.isNull(allowedIP)) {
			return false;
		}

		String[] ipAddressAndNetmask = StringUtil.split(
			allowedIP, StringPool.SLASH);

		byte[] netmask = null;

		if (Validator.isIPv4Address(ipAddressAndNetmask[0])) {
			netmask = new byte[4];
		}
		else if (Validator.isIPv6Address(ipAddressAndNetmask[0])) {
			netmask = new byte[16];
		}
		else {
			return false;
		}

		InetAddress allowedIPInetAddress = InetAddress.getByName(
			ipAddressAndNetmask[0]);

		if (ipAddressAndNetmask.length > 1) {
			String netmaskString = GetterUtil.getString(ipAddressAndNetmask[1]);

			if (Validator.isNumber(netmaskString)) {
				int cidr = GetterUtil.getInteger(netmaskString);

				int netmaskBytes = cidr / 8;

				for (int i = 0; i < netmaskBytes; i++) {
					netmask[i] = (byte)_BYTE[8];
				}

				if (netmaskBytes < netmask.length) {
					netmask[netmaskBytes] = (byte)_BYTE[cidr % 8];
				}
			}
			else {
				InetAddress netmaskInetAddress = InetAddress.getByName(
					netmaskString);

				netmask = netmaskInetAddress.getAddress();
			}
		}

		byte[] allowedIpAddressBytes = allowedIPInetAddress.getAddress();

		byte[] inetAddressBytes = inetAddress.getAddress();

		if (!(allowedIpAddressBytes.length == inetAddressBytes.length)) {
			return false;
		}

		for (int i = 0; i < netmask.length; i++) {
			if ((inetAddressBytes[i] & netmask[i]) !=
					(allowedIpAddressBytes[i] & netmask[i])) {

				return false;
			}
		}

		return true;
	}

	private static final int[] _BYTE = {
		0b00000000, 0b10000000, 0b11000000, 0b11100000, 0b11110000, 0b11111000,
		0b11111100, 0b11111110, 0b11111111
	};

	private static final int _DNS_SECURITY_ADDRESS_TIMEOUT_SECONDS =
		GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.DNS_SECURITY_ADDRESS_TIMEOUT_SECONDS));

	private static final Log _log = LogFactoryUtil.getLog(
		InetAddressUtil.class);

	private static final AtomicInteger _atomicInteger = new AtomicInteger(
		GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.DNS_SECURITY_THREAD_LIMIT)));
	private static final Map<String, InetAddress> _localHosts = new HashMap<>();

	static {
		try {
			List<NetworkInterface> networkInterfaces = Collections.list(
				NetworkInterface.getNetworkInterfaces());

			for (NetworkInterface networkInterface : networkInterfaces) {
				List<InetAddress> inetAddresses = Collections.list(
					networkInterface.getInetAddresses());

				for (InetAddress inetAddress : inetAddresses) {
					if (inetAddress instanceof Inet4Address) {
						_localHosts.put(
							inetAddress.getHostAddress(), inetAddress);
						_localHosts.put(inetAddress.getHostName(), inetAddress);
					}
				}
			}
		}
		catch (Exception exception) {
			_log.error("Unable to initialize local hosts", exception);
		}
	}

}