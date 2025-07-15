/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaDetector;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OSDetector;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.constants.SidecarConstants;
import com.liferay.portal.search.elasticsearch7.internal.util.ResourceUtil;
import com.liferay.portal.search.elasticsearch7.sidecar.agent.SidecarAgent;
import com.liferay.portal.util.PortalClassPathUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import java.net.URISyntaxException;
import java.net.URL;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.elasticsearch.common.settings.Settings;

/**
 * @author Dante Wang
 */
public class SidecarRuntimeConfigurationBuilder {

	public static SidecarRuntimeConfigurationBuilder builder() {
		return new SidecarRuntimeConfigurationBuilder();
	}

	public SidecarRuntimeConfiguration build() {
		String sidecarVersion = ResourceUtil.getResourceAsString(
			getClass(), SidecarConstants.SIDECAR_VERSION_FILE_NAME);

		_installElasticsearchIfNeeded(sidecarVersion);

		try {
			_sidecarTempDirPath = Files.createTempDirectory("sidecar");
		}
		catch (IOException ioException) {
			throw new IllegalStateException(
				"Unable to create temp folder", ioException);
		}

		String nodeName = _getNodeName();

		return new SidecarRuntimeConfiguration(
			_elasticsearchConfigurationWrapper.sidecarHeartbeatInterval(),
			_sidecarHomePath, nodeName, _createProcessConfig(),
			_elasticsearchConfigurationWrapper.sidecarShutdownTimeout(),
			_createSidecarServerArgs(nodeName), _sidecarTempDirPath,
			sidecarVersion);
	}

	public SidecarRuntimeConfigurationBuilder elasticsearchConfigurationWrapper(
		ElasticsearchConfigurationWrapper elasticsearchConfigurationWrapper) {

		_elasticsearchConfigurationWrapper = elasticsearchConfigurationWrapper;

		return this;
	}

	public SidecarRuntimeConfigurationBuilder elasticsearchInstancePaths(
		ElasticsearchInstancePaths elasticsearchInstancePaths) {

		_elasticsearchInstancePaths = elasticsearchInstancePaths;

		_sidecarHomePath = _elasticsearchInstancePaths.getHomePath();

		return this;
	}

	private SidecarRuntimeConfigurationBuilder() {
	}

	private String _createClasspath(
		Path dirPath, DirectoryStream.Filter<Path> filter) {

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				dirPath, filter)) {

			StringBundler sb = new StringBundler();

			directoryStream.forEach(
				path -> {
					sb.append(path);
					sb.append(File.pathSeparator);
				});

			if (sb.index() > 0) {
				sb.setIndex(sb.index() - 1);
			}

			return sb.toString();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to iterate " + dirPath, ioException);
		}
	}

	private ProcessConfig _createProcessConfig() {
		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		URL bundleURL = _getBundleURL(Sidecar.class);

		String bootstrapClassPath = _createClasspath(
			Paths.get(PropsValues.LIFERAY_SHIELDED_CONTAINER_LIB_PORTAL_DIR),
			path -> {
				String name = String.valueOf(path.getFileName());

				return name.contains("petra");
			});

		return builder.setArguments(
			_getJVMArguments()
		).setBootstrapClassPath(
			bootstrapClassPath
		).setEnvironment(
			_getEnvironment()
		).setJavaExecutable(
			System.getProperty("java.home") + "/bin/java"
		).setProcessLogConsumer(
			PortalClassPathUtil.createProcessLogConsumer(
				LogFactoryUtil.getLog(Sidecar.class))
		).setReactClassLoader(
			Sidecar.class.getClassLoader()
		).setRuntimeClassPath(
			StringBundler.concat(
				bundleURL.getPath(), File.pathSeparator, bootstrapClassPath)
		).build();
	}

	private SidecarServerArgs _createSidecarServerArgs(String nodeName) {
		Settings settings = ElasticsearchInstanceSettingsBuilder.builder(
		).clusterName(
			_elasticsearchConfigurationWrapper.clusterName()
		).discoveryTypeSingleNode(
			true
		).elasticsearchConfigurationWrapper(
			_elasticsearchConfigurationWrapper
		).elasticsearchInstancePaths(
			_elasticsearchInstancePaths
		).httpPortRange(
			new HttpPortRange(_elasticsearchConfigurationWrapper)
		).nodeName(
			nodeName
		).build();

		StringBundler sb = new StringBundler((2 * settings.size()) + 1);

		sb.append("Sidecar Elasticsearch properties : {");

		Map<String, Serializable> settingsMap = new TreeMap<>();

		for (String key : settings.keySet()) {
			List<String> list = settings.getAsList(key);

			if (ListUtil.isEmpty(list)) {
				continue;
			}

			String keyValue = StringBundler.concat(
				key, StringPool.EQUAL, StringUtil.merge(list));

			sb.append(keyValue);

			sb.append(StringPool.COMMA);

			if (list.size() == 1) {
				settingsMap.put(key, list.get(0));
			}
			else {
				settingsMap.put(key, new ArrayList<>(list));
			}
		}

		sb.setStringAt(StringPool.CLOSE_CURLY_BRACE, sb.index() - 1);

		if (_log.isDebugEnabled()) {
			_log.debug(sb.toString());
		}

		return new SidecarServerArgs(
			String.valueOf(_sidecarTempDirPath.resolve("config")), false,
			String.valueOf(_sidecarHomePath.resolve("logs")), false,
			settingsMap);
	}

	private URL _getBundleURL(Class<?> clazz) {
		ProtectionDomain protectionDomain = clazz.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		return codeSource.getLocation();
	}

	private Distribution _getElasticsearchDistribution(String sidecarVersion) {
		if (sidecarVersion.equals(ElasticsearchDistribution.VERSION)) {
			return new ElasticsearchDistribution();
		}

		throw new IllegalArgumentException(
			"Unsupported Elasticsearch version: " + sidecarVersion);
	}

	private HashMap<String, String> _getEnvironment() {
		return HashMapBuilder.putAll(
			System.getenv()
		).put(
			"HOSTNAME", "localhost"
		).put(
			"LIBFFI_TMPDIR", _sidecarHomePath.toString()
		).build();
	}

	private List<String> _getJVMArguments() {
		List<String> arguments = new ArrayList<>();

		for (String jvmOption :
				_elasticsearchConfigurationWrapper.sidecarJVMOptions()) {

			if (jvmOption.contains("|")) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						jvmOption + " is not a valid format for the JVM " +
							"options and will be ignored");
				}
			}
			else {
				arguments.add(jvmOption);
			}
		}

		if (_elasticsearchConfigurationWrapper.sidecarDebug()) {
			arguments.add(
				_elasticsearchConfigurationWrapper.sidecarDebugSettings());
		}

		arguments.add("-Des.distribution.type=tar");
		arguments.add("-Des.networkaddress.cache.negative.ttl=10");
		arguments.add("-Des.networkaddress.cache.ttl=60");
		arguments.add("-Dfile.encoding=UTF-8");
		arguments.add("-Dio.netty.noKeySetOptimization=true");
		arguments.add("-Dio.netty.noUnsafe=true");
		arguments.add("-Dio.netty.recycler.maxCapacityPerThread=0");
		arguments.add("-Djava.awt.headless=true");
		arguments.add("-Djava.io.tmpdir=" + _sidecarTempDirPath);
		arguments.add("-Djna.nosys=true");
		arguments.add("-Dlog4j.shutdownHookEnabled=false");
		arguments.add("-Dlog4j2.disable.jmx=true");
		arguments.add("-Dlog4j2.formatMsgNoLookups=true");
		arguments.add(
			"-Dorg.apache.lucene.vectorization.upperJavaFeatureVersion=21");
		arguments.add("--enable-native-access=ALL-UNNAMED");
		arguments.add(
			"--enable-native-access=org.elasticsearch.nativeaccess," +
				"org.apache.lucene.core");

		if (JavaDetector.isJDK21() && OSDetector.isLinux()) {
			arguments.add("-XX:-UseContainerSupport");
		}

		// Modules

		arguments.add("--add-modules=jdk.incubator.vector");
		arguments.add("--add-modules=jdk.management.agent");
		arguments.add("--add-modules=jdk.net");
		arguments.add("--add-modules=ALL-MODULE-PATH");
		arguments.add(
			"--add-opens=org.elasticsearch.server/org.elasticsearch." +
				"bootstrap=ALL-UNNAMED");
		arguments.add("--module-path=" + _sidecarHomePath.resolve("lib"));
		arguments.add("-Djdk.module.main=org.elasticsearch.server");

		// Apply agent to load modified classes

		URL sidecarAgentBundleURL = _getBundleURL(SidecarAgent.class);

		try {
			arguments.add(
				"-javaagent:" + Path.of(sidecarAgentBundleURL.toURI()));
		}
		catch (URISyntaxException uriSyntaxException) {
			ReflectionUtil.throwException(uriSyntaxException);
		}

		return arguments;
	}

	private String _getNodeName() {
		String nodeName = _elasticsearchConfigurationWrapper.nodeName();

		if (!Validator.isBlank(nodeName)) {
			return nodeName;
		}

		return "liferay_sidecar";
	}

	private void _installElasticsearchIfNeeded(String sidecarVersion) {
		ElasticsearchInstaller.builder(
		).distributablesDirectoryPath(
			_elasticsearchInstancePaths.getWorkPath()
		).distribution(
			_getElasticsearchDistribution(sidecarVersion)
		).installationDirectoryPath(
			_sidecarHomePath
		).build(
		).install();

		Path modulesPath = _sidecarHomePath.resolve(
			Sidecar.SIDECAR_MODULES_FOLDER_NAME);

		if (Files.exists(modulesPath)) {
			return;
		}

		Path defaultModulesPath = _sidecarHomePath.resolve(
			Sidecar.DEFAULT_MODULES_FOLDER_NAME);

		try {
			PathUtil.copyDirectory(
				defaultModulesPath, modulesPath,
				dir -> {
					if (Objects.equals(dir, defaultModulesPath)) {
						return false;
					}

					for (String sidecarModuleName :
							_elasticsearchConfigurationWrapper.
								sidecarModuleNames()) {

						if (dir.startsWith(
								defaultModulesPath.resolve(
									sidecarModuleName))) {

							return false;
						}
					}

					return true;
				});
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SidecarRuntimeConfigurationBuilder.class);

	private ElasticsearchConfigurationWrapper
		_elasticsearchConfigurationWrapper;
	private ElasticsearchInstancePaths _elasticsearchInstancePaths;
	private Path _sidecarHomePath;
	private Path _sidecarTempDirPath;

}