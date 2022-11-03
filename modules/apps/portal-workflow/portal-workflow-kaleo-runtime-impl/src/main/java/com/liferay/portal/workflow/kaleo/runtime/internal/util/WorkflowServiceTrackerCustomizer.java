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

package com.liferay.portal.workflow.kaleo.runtime.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.workflow.kaleo.definition.ScriptLanguage;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Jiaxu Wei
 */
public class WorkflowServiceTrackerCustomizer<T>
	implements ServiceTrackerCustomizer<T, T> {

	public WorkflowServiceTrackerCustomizer(
		BundleContext bundleContext, String errorMessage,
		boolean parseServiceMapKey, String propertyKey,
		Map<String, T> serviceMap, boolean throwException) {

		_bundleContext = bundleContext;
		_errorMessage = errorMessage;
		_parseServiceMapKey = parseServiceMapKey;
		_propertyKey = propertyKey;
		_serviceMap = serviceMap;
		_throwException = throwException;
	}

	@Override
	public T addingService(ServiceReference<T> serviceReference) {
		T service = _bundleContext.getService(serviceReference);

		String[] propertyValues = _getPropertyValues(service, serviceReference);

		for (String propertyValue : propertyValues) {
			try {
				_serviceMap.put(_getKey(propertyValue, service), service);
			}
			catch (KaleoDefinitionValidationException
						kaleoDefinitionValidationException) {

				throw new RuntimeException(kaleoDefinitionValidationException);
			}
		}

		return service;
	}

	@Override
	public void modifiedService(
		ServiceReference<T> serviceReference, T service) {
	}

	@Override
	public void removedService(
		ServiceReference<T> serviceReference, T service) {

		String[] propertyValues = _getPropertyValues(service, serviceReference);

		for (String propertyValue : propertyValues) {
			try {
				_serviceMap.remove(_getKey(propertyValue, service));
			}
			catch (KaleoDefinitionValidationException
						kaleoDefinitionValidationException) {

				throw new RuntimeException(kaleoDefinitionValidationException);
			}
		}

		_bundleContext.ungetService(serviceReference);
	}

	private String _getKey(String propertyValue, T service)
		throws KaleoDefinitionValidationException {

		if (_parseServiceMapKey) {
			ScriptLanguage scriptLanguage = ScriptLanguage.parse(propertyValue);

			if (scriptLanguage.equals(ScriptLanguage.JAVA)) {
				propertyValue =
					propertyValue + StringPool.COLON +
						ClassUtil.getClassName(service);
			}
		}

		return propertyValue;
	}

	private String[] _getPropertyValues(
		T service, ServiceReference<T> serviceReference) {

		Object value = serviceReference.getProperty(_propertyKey);

		String[] scriptingLanguages = GetterUtil.getStringValues(
			value, new String[] {String.valueOf(value)});

		if (_throwException && ArrayUtil.isEmpty(scriptingLanguages)) {
			if (_errorMessage == null) {
				_errorMessage =
					"The property \"" + _propertyKey + "\" is invalid for ";
			}

			throw new IllegalArgumentException(
				_errorMessage + ClassUtil.getClassName(service));
		}

		return scriptingLanguages;
	}

	private final BundleContext _bundleContext;
	private String _errorMessage;
	private final boolean _parseServiceMapKey;
	private final String _propertyKey;
	private final Map<String, T> _serviceMap;
	private final boolean _throwException;

}