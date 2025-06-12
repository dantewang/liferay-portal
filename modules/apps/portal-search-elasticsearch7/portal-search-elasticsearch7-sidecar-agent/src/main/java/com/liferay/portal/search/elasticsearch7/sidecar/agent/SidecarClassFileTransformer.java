/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.sidecar.agent;

import java.lang.instrument.ClassFileTransformer;

import java.security.ProtectionDomain;

import java.util.Map;
import java.util.function.Consumer;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author Dante Wang
 */
public class SidecarClassFileTransformer implements ClassFileTransformer {

	@Override
	public byte[] transform(
		ClassLoader loader, String className, Class<?> classBeingRedefined,
		ProtectionDomain protectionDomain, byte[] classFileBuffer) {

		Map.Entry<String, Consumer<MethodVisitor>> entry =
			_methodVisitorConsumers.get(className);

		if (entry == null) {
			return null;
		}

		return ClassModificationUtil.getModifiedClassBytes(
			classFileBuffer, entry.getKey(), entry.getValue());
	}

	private static final Map<String, Map.Entry<String, Consumer<MethodVisitor>>>
		_methodVisitorConsumers;
	private static final Consumer<MethodVisitor>
		_wipingLogicMethodVisitorConsumer = methodVisitor -> {
			methodVisitor.visitCode();
			methodVisitor.visitInsn(Opcodes.RETURN);
		};

	static {
		_methodVisitorConsumers = Map.of(
			"org/elasticsearch/entitlement/bootstrap/EntitlementBootstrap",
			Map.entry("bootstrap", _wipingLogicMethodVisitorConsumer),
			"org/elasticsearch/nativeaccess/PosixNativeAccess",
			Map.entry(
				"definitelyRunningAsRoot",
				methodVisitor -> {
					methodVisitor.visitCode();
					methodVisitor.visitInsn(Opcodes.ICONST_0);
					methodVisitor.visitInsn(Opcodes.IRETURN);
				}),
			"org/elasticsearch/bootstrap/Bootstrap",
			Map.entry("sendCliMarker", _wipingLogicMethodVisitorConsumer),
			"org/elasticsearch/bootstrap/Elasticsearch",
			Map.entry(
				"startCliMonitorThread", _wipingLogicMethodVisitorConsumer),
			"org/elasticsearch/bootstrap/Elasticsearch$EntitlementSelfTester",
			Map.entry("entitlementSelfTest", _wipingLogicMethodVisitorConsumer),
			"org/elasticsearch/common/settings/KeyStoreWrapper",
			Map.entry("save", _wipingLogicMethodVisitorConsumer),
			"org/elasticsearch/bootstrap/Security",
			Map.entry("configure", _wipingLogicMethodVisitorConsumer),
			"org/elasticsearch/bootstrap/Spawner",
			Map.entry(
				"spawnNativeControllers", _wipingLogicMethodVisitorConsumer));
	}

}