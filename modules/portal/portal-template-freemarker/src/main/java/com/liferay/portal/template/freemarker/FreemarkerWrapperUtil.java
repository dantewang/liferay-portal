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

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.ObjectWrapper;

/**
 * @author Dante Wang
 */
public class FreemarkerWrapperUtil {

	public static BeansWrapper getBeansWrapper() {
		BeansWrapperBuilder beansWrapperBuilder = new BeansWrapperBuilder(
			Configuration.VERSION_2_3_23);

		return beansWrapperBuilder.build();
	}

	public static ObjectWrapper getObjectWrapper() {
		DefaultObjectWrapperBuilder defaultObjectWrapperBuilder =
			new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_23);

		return defaultObjectWrapperBuilder.build();
	}

}