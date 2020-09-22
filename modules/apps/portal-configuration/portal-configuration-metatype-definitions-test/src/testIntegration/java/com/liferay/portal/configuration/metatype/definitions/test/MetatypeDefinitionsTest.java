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

package com.liferay.portal.configuration.metatype.definitions.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeInformation;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeService;
import com.liferay.portal.configuration.metatype.definitions.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.test.util.metatype.MetatypeDefinitionsTestConfiguration;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.FrameworkUtil;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class MetatypeDefinitionsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDefaultValueConsistent() {
		MetatypeDefinitionsTestConfiguration
			metatypeDefinitionsTestConfiguration =
				ConfigurableUtil.createConfigurable(
					MetatypeDefinitionsTestConfiguration.class,
					Collections.emptyMap());

		Assert.assertEquals(
			"key1=value1,key2=va\\,lue2",
			metatypeDefinitionsTestConfiguration.singleValuedAttribute());
		Assert.assertArrayEquals(
			new String[] {"key1=value1", "key2=va,lue2"},
			metatypeDefinitionsTestConfiguration.multiValuedAttribute());

		ExtendedMetaTypeInformation extendedMetaTypeInformation =
			_extendedMetaTypeService.getMetaTypeInformation(
				FrameworkUtil.getBundle(
					MetatypeDefinitionsTestConfiguration.class));

		ExtendedObjectClassDefinition extendedObjectClassDefinition =
			extendedMetaTypeInformation.getObjectClassDefinition(
				MetatypeDefinitionsTestConfiguration.class.getName(), null);

		AttributeDefinition singleValuedAttributeDefinition =
			_getAttributeDefinition(
				"singleValuedAttribute", extendedObjectClassDefinition);

		Assert.assertEquals(
			metatypeDefinitionsTestConfiguration.singleValuedAttribute(),
			singleValuedAttributeDefinition.getDefaultValue()[0]);

		AttributeDefinition multiValuedAttributeDefinition =
			_getAttributeDefinition(
				"multiValuedAttribute", extendedObjectClassDefinition);

		Assert.assertArrayEquals(
			metatypeDefinitionsTestConfiguration.multiValuedAttribute(),
			multiValuedAttributeDefinition.getDefaultValue());
	}

	private AttributeDefinition _getAttributeDefinition(
		String id, ObjectClassDefinition objectClassDefinition) {

		AttributeDefinition[] attributeDefinitions =
			objectClassDefinition.getAttributeDefinitions(
				ObjectClassDefinition.ALL);

		for (AttributeDefinition attributeDefinition : attributeDefinitions) {
			if (id.equals(attributeDefinition.getID())) {
				return attributeDefinition;
			}
		}

		return null;
	}

	@Inject
	private ExtendedMetaTypeService _extendedMetaTypeService;

}