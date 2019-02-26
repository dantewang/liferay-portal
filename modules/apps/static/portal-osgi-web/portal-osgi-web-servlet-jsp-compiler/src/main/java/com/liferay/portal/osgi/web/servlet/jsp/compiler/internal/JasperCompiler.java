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

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.Options;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.JavacErrorDetail;
import org.apache.jasper.compiler.Jsr199JavaCompiler;
import org.apache.jasper.compiler.Node;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Dante Wang
 */
public class JasperCompiler extends Jsr199JavaCompiler {

	@Override
	public JavacErrorDetail[] compile(String className, Node.Nodes pageNodes)
		throws JasperException {

		classFiles = new ArrayList<>();

		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

		if (javaCompiler == null) {
			errDispatcher.jspError("jsp.error.nojdk");

			throw new JasperException("Unable to find Java compiler");
		}

		List<Diagnostic<? extends JavaFileObject>> diagnostics = null;

		try {
			diagnostics = _jspCompiler.compile(
				new StringJavaFileObject(
					className.substring(className.lastIndexOf('.') + 1),
					charArrayWriter.toString()),
				options, this::getJavaFileManager);

			if (diagnostics == null) {
				for (BytecodeFile bytecodeFile : classFiles) {
					rtctxt.setBytecode(
						bytecodeFile.getClassName(),
						bytecodeFile.getBytecode());
				}

				return null;
			}
		}
		catch (IOException ioe) {
			throw new JasperException(ioe);
		}

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

		return javacErrorDetails;
	}


	@Override
	public void init(
		JspCompilationContext jspCompilationContext,
		ErrorDispatcher errorDispatcher, boolean suppressLogging) {

		Options options = jspCompilationContext.getOptions();

		ServletContext servletContext =
			jspCompilationContext.getServletContext();

		_jspCompiler.init(options.getScratchDir(), servletContext);

		jspCompilationContext.setClassLoader(
			(URLClassLoader)servletContext.getClassLoader());

		initTLDMappings(
			servletContext, jspCompilationContext.getTagFileJarUrls());

		super.init(jspCompilationContext, errorDispatcher, suppressLogging);
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
	protected JavaFileObject getOutputFile(String className, URI uri) {
		Map<String, Map<String, JavaFileObject>> packageMap =
			rtctxt.getPackageMap();

		String packageName = className.substring(
			0, className.lastIndexOf(CharPool.PERIOD));

		// Swap the parent class's packageJavaFileObjects reference from a plain
		// HashMap to a thread safe ConcurrentHashMap

		Map<String, JavaFileObject> packageJavaFileObjects = packageMap.get(
			packageName);

		JavaFileObject javaFileObject = super.getOutputFile(className, uri);

		if (packageJavaFileObjects == null) {
			packageMap.put(
				packageName,
				new ConcurrentHashMap<>(packageMap.get(packageName)));
		}

		return javaFileObject;
	}

	@SuppressWarnings("unchecked")
	protected void initTLDMappings(
		ServletContext servletContext, Map<String, URL> tagFileJarUrls) {

		Map<String, String[]> tldMappings =
			(Map<String, String[]>)servletContext.getAttribute(
				Constants.JSP_TLD_URI_TO_LOCATION_MAP);

		if (tldMappings != null) {
			return;
		}

		tldMappings = new HashMap<>();

		try {
			for (Bundle bundle : _jspCompiler.getParticipatingBundles()) {
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
			Constants.JSP_TLD_URI_TO_LOCATION_MAP, tldMappings);
	}

	private static final Log _log = LogFactoryUtil.getLog(JasperCompiler.class);

	private final JspCompiler _jspCompiler = new JspCompiler();

}
