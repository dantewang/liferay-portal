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

package com.liferay.portal.configuration.test.util.metatype;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author Dante Wang
 */
@Meta.OCD(
	id = "com.liferay.portal.configuration.test.util.metatype.MetatypeDefinitionsTestConfiguration"
)
public interface MetatypeDefinitionsTestConfiguration {

	@Meta.AD(deflt = "key1=value1,key2=va\\,lue2", required = false)
	public String[] multiValuedAttribute();

	@Meta.AD(deflt = "key1=value1,key2=va\\,lue2", required = false)
	public String singleValuedAttribute();

}