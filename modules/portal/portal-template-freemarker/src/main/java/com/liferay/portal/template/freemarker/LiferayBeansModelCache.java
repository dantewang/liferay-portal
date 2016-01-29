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

package com.liferay.portal.template.freemarker;

import com.liferay.portal.kernel.concurrent.ConcurrentHashSet;
import com.liferay.portal.kernel.util.ReflectionUtil;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.util.ModelCache;
import freemarker.ext.util.ModelFactory;

import freemarker.template.TemplateModel;

import java.lang.reflect.Method;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Dante Wang
 */
public class LiferayBeansModelCache extends ModelCache {

	public LiferayBeansModelCache(
			BeansWrapper beansWrapper, Map<String, Object> helperUtilities)
		throws Exception {

		_beansWrapper = beansWrapper;

		_method = ReflectionUtil.getDeclaredMethod(
			BeansWrapper.class, "getModelFactory", Class.class);

		for (Object object : helperUtilities.values()) {
			if ((object instanceof Map)) {
				continue;
			}

			try {
				_acceptedObjects.add(object);
			}
			catch (NullPointerException e) {
				// Sliently bypass object that can't be added into the hashset
			}
		}

		setUseCache(true);
	}

	@Override
	public TemplateModel getInstance(Object object) {
		TemplateModel templateModel = _modelCache.get(object);

		if (templateModel == null) {
			templateModel = create(object);

			if (isCacheable(object)) {
				_modelCache.put(object, templateModel);
			}
		}

		return templateModel;
	}

	@Override
	protected TemplateModel create(Object object) {
		Class clazz = object.getClass();

		ModelFactory modelFactory = _modelFactories.get(clazz);

		if (modelFactory == null) {
			try {
				modelFactory = (ModelFactory)_method.invoke(
					_beansWrapper, clazz);

				_modelFactories.put(clazz, modelFactory);
			}
			catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}

		return modelFactory.create(object, _beansWrapper);
	}

	@Override
	protected boolean isCacheable(Object object) {
		return (object.getClass() != Boolean.class) &&
			_acceptedObjects.contains(object);
	}

	private final Set<Object> _acceptedObjects = new ConcurrentHashSet<>();
	private final BeansWrapper _beansWrapper;
	private final Method _method;
	private final ConcurrentMap<Object, TemplateModel> _modelCache =
		new ConcurrentHashMap<>();
	private final ConcurrentMap<Class, ModelFactory> _modelFactories =
		new ConcurrentHashMap<>();

}