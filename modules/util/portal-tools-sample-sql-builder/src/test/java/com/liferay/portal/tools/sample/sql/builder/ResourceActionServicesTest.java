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

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.portal.kernel.exception.ResourceActionsException;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.util.SimpleCounter;

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Dante Wang
 */
public class ResourceActionServicesTest {

	@BeforeClass
	public static void setUpClass() {
		ResourceActionServices.init(new SimpleCounter(1));
	}

	@Test
	public void testPopulateResourceActions() throws ResourceActionsException {
		ResourceActions resourceActions =
			ResourceActionServices.getResourceActions();

		resourceActions.populatePortletResources(
			ResourceActionServicesTest.class.getClassLoader(), "default.xml");

		Map<String, Set<ResourceAction>> resourceActionMap =
			ResourceActionServices.getResourceActionMap();

		Assert.assertEquals(14, resourceActionMap.size());

		for (Set<ResourceAction> resourceActionSet :
				resourceActionMap.values()) {

			for (ResourceAction resourceAction : resourceActionSet) {
				System.out.println(resourceAction);
			}
		}
	}

}