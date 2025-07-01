/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.local.LocalProcessLauncher;

import com.sun.tools.attach.VirtualMachine;

import java.io.Serializable;

import java.net.URL;

import java.nio.file.Path;

import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * @author Tina Tian
 */
public class SidecarMainProcessCallable
	implements ProcessCallable<Serializable> {

	public SidecarMainProcessCallable(long heartbeatInterval) {
		_heartbeatInterval = heartbeatInterval;
	}

	@Override
	public Serializable call() throws ProcessException {
		LocalProcessLauncher.ProcessContext.attach(
			"SidecarMainProcessCallable", _heartbeatInterval,
			(shutdownCode, shutdownThrowable) -> {
				ElasticsearchServerUtil.shutdown();

				return true;
			});

		try {
			_loadAgent();
		}
		catch (Exception exception) {
			throw new ProcessException(
				"Unable to attach Sidecar agent", exception);
		}

		ElasticsearchServerUtil.waitForShutdown();

		return null;
	}

	private void _loadAgent() throws Exception {
		ProtectionDomain protectionDomain =
			SidecarMainProcessCallable.class.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		Path path = Path.of(url.toURI());

		ProcessHandle processHandle = ProcessHandle.current();

		VirtualMachine virtualMachine = null;

		try {
			virtualMachine = VirtualMachine.attach(
				String.valueOf(processHandle.pid()));

			virtualMachine.loadAgent(path.toString());
		}
		finally {
			if (virtualMachine != null) {
				virtualMachine.detach();
			}
		}
	}

	private static final long serialVersionUID = 1L;

	private final long _heartbeatInterval;

}