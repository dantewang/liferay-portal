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

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateResourceLoader;
import com.liferay.portal.template.BaseTemplateResourceLoader;
import com.liferay.portal.template.TemplateResourceParser;
import com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Igor Spasic
 */
@Component(
	configurationPid = "com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration",
	configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true,
	service =
		{FreeMarkerTemplateResourceLoader.class, TemplateResourceLoader.class}
)
public class FreeMarkerTemplateResourceLoader
	extends BaseTemplateResourceLoader {

	@Override
	public String getName() {
		return TemplateConstants.LANG_TYPE_FTL;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_freeMarkerEngineConfiguration = ConfigurableUtil.createConfigurable(
			FreeMarkerEngineConfiguration.class, properties);

		modificationCheckInterval =
			_freeMarkerEngineConfiguration.resourceModificationCheck();

		init();
	}

	@Deactivate
	protected void deactivate() {
		destroy();
	}

	@Reference(unbind = "-")
	protected void setMultiVMPool(MultiVMPool multiVMPool) {
		this.multiVMPool = multiVMPool;
	}

	@Reference(unbind = "-")
	protected void setSingleVMPool(SingleVMPool singleVMPool) {
		this.singleVMPool = singleVMPool;
	}

	@Reference(
		cardinality = ReferenceCardinality.AT_LEAST_ONE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(lang.type=" + TemplateConstants.LANG_TYPE_FTL + ")"
	)
	protected void setTemplateResourceParser(
		TemplateResourceParser templateResourceParser) {

		templateResourceParsers.add(templateResourceParser);
	}

	protected void unsetTemplateResourceParser(
		TemplateResourceParser templateResourceParser) {

		templateResourceParsers.remove(templateResourceParser);
	}

	private static volatile FreeMarkerEngineConfiguration
		_freeMarkerEngineConfiguration;

}