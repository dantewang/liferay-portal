/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.process.ProcessConfig;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.util.PortalClassPathUtil;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import java.util.Map;

/**
 * @author Dante Wang
 */
public class SidecarRuntimeConfiguration implements Externalizable {

	public SidecarRuntimeConfiguration() {
	}

	public SidecarRuntimeConfiguration(
		long heartbeatInterval, Path homePath, String nodeName,
		ProcessConfig processConfig, long shutdownTimeout,
		SidecarServerArgs sidecarServerArgs, Path tempDirPath, String version) {

		_heartbeatInterval = heartbeatInterval;
		_homePath = homePath;
		_nodeName = nodeName;
		_processConfig = processConfig;
		_shutdownTimeout = shutdownTimeout;
		_sidecarServerArgs = sidecarServerArgs;
		_tempDirPath = tempDirPath;
		_version = version;
	}

	public long getHeartbeatInterval() {
		return _heartbeatInterval;
	}

	public Path getHomePath() {
		return _homePath;
	}

	public String getNodeName() {
		return _nodeName;
	}

	public ProcessConfig getProcessConfig() {
		return _processConfig;
	}

	public long getShutdownTimeout() {
		return _shutdownTimeout;
	}

	public SidecarServerArgs getSidecarServerArgs() {
		return _sidecarServerArgs;
	}

	public Path getTempDirPath() {
		return _tempDirPath;
	}

	public String getVersion() {
		return _version;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		try {
			_tempDirPath = Files.createTempDirectory("sidecar");
		}
		catch (IOException ioException) {
			throw new IllegalStateException(
				"Unable to create temp folder", ioException);
		}

		_heartbeatInterval = objectInput.readLong();

		String homePathString = objectInput.readUTF();

		_homePath = Path.of(homePathString);

		_nodeName = objectInput.readUTF();

		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		List<String> arguments = (List<String>)objectInput.readObject();

		arguments.add("-Djava.io.tmpdir=" + _tempDirPath);

		_processConfig = builder.setArguments(
			arguments
		).setBootstrapClassPath(
			objectInput.readUTF()
		).setEnvironment(
			HashMapBuilder.putAll(
				System.getenv()
			).put(
				"HOSTNAME", "localhost"
			).put(
				"LIBFFI_TMPDIR", homePathString
			).build()
		).setJavaExecutable(
			objectInput.readUTF()
		).setProcessLogConsumer(
			PortalClassPathUtil.createProcessLogConsumer(
				LogFactoryUtil.getLog(Sidecar.class))
		).setReactClassLoader(
			Sidecar.class.getClassLoader()
		).setRuntimeClassPath(
			objectInput.readUTF()
		).build();

		_shutdownTimeout = objectInput.readLong();

		_sidecarServerArgs = new SidecarServerArgs(
			String.valueOf(_tempDirPath.resolve("config")),
			objectInput.readBoolean(), objectInput.readUTF(),
			objectInput.readBoolean(),
			(Map<String, Serializable>)objectInput.readObject());

		_version = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(_heartbeatInterval);
		objectOutput.writeUTF(_homePath.toString());
		objectOutput.writeUTF(_nodeName);

		// Exclude ENV and temp dir in ProcessConfig

		List<String> arguments = _processConfig.getArguments();

		arguments.removeIf(argument -> argument.startsWith("-Djava.io.tmpdir"));

		objectOutput.writeObject(arguments);

		objectOutput.writeUTF(_processConfig.getBootstrapClassPath());
		objectOutput.writeUTF(_processConfig.getJavaExecutable());
		objectOutput.writeUTF(_processConfig.getRuntimeClassPath());

		objectOutput.writeLong(_shutdownTimeout);

		// Exclude configDir in SidecarServerArgs as it's tied to the temp dir

		objectOutput.writeBoolean(_sidecarServerArgs.isDaemonize());
		objectOutput.writeUTF(_sidecarServerArgs.getLogsDir());
		objectOutput.writeBoolean(_sidecarServerArgs.isQuiet());
		objectOutput.writeObject(_sidecarServerArgs.getSettings());

		objectOutput.writeUTF(_version);
	}

	private long _heartbeatInterval;
	private Path _homePath;
	private String _nodeName;
	private ProcessConfig _processConfig;
	private long _shutdownTimeout;
	private SidecarServerArgs _sidecarServerArgs;
	private Path _tempDirPath;
	private String _version;

}