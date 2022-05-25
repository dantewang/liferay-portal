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

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Michael C. Han
 * @author Shuyang Zhou
 * @author Marta Medio
 */
public class InetAddressUtil {

	public static InetAddress getInetAddressByName(String domain)
		throws UnknownHostException {

		Address address = _resolvedAddresses.computeIfAbsent(
			domain,
			key -> {
				try {
					DNSResolveTask dnsResolveTask = new DNSResolveTask(key);

					_threadPoolExecutor.execute(dnsResolveTask);

					return new Address(
						dnsResolveTask.get(
							_DNS_SECURITY_ADDRESS_TIMEOUT_SECONDS,
							TimeUnit.SECONDS),
						null);
				}
				catch (ExecutionException | InterruptedException |
					   TimeoutException exception) {

					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}

					return new Address(
						null,
						new UnknownHostException(
							"Unable to resolve domain: " + key));
				}
			});

		return address.get();
	}

	public static String getLocalHostName() throws Exception {
		return LocalHostNameHolder._LOCAL_HOST_NAME;
	}

	public static InetAddress getLocalInetAddress() throws Exception {
		Enumeration<NetworkInterface> enumeration1 =
			NetworkInterface.getNetworkInterfaces();

		while (enumeration1.hasMoreElements()) {
			NetworkInterface networkInterface = enumeration1.nextElement();

			Enumeration<InetAddress> enumeration2 =
				networkInterface.getInetAddresses();

			while (enumeration2.hasMoreElements()) {
				InetAddress inetAddress = enumeration2.nextElement();

				if (!inetAddress.isLoopbackAddress() &&
					(inetAddress instanceof Inet4Address)) {

					return inetAddress;
				}
			}
		}

		throw new SystemException("No local internet address");
	}

	public static InetAddress getLoopbackInetAddress()
		throws UnknownHostException {

		return InetAddress.getByName("127.0.0.1");
	}

	public static boolean isLocalInetAddress(InetAddress inetAddress) {
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

	private static final Map<String, Address> _resolvedAddresses =
		new ConcurrentHashMap<>();

	private static final ThreadPoolExecutor _threadPoolExecutor =
		new ThreadPoolExecutor(
			1,
			GetterUtil.getInteger(
				PropsUtil.get(PropsKeys.DNS_SECURITY_THREAD_LIMIT)),
			60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
			new NamedThreadFactory(
				"InetAddressUtil DNS Resolve Thread - ", Thread.NORM_PRIORITY,
				InetAddressUtil.class.getClassLoader()),
			(runnable, executor) -> {
				if (!(runnable instanceof DNSResolveTask)) {
					return;
				}

				DNSResolveTask dnsResolveTask = (DNSResolveTask)runnable;

				_log.error(
					"Thread limit exceeded to resolve domain: " +
						dnsResolveTask.getDomain());

				dnsResolveTask.reject();
			});

	private static class Address {

		public Address(
			InetAddress inetAddress,
			UnknownHostException unknownHostException) {

			_inetAddress = inetAddress;
			_unknownHostException = unknownHostException;
		}

		public InetAddress get() throws UnknownHostException {
			if (_inetAddress == null) {
				throw _unknownHostException;
			}

			return _inetAddress;
		}

		private final InetAddress _inetAddress;
		private final UnknownHostException _unknownHostException;

	}

	private static class DNSResolveTask extends FutureTask<InetAddress> {

		public DNSResolveTask(String domain) {
			super(() -> InetAddress.getByName(domain));

			_domain = domain;
		}

		public String getDomain() {
			return _domain;
		}

		public void reject() {
			set(null);
		}

		private final String _domain;

	}

	private static class LocalHostNameHolder {

		private static final String _LOCAL_HOST_NAME;

		static {
			try {
				InetAddress inetAddress = getLocalInetAddress();

				_LOCAL_HOST_NAME = inetAddress.getHostName();
			}
			catch (Exception exception) {
				throw new ExceptionInInitializerError(exception);
			}
		}

	}

}