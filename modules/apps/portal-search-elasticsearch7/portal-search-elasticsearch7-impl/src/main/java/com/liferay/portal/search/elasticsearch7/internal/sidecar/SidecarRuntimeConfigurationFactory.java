/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaDetector;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OSDetector;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.constants.SidecarConstants;
import com.liferay.portal.search.elasticsearch7.internal.util.ResourceUtil;
import com.liferay.portal.search.elasticsearch7.sidecar.PathUtil;
import com.liferay.portal.search.elasticsearch7.sidecar.Sidecar;
import com.liferay.portal.search.elasticsearch7.sidecar.SidecarRuntimeConfiguration;
import com.liferay.portal.search.elasticsearch7.sidecar.SidecarServerArgs;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import java.net.URL;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.elasticsearch.common.settings.Settings;

/**
 * @author Dante Wang
 */
public class SidecarRuntimeConfigurationFactory {

	public static SidecarRuntimeConfiguration create(
		ElasticsearchConfigurationWrapper elasticsearchConfigurationWrapper,
		ElasticsearchInstancePaths elasticsearchInstancePaths) {

		Path sidecarTempDirPath =
			SidecarRuntimeConfiguration.createSidecarTempDirPath();

		return new SidecarRuntimeConfiguration(
			_getNodeName(elasticsearchConfigurationWrapper),
			_createProcessConfig(
				elasticsearchConfigurationWrapper,
				elasticsearchInstancePaths.getHomePath(), sidecarTempDirPath),
			elasticsearchConfigurationWrapper.sidecarHeartbeatInterval(),
			elasticsearchInstancePaths.getHomePath(),
			_createSidecarServerArgs(
				elasticsearchConfigurationWrapper, elasticsearchInstancePaths),
			elasticsearchConfigurationWrapper.sidecarShutdownTimeout(),
			sidecarTempDirPath,
			ResourceUtil.getResourceAsString(
				SidecarRuntimeConfigurationFactory.class,
				SidecarConstants.SIDECAR_VERSION_FILE_NAME),
			elasticsearchInstancePaths.getWorkPath());
	}

	private static String _createClasspath(
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

	private static ProcessConfig _createProcessConfig(
		ElasticsearchConfigurationWrapper elasticsearchConfigurationWrapper,
		Path sidecarHomePath, Path sidecarTempDirPath) {

		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		ProtectionDomain protectionDomain = Sidecar.class.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL bundleURL = codeSource.getLocation();

		String bootstrapClassPath = _createClasspath(
			Paths.get(PropsValues.LIFERAY_SHIELDED_CONTAINER_LIB_PORTAL_DIR),
			path -> {
				String name = String.valueOf(path.getFileName());

				return name.contains("petra");
			});

		return builder.setArguments(
			_getJVMArguments(
				elasticsearchConfigurationWrapper, sidecarHomePath,
				sidecarTempDirPath)
		).setBootstrapClassPath(
			bootstrapClassPath
		).setEnvironment(
			HashMapBuilder.putAll(
				System.getenv()
			).put(
				"HOSTNAME", "localhost"
			).put(
				"LIBFFI_TMPDIR", sidecarHomePath.toString()
			).build()
		).setJavaExecutable(
			System.getProperty("java.home") + "/bin/java"
		).setProcessLogConsumer(
			Sidecar::consumeProcessLog
		).setReactClassLoader(
			Sidecar.class.getClassLoader()
		).setRuntimeClassPath(
			StringBundler.concat(
				bundleURL.getPath(), File.pathSeparator, bootstrapClassPath)
		).build();
	}

	private static SidecarServerArgs _createSidecarServerArgs(
		ElasticsearchConfigurationWrapper elasticsearchConfigurationWrapper,
		ElasticsearchInstancePaths elasticsearchInstancePaths) {

		Settings settings = ElasticsearchInstanceSettingsBuilder.builder(
		).clusterName(
			elasticsearchConfigurationWrapper.clusterName()
		).discoveryTypeSingleNode(
			true
		).elasticsearchConfigurationWrapper(
			elasticsearchConfigurationWrapper
		).elasticsearchInstancePaths(
			elasticsearchInstancePaths
		).httpPortRange(
			new HttpPortRange(elasticsearchConfigurationWrapper)
		).nodeName(
			_getNodeName(elasticsearchConfigurationWrapper)
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

		Path sidecarHomePath = elasticsearchInstancePaths.getHomePath();

		return new SidecarServerArgs(
			String.valueOf(
				sidecarHomePath.resolve(
					SidecarRuntimeConfiguration.LIFERAY_SIDECAR_CONFIG)),
			false, String.valueOf(sidecarHomePath.resolve("logs")), false,
			settingsMap);
	}

	private static List<String> _getJVMArguments(
		ElasticsearchConfigurationWrapper elasticsearchConfigurationWrapper,
		Path sidecarHomePath, Path sidecarTempDirPath) {

		List<String> arguments = new ArrayList<>();

		for (String jvmOption :
				elasticsearchConfigurationWrapper.sidecarJVMOptions()) {

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

		if (elasticsearchConfigurationWrapper.sidecarDebug()) {
			arguments.add(
				elasticsearchConfigurationWrapper.sidecarDebugSettings());
		}

		Path configFolder = sidecarHomePath.resolve(
			SidecarRuntimeConfiguration.LIFERAY_SIDECAR_CONFIG);

		if (Files.exists(configFolder)) {
			PathUtil.deleteDir(configFolder);
		}

		try {
			Files.createDirectories(configFolder);

			Files.write(
				configFolder.resolve("log4j2.properties"),
				Arrays.asList(
					"logger.bootstrapchecks.name=org.elasticsearch.bootstrap." +
						"BootstrapChecks",
					"logger.bootstrapchecks.level=error",
					"logger.deprecation.name=org.elasticsearch.deprecation",
					"logger.deprecation.level=error", StringPool.BLANK,
					ResourceUtil.getResourceAsString(
						SidecarRuntimeConfigurationFactory.class,
						"/log4j2-sidecar.properties")));
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to copy log4j2.properties to " + configFolder,
				ioException);
		}

		arguments.add(
			"--enable-native-access=org.elasticsearch.nativeaccess," +
				"org.apache.lucene.core");
		arguments.add("--enable-native-access=ALL-UNNAMED");
		arguments.add("-Des.distribution.type=tar");
		arguments.add("-Des.networkaddress.cache.negative.ttl=10");
		arguments.add("-Des.networkaddress.cache.ttl=60");
		arguments.add("-Des.path.conf=" + configFolder);
		arguments.add("-Dfile.encoding=UTF-8");
		arguments.add("-Dio.netty.noKeySetOptimization=true");
		arguments.add("-Dio.netty.noUnsafe=true");
		arguments.add("-Dio.netty.recycler.maxCapacityPerThread=0");
		arguments.add("-Djava.awt.headless=true");
		arguments.add("-Djava.io.tmpdir=" + sidecarTempDirPath);
		arguments.add("-Djna.nosys=true");
		arguments.add("-Dlog4j.shutdownHookEnabled=false");
		arguments.add("-Dlog4j2.disable.jmx=true");
		arguments.add("-Dlog4j2.formatMsgNoLookups=true");
		arguments.add(
			"-Dorg.apache.lucene.vectorization.upperJavaFeatureVersion=21");

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
		arguments.add("--module-path=" + sidecarHomePath.resolve("lib"));
		arguments.add("-Djdk.module.main=org.elasticsearch.server");

		// Apply agent to load modified classes

		Path path = Path.of(
			PropsUtil.get(PropsKeys.LIFERAY_HOME), "elasticsearch-sidecar",
			"com.liferay.portal.search.elasticsearch7.sidecar.agent.jar");

		arguments.add("-javaagent:" + path.toAbsolutePath());

		return arguments;
	}

	private static String _getNodeName(
		ElasticsearchConfigurationWrapper elasticsearchConfigurationWrapper) {

		String nodeName = elasticsearchConfigurationWrapper.nodeName();

		if (!Validator.isBlank(nodeName)) {
			return nodeName;
		}

		return "liferay_sidecar";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SidecarRuntimeConfigurationFactory.class);

}