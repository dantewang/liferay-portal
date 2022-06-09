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

	public static boolean isHostAllowed(String host, Set<String> allowedHosts)
		throws UnknownHostException {

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
			inetAddress = getInetAddressByName(host);
		}

		return allowedHosts.contains(inetAddress.getHostAddress());
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
			_log.error("Unable to initalize local hosts", exception);
		}
	}

}