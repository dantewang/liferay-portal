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

package com.liferay.portal.template.velocity.internal;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateResourceLoader;
import com.liferay.portal.template.BaseTemplateResourceLoader;
import com.liferay.portal.template.TemplateResourceParser;
import com.liferay.portal.template.velocity.configuration.VelocityEngineConfiguration;

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
 * @author Peter Fellwock
 */
@Component(
	configurationPid = "com.liferay.portal.template.velocity.configuration.VelocityEngineConfiguration",
	configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true,
	service =
		{TemplateResourceLoader.class, VelocityTemplateResourceLoader.class}
)
public class VelocityTemplateResourceLoader
	extends BaseTemplateResourceLoader {

	@Override
	public String getName() {
		return null;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		VelocityEngineConfiguration velocityEngineConfiguration =
			ConfigurableUtil.createConfigurable(
				VelocityEngineConfiguration.class, properties);

		modificationCheckInterval =
			velocityEngineConfiguration.resourceModificationCheckInterval();

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
		target = "(lang.type=" + TemplateConstants.LANG_TYPE_VM + ")"
	)
	protected void setTemplateResourceParser(
		TemplateResourceParser templateResourceParser) {

		templateResourceParsers.add(templateResourceParser);
	}

	protected void unsetTemplateResourceParser(
		TemplateResourceParser templateResourceParser) {

		templateResourceParsers.remove(templateResourceParser);
	}

}