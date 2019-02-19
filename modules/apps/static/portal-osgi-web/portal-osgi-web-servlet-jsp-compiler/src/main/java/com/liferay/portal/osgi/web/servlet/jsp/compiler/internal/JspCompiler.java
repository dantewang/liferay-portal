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
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.osgi.web.servlet.jsp.compiler.internal.util.ClassPathUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URI;
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
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.Options;
import org.apache.jasper.compiler.Compiler;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.JavacErrorDetail;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.compiler.SmapStratum;
import org.apache.jasper.compiler.TldCache;
import org.apache.jasper.servlet.JspServletWrapper;

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
 * @author Dante Wang
 */
public class JspCompiler extends Compiler {

	@Override
	public void init(
		JspCompilationContext jspCompilationContext,
		JspServletWrapper jspServletWrapper) {

		Options options = jspCompilationContext.getOptions();

		_jspRuntimeContext = jspCompilationContext.getRuntimeContext();

		_classPath.add(options.getScratchDir());

		ServletContext servletContext =
			jspCompilationContext.getServletContext();

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

		jspCompilationContext.setClassLoader(jspBundleClassloader);

		initClassPath(servletContext);
		//initTLDMappings(
		//	servletContext, jspCompilationContext.getTagFileJarUrls());

		super.init(jspCompilationContext, jspServletWrapper);
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

	protected void collectTLDMappings(
			Map<String, String[]> tldMappings, Map<String, URL> tagFileJarUrls,
			Bundle bundle)
		throws IOException {

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		List<String> resourcePaths = new ArrayList<>(
			bundleWiring.listResources(
				"/META-INF/", "*.tld", BundleWiring.LISTRESOURCES_RECURSE));

		resourcePaths.addAll(
			bundleWiring.listResources(
				"/WEB-INF/", "*.tld", BundleWiring.LISTRESOURCES_RECURSE));

		for (String resourcePath : resourcePaths) {
			URL url = bundle.getResource(resourcePath);

			String uri = TldURIUtil.getTldURI(url);

			if (uri != null) {
				String absoluteResourcePath = StringPool.SLASH.concat(
					resourcePath);

				tldMappings.put(
					uri.trim(), new String[] {absoluteResourcePath, null});

				String urlString = url.toExternalForm();

				tagFileJarUrls.put(
					absoluteResourcePath,
					new URL(
						urlString.substring(
							0, urlString.length() - resourcePath.length())));
			}
		}
	}

	@Override
	protected void generateClass(Map<String, SmapStratum> smaps)
		throws Exception {

		_classFiles = new ArrayList<>();

		String className = ctxt.getServletClassName();

		String javaFileName = ctxt.getServletJavaFileName();

		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

		if (javaCompiler == null) {
			errDispatcher.jspError("jsp.error.nojdk");

			throw new JasperException("Unable to find Java compiler");
		}

		DiagnosticCollector<JavaFileObject> diagnosticCollector =
			new DiagnosticCollector<>();

		StandardJavaFileManager standardJavaFileManager =
			javaCompiler.getStandardFileManager(
				diagnosticCollector, null, null);

		List<File> cpath = new ArrayList<>();

		try {
			standardJavaFileManager.setLocation(
				StandardLocation.CLASS_PATH, cpath);
		}
		catch (IOException ioe) {
			throw new JasperException(ioe);
		}

		try (JavaFileManager javaFileManager = getJavaFileManager(
				standardJavaFileManager)) {

			JavaCompiler.CompilationTask compilationTask = javaCompiler.getTask(
				null, javaFileManager, diagnosticCollector, _getOptions(), null,
				Arrays.asList(
					new StringJavaFileObject(
						className.substring(className.lastIndexOf('.') + 1),
						FileUtil.read(javaFileName))));

			if (_log.isDebugEnabled()) {
				_log.debug("Compiling JSP: ".concat(className));
			}

			if (compilationTask.call()) {
				//for (BytecodeFile bytecodeFile : _classFiles) {
				//	_jspRuntimeContext.setBytecode(
				//		bytecodeFile.getClassName(),
				//		bytecodeFile.getBytecode());
				//}

				return;
			}
		}
		catch (IOException ioe) {
			throw new JasperException(ioe);
		}

		List<Diagnostic<? extends JavaFileObject>> diagnostics =
			diagnosticCollector.getDiagnostics();

		JavacErrorDetail[] javacErrorDetails =
			new JavacErrorDetail[diagnostics.size()];

		for (int i = 0; i < diagnostics.size(); i++) {
			Diagnostic<? extends JavaFileObject> diagnostic = diagnostics.get(
				i);

			javacErrorDetails[i] = ErrorDispatcher.createJavacError(
				javaFileName, pageNodes,
				new StringBuilder(diagnostic.getMessage(null)),
				(int)diagnostic.getLineNumber());
		}
	}

	protected JavaFileManager getJavaFileManager(
		JavaFileManager javaFileManager) {

		if (javaFileManager instanceof StandardJavaFileManager) {
			StandardJavaFileManager standardJavaFileManager =
				(StandardJavaFileManager)javaFileManager;

			try {
				standardJavaFileManager.setLocation(
					StandardLocation.CLASS_PATH, _classPath);
			}
			catch (IOException ioe) {
				_log.error(ioe.getMessage(), ioe);
			}

			javaFileManager = new BundleJavaFileManager(
				_classLoader, standardJavaFileManager,
				_javaFileObjectResolvers);
		}

		return _getJavaFileManager(javaFileManager);
	}

	protected JavaFileObject getOutputFile(String className, URI uri) {
		String packageName = className.substring(
			0, className.lastIndexOf(CharPool.PERIOD));

		Map<String, JavaFileObject> packageJavaFileObjects = _packageMap.get(
			packageName);

		BytecodeFile bytecodeFile = new BytecodeFile(uri, className);

		if (packageJavaFileObjects == null) {
			packageJavaFileObjects = new ConcurrentHashMap<>();

			_packageMap.put(packageName, packageJavaFileObjects);
		}

		packageJavaFileObjects.put(className, bytecodeFile);

		_classFiles.add(bytecodeFile);

		return bytecodeFile;
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

	@SuppressWarnings("unchecked")
	protected void initTLDMappings(
		ServletContext servletContext, Map<String, URL> tagFileJarUrls) {

		Map<String, String[]> tldMappings =
			(Map<String, String[]>)servletContext.getAttribute(
				TldCache.SERVLET_CONTEXT_ATTRIBUTE_NAME);

		if (tldMappings != null) {
			return;
		}

		tldMappings = new HashMap<>();

		try {
			for (Bundle bundle : _allParticipatingBundles) {
				collectTLDMappings(tldMappings, tagFileJarUrls, bundle);
			}
		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

		Map<String, String> map =
			(Map<String, String>)servletContext.getAttribute(
				"jsp.taglib.mappings");

		if (map != null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				tldMappings.put(
					entry.getKey(), new String[] {entry.getValue(), null});
			}
		}

		servletContext.setAttribute(
			TldCache.SERVLET_CONTEXT_ATTRIBUTE_NAME, tldMappings);
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

	private JavaFileManager _getJavaFileManager(
		JavaFileManager javaFileManager) {

		return new ForwardingJavaFileManager<JavaFileManager>(javaFileManager) {

			@Override
			public JavaFileObject getJavaFileForOutput(
				Location location, String className, JavaFileObject.Kind kind,
				FileObject sibling) {

				return getOutputFile(
					className,
					URI.create(
						"file:///" + className.replace('.', '/') + kind));
			}

			@Override
			public String inferBinaryName(
				Location location, JavaFileObject javaFileObject) {

				if (javaFileObject instanceof BytecodeFile) {
					BytecodeFile bytecodeFile = (BytecodeFile)javaFileObject;

					return bytecodeFile.getClassName();
				}

				return super.inferBinaryName(location, javaFileObject);
			}

			@Override
			public Iterable<JavaFileObject> list(
					Location location, String packageName,
					Set<JavaFileObject.Kind> kinds, boolean recurse)
				throws IOException {

				if ((location == StandardLocation.CLASS_PATH) &&
					packageName.startsWith(Constants.JSP_PACKAGE_NAME)) {

					Map<String, JavaFileObject> packageFiles = _packageMap.get(
						packageName);

					if (packageFiles != null) {
						return packageFiles.values();
					}
				}

				Iterable<JavaFileObject> itr = super.list(
					location, packageName, kinds, recurse);

				return itr;
			}

		};
	}

	private List<String> _getOptions() {
		List<String> optionList = new ArrayList<>();

		optionList.add("-proc:none");

		String javaExtDirs = System.getProperty("java.ext.dirs");

		if (javaExtDirs != null) {
			optionList.add("-extdirs");
			optionList.add(javaExtDirs);
		}

		// In Tomcat 9.0.16 Jasper, options.getCompilerSourceVM() and
		// options.getCompilerTargetVM() never returns null

		optionList.add("-source");
		optionList.add(options.getCompilerSourceVM());
		optionList.add("-target");
		optionList.add(options.getCompilerTargetVM());

		if (options.getClassDebugInfo()) {
			optionList.add("-g");
		}
		else {
			optionList.add("-g:none");
		}

		return optionList;
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
	private List<BytecodeFile> _classFiles;
	private ClassLoader _classLoader;
	private final List<File> _classPath = new ArrayList<>();
	private final List<JavaFileObjectResolver> _javaFileObjectResolvers =
		new ArrayList<>();
	private JspRuntimeContext _jspRuntimeContext;
	private final Map<String, Map<String, JavaFileObject>> _packageMap =
		new ConcurrentHashMap<>();

	private static class BytecodeFile extends SimpleJavaFileObject {

		public BytecodeFile(URI uri, String className) {
			super(uri, Kind.CLASS);

			_className = className;
		}

		public byte[] getBytecode() {
			return _bytecode;
		}

		public String getClassName() {
			return _className;
		}

		public InputStream openInputStream() {
			return new ByteArrayInputStream(_bytecode);
		}

		public OutputStream openOutputStream() {
			return new ByteArrayOutputStream() {

				public void close() {
					_bytecode = toByteArray();
				}

			};
		}

		private byte[] _bytecode;
		private final String _className;

	}

}