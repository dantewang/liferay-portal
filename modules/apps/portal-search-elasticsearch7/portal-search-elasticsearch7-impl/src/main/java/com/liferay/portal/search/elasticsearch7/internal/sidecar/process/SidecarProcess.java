/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar.process;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.process.local.LocalProcessExecutor;
import com.liferay.petra.string.StringBundler;

import java.io.IOException;
import java.io.Serializable;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * @author Tina Tian
 */
public class SidecarProcess {

	public static void start(
		ProcessConfig processConfig, long heartbeatInterval, byte[] bytes) {

		SidecarProcess sidecarProcess =
			_sidecarProcessDCLSingleton.getSingleton(
				SidecarProcess::new);

		sidecarProcess._start(processConfig, heartbeatInterval, bytes);
	}

	public static void stop() {
		_sidecarProcessDCLSingleton.destroy(SidecarProcess::_stop);
	}

	private ProcessChannel<Serializable> _executeSidecarMainProcess(
		ProcessConfig processConfig, long heartbeatInterval) {

		try {
			return _processExecutor.execute(
				processConfig,
				new SidecarMainProcessCallable(heartbeatInterval));
		}
		catch (ProcessException processException) {
			throw new RuntimeException(
				"Unable to start sidecar Elasticsearch process",
				processException);
		}
	}

	private void _start(
		ProcessConfig processConfig, long heartbeatInterval, byte[] bytes) {

		ProcessChannel<Serializable> processChannel =
			_executeSidecarMainProcess(processConfig, heartbeatInterval);

		NoticeableFuture<String> noticeableFuture = processChannel.write(
			new StartSidecarProcessCallable(bytes));

		try {
			_waitForPublishedAddress(noticeableFuture);

			_processChannel = processChannel;
		}
		catch (IOException ioException) {
			if (Objects.equals(ioException.getMessage(), "Stream closed")) {
				throw new RuntimeException(
					StringBundler.concat(
						"Sidecar JVM did not launch successfully. ",
						SidecarMainProcessCallable.class.getSimpleName(),
						" may have crashed, or its classpath may be missing ",
						"required libraries"),
					ioException);
			}

			processChannel.write(new StopSidecarProcessCallable());

			throw new RuntimeException(ioException);
		}
		catch (Exception exception) {
			processChannel.write(new StopSidecarProcessCallable());

			if (exception instanceof RuntimeException) {
				throw (RuntimeException)exception;
			}

			throw new RuntimeException(exception);
		}
	}

	private void _stop() {
		if (_processChannel != null) {
			_processChannel.write(new StopSidecarProcessCallable());

			NoticeableFuture<?> noticeableFuture =
				_processChannel.getProcessNoticeableFuture();

			try {
				_processChannel = null;

				noticeableFuture.get();
			}
			catch (InterruptedException | ExecutionException exception) {
				throw new RuntimeException(exception);
			}
		}
	}

	private void _waitForPublishedAddress(
			NoticeableFuture<String> noticeableFuture)
		throws Exception {

		try {
			noticeableFuture.get();
		}
		catch (ExecutionException executionException) {
			throw new Exception(executionException.getCause());
		}
		catch (InterruptedException interruptedException) {
			throw new RuntimeException(interruptedException);
		}
	}

	private static final DCLSingleton<SidecarProcess>
		_sidecarProcessDCLSingleton = new DCLSingleton<>();

	private volatile ProcessChannel<Serializable> _processChannel;
	private final ProcessExecutor _processExecutor = new LocalProcessExecutor();

}