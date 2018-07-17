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

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.lang.ClassLoaderPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration;

import freemarker.core.Environment;
import freemarker.core.TemplateClassResolver;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.utility.Execute;
import freemarker.template.utility.ObjectConstructor;

import java.util.Collection;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration",
	configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true
)
public class LiferayTemplateClassResolver implements TemplateClassResolver {

	@Override
	public Class<?> resolve(
			String className, Environment environment, Template template)
		throws TemplateException {

		if (className.equals(Execute.class.getName()) ||
			className.equals(ObjectConstructor.class.getName())) {

			throw new TemplateException(
				StringBundler.concat(
					"Instantiating ", className, " is not allowed in the ",
					"template for security reasons"),
				environment);
		}

		String[] restrictedClassNames = GetterUtil.getStringValues(
			_freeMarkerEngineConfiguration.restrictedClasses());

		for (String restrictedClassName : restrictedClassNames) {
			if (match(restrictedClassName, className)) {
				throw new TemplateException(
					StringBundler.concat(
						"Instantiating ", className, " is not allowed in the ",
						"template for security reasons"),
					environment);
			}
		}

		boolean allowed = false;

		String[] allowedClasseNames = GetterUtil.getStringValues(
			_freeMarkerEngineConfiguration.allowedClasses());

		for (String allowedClassName : allowedClasseNames) {
			if (match(allowedClassName, className)) {
				allowed = true;

				break;
			}
		}

		if (allowed) {
			try {
				Collection<ClassLoader> classLoaderMapValues =
					ClassLoaderPool.getClassLoaders();

				ClassLoader[] classLoaders = classLoaderMapValues.toArray(
					new ClassLoader[classLoaderMapValues.size()]);

				ClassLoader wwhitelistedAggregateClassLoader =
					AggregateClassLoader.getAggregateClassLoader(classLoaders);

				return Class.forName(
					className, true, wwhitelistedAggregateClassLoader);
			}
			catch (Exception e) {
				throw new TemplateException(e, environment);
			}
		}

		throw new TemplateException(
			StringBundler.concat(
				"Instantiating ", className, " is not allowed in the template ",
				"for security reasons"),
			environment);
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_freeMarkerEngineConfiguration = ConfigurableUtil.createConfigurable(
			FreeMarkerEngineConfiguration.class, properties);
	}

	@Deactivate
	protected void deactivate() {
	}

	protected boolean match(String className, String matchedClassName) {
		if (className.equals(StringPool.STAR)) {
			return true;
		}
		else if (className.endsWith(StringPool.STAR)) {
			if (matchedClassName.regionMatches(
					0, className, 0, className.length() - 1)) {

				return true;
			}
		}
		else if (className.equals(matchedClassName)) {
			return true;
		}
		else {
			int index = className.lastIndexOf('.');

			if ((className.length() == index) &&
				className.regionMatches(0, matchedClassName, 0, index)) {

				return true;
			}
		}

		return false;
	}

	@Modified
	protected void modified(
		BundleContext bundleContext, Map<String, Object> properties) {

		_freeMarkerEngineConfiguration = ConfigurableUtil.createConfigurable(
			FreeMarkerEngineConfiguration.class, properties);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayTemplateClassResolver.class);

	private volatile FreeMarkerEngineConfiguration
		_freeMarkerEngineConfiguration;

}