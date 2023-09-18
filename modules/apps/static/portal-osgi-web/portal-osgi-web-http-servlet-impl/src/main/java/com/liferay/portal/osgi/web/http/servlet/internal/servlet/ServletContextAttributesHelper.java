/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ProxyFactory;

import java.io.File;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.eclipse.equinox.http.servlet.internal.context.ContextController;
import org.eclipse.equinox.http.servlet.internal.context.ProxyContext;

/**
 * @author Dante Wang
 */
public class ServletContextAttributesHelper extends ProxyContext {

	public ServletContextAttributesHelper(
		String contextName, ServletContext parentServletContext,
		File parentServletContextTempDir) {

		super(contextName, _dummyServletContext);

		_parentServletContext = parentServletContext;

		if (parentServletContextTempDir == null) {
			_servletContextTempDir = null;

			return;
		}

		_servletContextTempDir = new File(
			parentServletContextTempDir, contextName + hashCode());

		_servletContextTempDir.mkdirs();

		_attributesDictionary.put(
			JavaConstants.JAVAX_SERVLET_CONTEXT_TEMPDIR,
			_servletContextTempDir);
	}

	@Override
	public void createContextAttributes(ContextController contextController) {
	}

	@Override
	public void destroy() {
		if (_servletContextTempDir != null) {
			FileUtil.deltree(_servletContextTempDir);
		}
	}

	@Override
	public void destroyContextAttributes(ContextController contextController) {
	}

	public Dictionary<String, Object> getContextAttributes(
		ContextController contextController) {

		return _attributesDictionary;
	}

	public ServletContext getServletContext() {
		return _parentServletContext;
	}

	@Override
	public String getServletPath() {
		return StringPool.BLANK;
	}

	private static final ServletContext _dummyServletContext =
		ProxyFactory.newDummyInstance(ServletContext.class);

	private final Dictionary<String, Object> _attributesDictionary =
		new Dictionary<String, Object>() {

			@Override
			public Enumeration<Object> elements() {
				return Collections.enumeration(_attributesMap.values());
			}

			@Override
			public Object get(Object key) {
				return _attributesMap.get(key);
			}

			@Override
			public boolean isEmpty() {
				return _attributesMap.isEmpty();
			}

			@Override
			public Enumeration<String> keys() {
				return Collections.enumeration(_attributesMap.keySet());
			}

			@Override
			public Object put(String key, Object value) {
				return _attributesMap.put(key, value);
			}

			@Override
			public Object remove(Object key) {
				return _attributesMap.remove(key);
			}

			@Override
			public int size() {
				return _attributesMap.size();
			}

			private final Map<String, Object> _attributesMap =
				new ConcurrentHashMap<>();

		};

	private final ServletContext _parentServletContext;
	private final File _servletContextTempDir;

}