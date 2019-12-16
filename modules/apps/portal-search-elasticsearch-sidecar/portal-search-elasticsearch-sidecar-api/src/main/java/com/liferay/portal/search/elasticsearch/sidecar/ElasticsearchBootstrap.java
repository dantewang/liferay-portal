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

package com.liferay.portal.search.elasticsearch.sidecar;

import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.process.PathHolder;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.process.ProcessLog;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PortalClassPathUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.net.URI;
import java.net.URL;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dante Wang
 */
public class ElasticsearchBootstrap {

	public ElasticsearchBootstrap(
			ProcessExecutor processExecutor, String hostname)
		throws Exception {

		_processExecutor = processExecutor;

		_processConfig = _createProcessConfig(hostname);
	}

	public void start() throws Exception {
		_processChannel = _processExecutor.execute(
			_processConfig,
			new ElasticsearchBootstrapProcessCallable(new String[0]));
	}

	public void stop() {
		if (_processChannel != null) {
			NoticeableFuture<String> noticeableFuture =
				_processChannel.getProcessNoticeableFuture();

			noticeableFuture.cancel(true);
		}
	}

	private String _createClasspath(File esClasspath) throws Exception {
		StringBundler sb = new StringBundler();

		// This module

		ProtectionDomain protectionDomain =
			ElasticsearchBootstrap.class.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		URI uri = url.toURI();

		File file = new File(uri);

		sb.append(file.getAbsolutePath());

		sb.append(File.pathSeparator);

		// $ES_HOME/libs/*

		File[] libFiles = esClasspath.listFiles();

		for (File libFile : libFiles) {
			sb.append(libFile.getAbsolutePath());
			sb.append(File.pathSeparator);
		}

		// Only include petra jars in the classpath.
		// Elasticsearch has a Jar Hell check, we need to keep classpath minimum
		// to avoid classes with the same name, even though such classes may not
		// conflict with Elasticsearch's.

		ProcessConfig portalProcessConfig =
			PortalClassPathUtil.getPortalProcessConfig();

		PathHolder[] pathHolders =
			portalProcessConfig.getBootstrapClassPathHolders();

		for (PathHolder pathHolder : pathHolders) {
			String path = pathHolder.toString();

			if (!path.contains("petra")) {
				continue;
			}

			sb.append(path);
			sb.append(File.pathSeparator);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private ProcessConfig _createProcessConfig(String hostname)
		throws Exception {

		// ES ENV: ES_CLASSPATH = $ES_HOME/lib/*

		File esClasspath = new File(_ES_HOME, "lib");

		// ES ENV: ES_PATH_CONF = $ES_HOME/config

		File esPathConf = new File(_ES_HOME, "config");

		// Java executable, use the same one as portal

		String javaExecutable = _getJavaExecutable(
			new File(System.getProperty("java.home")));

		List<String> arguments = new ArrayList<>();

		// JVM arguments from config/jvm.options

		_parseJVMArguments(
			arguments, javaExecutable, new File(esPathConf, "jvm.options"),
			esClasspath);

		// Arguments in bin/elasticsearch script

		arguments.add("-Des.path.home=".concat(_ES_HOME.getAbsolutePath()));
		arguments.add("-Des.path.conf=".concat(esPathConf.getAbsolutePath()));

		// TODO: Hard coded values from bin/elasticsearch

		arguments.add("-Des.distribution.flavor=default");
		arguments.add("-Des.distribution.type=tar");
		arguments.add("-Des.bundled_jdk=true");

		// Environments

		Map<String, String> environments = new HashMap<>();

		// ProcessExecutor cleans up default environment

		environments.putAll(System.getenv());

		// Elasticsearch requires HOSTNAME to be set

		environments.put("HOSTNAME", hostname);

		// Process executor classpath

		String classpath = _createClasspath(esClasspath);

		ProcessConfig.Builder processConfigBuilder =
			new ProcessConfig.Builder();

		return processConfigBuilder.setArguments(
			arguments
		).setBootstrapClassPath(
			classpath
		).setEnvironment(
			environments
		).setJavaExecutable(
			javaExecutable
		).setProcessLogConsumer(
			processLog -> {
				if (ProcessLog.Level.DEBUG == processLog.getLevel()) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else if (ProcessLog.Level.INFO == processLog.getLevel()) {
					if (_log.isInfoEnabled()) {
						_log.info(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else if (ProcessLog.Level.WARN == processLog.getLevel()) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else {
					_log.error(
						processLog.getMessage(), processLog.getThrowable());
				}
			}
		).setReactClassLoader(
			ElasticsearchBootstrap.class.getClassLoader()
		).setRuntimeClassPath(
			classpath
		).build();
	}

	private String _getJavaExecutable(File javaHome)
		throws FileNotFoundException {

		File bin = new File(javaHome, "bin");

		File java = new File(bin, "java");

		if (!java.exists() || !java.isFile()) {

			// Windows

			java = new File(bin, "java.exe");
		}

		if (java.exists() && java.isFile()) {
			if (_log.isDebugEnabled()) {
				_log.debug("Java executable: " + java.getAbsolutePath());
			}

			return java.getAbsolutePath();
		}

		throw new FileNotFoundException(
			"Unable to find the java executable used to start Liferay");
	}

	private void _parseJVMArguments(
			List<String> arguments, String javaExecutable, File jvmOptionsFile,
			File esClasspath)
		throws Exception {

		// TODO: inline ES jvm.options logic instead of starting a process

		String esClasspathPath = esClasspath.getAbsolutePath();

		if (!esClasspathPath.endsWith(File.separator)) {
			esClasspathPath = esClasspathPath.concat(File.separator);
		}

		List<String> command = new ArrayList<>();

		command.add(javaExecutable);
		command.add("-cp");
		command.add(esClasspathPath.concat("*"));
		command.add("org.elasticsearch.tools.launchers.JvmOptionsParser");
		command.add(jvmOptionsFile.getAbsolutePath());

		ProcessBuilder processBuilder = new ProcessBuilder();

		processBuilder.command(command);
		processBuilder.directory(_ES_HOME);

		Process process = null;

		try {
			process = processBuilder.start();

			try (InputStream inputStream = process.getInputStream();
				InputStream errorStream = process.getErrorStream()) {

				String output = StreamUtil.toString(inputStream);

				if (output.indexOf("${ES_TMPDIR}") != -1) {
					output = StringUtil.replace(
						output, "${ES_TMPDIR}",
						System.getProperty("java.io.tmpdir"));
				}

				if (_log.isDebugEnabled()) {
					_log.debug(output);
				}

				Collections.addAll(
					arguments, StringUtil.split(output, StringPool.SPACE));

				if (_log.isWarnEnabled()) {
					_log.warn(StreamUtil.toString(errorStream));
				}
			}
		}
		finally {
			if (process != null) {
				process.destroy();

				process.waitFor();
			}
		}
	}

	// TODO: ES_HOME is hard coded

	private static final File _ES_HOME = new File(
		PropsValues.LIFERAY_HOME, "elasticsearch-7.4.1-1");

	private static final Log _log = LogFactoryUtil.getLog(
		ElasticsearchBootstrap.class);

	private ProcessChannel<String> _processChannel;
	private final ProcessConfig _processConfig;
	private final ProcessExecutor _processExecutor;

}