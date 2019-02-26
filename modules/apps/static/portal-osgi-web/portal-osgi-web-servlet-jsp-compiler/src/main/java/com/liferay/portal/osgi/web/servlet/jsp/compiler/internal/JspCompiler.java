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

package com.liferay.portal.osgi.web.servlet.jsp.compiler.internal;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.concurrent.ConcurrentReferenceKeyHashMap;
import com.liferay.petra.concurrent.ConcurrentReferenceValueHashMap;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.osgi.web.servlet.jsp.compiler.internal.util.ClassPathUtil;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.servlet.ServletContext;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.apache.jasper.Constants;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class JspCompiler {

	public List<Diagnostic<? extends JavaFileObject>> compile(
			JavaFileObject sourceJavaFileObject, List<String> compileOptions,
			Function<JavaFileManager, JavaFileManager> javaFileManagerFunction)
		throws IOException {

		DiagnosticCollector<JavaFileObject> diagnosticCollector =
			new DiagnosticCollector<>();

		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

		StandardJavaFileManager standardJavaFileManager =
			javaCompiler.getStandardFileManager(
				diagnosticCollector, null, null);

		standardJavaFileManager.setLocation(
			StandardLocation.CLASS_PATH, _classPath);

		try (JavaFileManager javaFileManager = javaFileManagerFunction.apply(
				new BundleJavaFileManager(
					_classLoader, standardJavaFileManager,
					_javaFileObjectResolvers))) {

			JavaCompiler.CompilationTask compilationTask = javaCompiler.getTask(
				null, javaFileManager, diagnosticCollector, compileOptions,
				null, Arrays.asList(sourceJavaFileObject));

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Compiling JSP: ".concat(sourceJavaFileObject.getName()));
			}

			if (compilationTask.call()) {
				return null;
			}
		}

		return diagnosticCollector.getDiagnostics();
	}

	public void init(File scratchDir, ServletContext servletContext) {
		_classPath.add(scratchDir);

		ClassLoader classLoader = servletContext.getClassLoader();

		if (!(classLoader instanceof JspBundleClassloader)) {
			throw new IllegalStateException(
				"Class loader is not an instance of JspBundleClassloader");
		}

		JspBundleClassloader jspBundleClassloader =
			(JspBundleClassloader)classLoader;

		_allParticipatingBundles = jspBundleClassloader.getBundles();

		Bundle bundle = _allParticipatingBundles[0];

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		_classLoader = bundleWiring.getClassLoader();

		for (Bundle participatingBundle : _allParticipatingBundles) {
			bundleWiring = participatingBundle.adapt(BundleWiring.class);

			for (BundleWire bundleWire : bundleWiring.getRequiredWires(null)) {
				BundleWiring providedBundleWiring =
					bundleWire.getProviderWiring();

				_bundleWiringPackageNames.put(
					providedBundleWiring,
					_collectPackageNames(providedBundleWiring));
			}

			_javaFileObjectResolvers.add(
				new JspJavaFileObjectResolver(
					bundleWiring, _jspBundleWiring, _bundleWiringPackageNames,
					_serviceTracker));
		}

		if (_log.isInfoEnabled()) {
			StringBundler sb = new StringBundler(
				_bundleWiringPackageNames.size() * 4 + 6);

			sb.append("JSP compiler for bundle ");
			sb.append(bundle.getSymbolicName());
			sb.append(StringPool.DASH);
			sb.append(bundle.getVersion());
			sb.append(" has dependent bundle wirings: ");

			for (BundleWiring curBundleWiring :
				_bundleWiringPackageNames.keySet()) {

				Bundle currentBundle = curBundleWiring.getBundle();

				sb.append(currentBundle.getSymbolicName());

				sb.append(StringPool.DASH);
				sb.append(currentBundle.getVersion());
				sb.append(StringPool.COMMA_AND_SPACE);
			}

			sb.setIndex(sb.index() - 1);

			_log.info(sb.toString());
		}

		initClassPath(servletContext);
	}

	protected void addDependenciesToClassPath() {
		ClassLoader frameworkClassLoader = Bundle.class.getClassLoader();

		for (String className : _JSP_COMPILER_DEPENDENCIES) {
			try {
				Class<?> clazz = Class.forName(
					className, true, frameworkClassLoader);

				addDependencyToClassPath(clazz);
			}
			catch (ClassNotFoundException cnfe) {
				_log.error(
					"Unable to add depedency " + className +
						" to the classpath");
			}
		}
	}

	protected void addDependencyToClassPath(Class<?> clazz) {
		ProtectionDomain protectionDomain = clazz.getProtectionDomain();

		if (protectionDomain == null) {
			return;
		}

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		try {
			File file = ClassPathUtil.getFile(url);

			if ((file == null) && _log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Ignoring URL ", url, " because of unknown protocol ",
						url.getProtocol()));
			}

			if (file.exists() && file.canRead()) {
				_classPath.remove(file);

				_classPath.add(0, file);
			}
		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	protected Bundle[] getParticipatingBundles() {
		return _allParticipatingBundles;
	}

	protected void initClassPath(ServletContext servletContext) {
		if (System.getSecurityManager() != null) {
			AccessController.doPrivileged(
				new PrivilegedAction<Void>() {

					@Override
					public Void run() {
						addDependenciesToClassPath();

						return null;
					}

				});
		}
		else {
			addDependenciesToClassPath();
		}
	}

	private static Set<String> _collectPackageNames(BundleWiring bundleWiring) {
		Set<String> packageNames = _bundleWiringPackageNamesCache.get(
			bundleWiring);

		if (packageNames != null) {
			return packageNames;
		}

		packageNames = new HashSet<>();

		for (BundleCapability bundleCapability :
				bundleWiring.getCapabilities(
					BundleRevision.PACKAGE_NAMESPACE)) {

			Map<String, Object> attributes = bundleCapability.getAttributes();

			Object packageName = attributes.get(
				BundleRevision.PACKAGE_NAMESPACE);

			if (packageName != null) {
				packageNames.add((String)packageName);
			}
		}

		_bundleWiringPackageNamesCache.put(bundleWiring, packageNames);

		return packageNames;
	}

	private static final String[] _JSP_COMPILER_DEPENDENCIES = {
		"com.liferay.portal.kernel.exception.PortalException",
		"com.liferay.portal.util.PortalImpl", "javax.portlet.PortletException",
		"javax.servlet.ServletException"
	};

	private static final Log _log = LogFactoryUtil.getLog(JspCompiler.class);

	private static final Map<BundleWiring, Set<String>>
		_bundleWiringPackageNamesCache = new ConcurrentReferenceKeyHashMap<>(
			new ConcurrentReferenceValueHashMap<BundleWiring, Set<String>>(
				FinalizeManager.SOFT_REFERENCE_FACTORY),
			FinalizeManager.WEAK_REFERENCE_FACTORY);
	private static final BundleWiring _jspBundleWiring;
	private static final Map<BundleWiring, Set<String>>
		_jspBundleWiringPackageNames = new HashMap<>();
	private static final ServiceTracker
		<Map<String, List<URL>>, Map<String, List<URL>>> _serviceTracker;

	static {
		Bundle jspBundle = FrameworkUtil.getBundle(JspCompiler.class);

		_jspBundleWiring = jspBundle.adapt(BundleWiring.class);

		for (BundleWire bundleWire : _jspBundleWiring.getRequiredWires(null)) {
			BundleWiring providedBundleWiring = bundleWire.getProviderWiring();

			Set<String> packageNames = _collectPackageNames(
				providedBundleWiring);

			_jspBundleWiringPackageNames.put(
				providedBundleWiring, packageNames);
		}

		BundleContext bundleContext = jspBundle.getBundleContext();

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext,
			"(&(jsp.compiler.resource.map=*)(objectClass=" +
				Map.class.getName() + "))");
	}

	private Bundle[] _allParticipatingBundles;
	private final Map<BundleWiring, Set<String>> _bundleWiringPackageNames =
		new HashMap<>(_jspBundleWiringPackageNames);
	private ClassLoader _classLoader;
	private final List<File> _classPath = new ArrayList<>();
	private final List<JavaFileObjectResolver> _javaFileObjectResolvers =
		new ArrayList<>();

}