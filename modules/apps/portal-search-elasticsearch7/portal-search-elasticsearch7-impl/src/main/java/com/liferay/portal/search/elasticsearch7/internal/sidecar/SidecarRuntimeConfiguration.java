/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.process.ProcessConfig;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;

/**
 * @author Dante Wang
 */
public class SidecarRuntimeConfiguration implements Externalizable {

	public static final String LIFERAY_SIDECAR_CONFIG =
		"liferay_sidecar_config";

	public static Path createSidecarTempDirPath() {
		Path sidecarTempDirPath = null;

		try {
			sidecarTempDirPath = Files.createTempDirectory("sidecar");
		}
		catch (IOException ioException) {
			throw new IllegalStateException(
				"Unable to create temp folder", ioException);
		}

		return sidecarTempDirPath;
	}

	public SidecarRuntimeConfiguration() {
	}

	public SidecarRuntimeConfiguration(
		String nodeName, ProcessConfig processConfig,
		long sidecarHeartbeatInterval, Path sidecarHomePath,
		SidecarServerArgs sidecarServerArgs, long sidecarShutdownTimeout,
		Path sidecarTempDirPath, Path sidecarWorkPath) {

		_nodeName = nodeName;
		_processConfig = processConfig;
		_sidecarHeartbeatInterval = sidecarHeartbeatInterval;
		_sidecarHomePath = sidecarHomePath;
		_sidecarServerArgs = sidecarServerArgs;
		_sidecarShutdownTimeout = sidecarShutdownTimeout;
		_sidecarTempDirPath = sidecarTempDirPath;
		_sidecarWorkPath = sidecarWorkPath;
	}

	public String getNodeName() {
		return _nodeName;
	}

	public ProcessConfig getProcessConfig() {
		return _processConfig;
	}

	public long getSidecarHeartbeatInterval() {
		return _sidecarHeartbeatInterval;
	}

	public Path getSidecarHomePath() {
		return _sidecarHomePath;
	}

	public SidecarServerArgs getSidecarServerArgs() {
		return _sidecarServerArgs;
	}

	public long getSidecarShutdownTimeout() {
		return _sidecarShutdownTimeout;
	}

	public Path getSidecarTempDirPath() {
		return _sidecarTempDirPath;
	}

	public Path getSidecarWorkPath() {
		return _sidecarWorkPath;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		_nodeName = objectInput.readUTF();

		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		builder.setArguments(
			(List<String>)objectInput.readObject()
		).setBootstrapClassPath(
			objectInput.readUTF()
		).setJavaExecutable(
			objectInput.readUTF()
		).setProcessLogConsumer(
			Sidecar::consumeProcessLog
		).setReactClassLoader(
			Sidecar.class.getClassLoader()
		).setRuntimeClassPath(
			objectInput.readUTF()
		);

		_sidecarHeartbeatInterval = objectInput.readLong();

		String sidecarHomePathString = objectInput.readUTF();

		_processConfig = builder.setEnvironment(
			HashMapBuilder.putAll(
				System.getenv()
			).put(
				"HOSTNAME", "localhost"
			).put(
				"LIBFFI_TMPDIR", sidecarHomePathString
			).build()
		).build();

		_sidecarHomePath = Path.of(sidecarHomePathString);

		_sidecarServerArgs = (SidecarServerArgs)objectInput.readObject();
		_sidecarShutdownTimeout = objectInput.readLong();
		_sidecarWorkPath = Path.of(objectInput.readUTF());

		// Temp dir path is different every startup

		_sidecarTempDirPath = createSidecarTempDirPath();

		List<String> arguments = _processConfig.getArguments();

		arguments.add("-Djava.io.tmpdir=" + _sidecarTempDirPath);
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		List<String> arguments = _processConfig.getArguments();

		arguments.removeIf(argument -> argument.startsWith("-Djava.io.tmpdir"));

		objectOutput.writeUTF(_nodeName);

		// ProcessConfig

		objectOutput.writeObject(_processConfig.getArguments());
		objectOutput.writeUTF(_processConfig.getBootstrapClassPath());
		objectOutput.writeUTF(_processConfig.getJavaExecutable());
		objectOutput.writeUTF(_processConfig.getRuntimeClassPath());

		objectOutput.writeLong(_sidecarHeartbeatInterval);
		objectOutput.writeUTF(_sidecarHomePath.toString());
		objectOutput.writeObject(_sidecarServerArgs);
		objectOutput.writeLong(_sidecarShutdownTimeout);
		objectOutput.writeUTF(_sidecarWorkPath.toString());

		arguments.add("-Djava.io.tmpdir=" + _sidecarTempDirPath);
	}

	private String _nodeName;
	private ProcessConfig _processConfig;
	private long _sidecarHeartbeatInterval;
	private Path _sidecarHomePath;
	private SidecarServerArgs _sidecarServerArgs;
	private long _sidecarShutdownTimeout;
	private Path _sidecarTempDirPath;
	private Path _sidecarWorkPath;

}