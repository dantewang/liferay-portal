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

package com.liferay.portal.cache.mvcc;

import com.liferay.portal.model.MVCCModel;

import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.DefaultElementValueComparator;

/**
 * @author Tina Tian
 */
public class MVCCCacheElementValueComparator
	extends DefaultElementValueComparator {

	public MVCCCacheElementValueComparator(
		CacheConfiguration cacheConfiguration) {

		super(cacheConfiguration);
	}

	public boolean equals(Element element1, Element element2) {
		Object value1 = element1.getObjectValue();
		Object value2 = element2.getObjectValue();

		if ((value1 instanceof MVCCModel) && (value2 instanceof MVCCModel)) {
			MVCCModel mvccModel1 = (MVCCModel)value1;
			MVCCModel mvccModel2 = (MVCCModel)value2;

			return mvccModel1.getMvccVersion() == mvccModel2.getMvccVersion();
		}

		return super.equals(element1, element2);
	}

}