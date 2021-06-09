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

package com.liferay.portal.dao.orm.hibernate.event;

import org.hibernate.event.internal.DefaultPreLoadEventListener;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.Type;

/**
 * @author Dante Wang
 */
public class LazyBlobPreLoadEventListener extends DefaultPreLoadEventListener {

	public static final LazyBlobPreLoadEventListener INSTANCE =
		new LazyBlobPreLoadEventListener();

	public void onPreLoad(PreLoadEvent preLoadEvent) {
		Object[] state = preLoadEvent.getState();

		EntityPersister entityPersister = preLoadEvent.getPersister();

		String[] propertyNames = entityPersister.getPropertyNames();

		Type[] propertyTypes = entityPersister.getPropertyTypes();

		for (int i = 0; i < propertyNames.length; i++) {
			if (propertyNames[i].endsWith("BlobModel") &&
				(propertyTypes[i] instanceof OneToOneType)) {

				state[i] = null;
			}
		}

		super.onPreLoad(preLoadEvent);
	}

}