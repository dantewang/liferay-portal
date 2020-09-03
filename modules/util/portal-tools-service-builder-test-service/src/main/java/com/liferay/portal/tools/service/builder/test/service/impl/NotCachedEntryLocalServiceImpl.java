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

package com.liferay.portal.tools.service.builder.test.service.impl;

import com.liferay.portal.tools.service.builder.test.model.NotCachedEntry;
import com.liferay.portal.tools.service.builder.test.model.impl.NotCachedEntryImpl;
import com.liferay.portal.tools.service.builder.test.service.base.NotCachedEntryLocalServiceBaseImpl;

import java.util.List;

/**
 * The implementation of the not cached entry local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the <code>com.liferay.portal.tools.service.builder.test.service.NotCachedEntryLocalService</code> interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see NotCachedEntryLocalServiceBaseImpl
 */
public class NotCachedEntryLocalServiceImpl
	extends NotCachedEntryLocalServiceBaseImpl {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Use <code>com.liferay.portal.tools.service.builder.test.service.NotCachedEntryLocalService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>com.liferay.portal.tools.service.builder.test.service.NotCachedEntryLocalServiceUtil</code>.
	 */
	public NotCachedEntry addNotCachedEntry(long column1, long column2) {
		long notCachedEntryId = counterLocalService.increment();

		NotCachedEntry notCachedEntry = createNotCachedEntry(notCachedEntryId);

		notCachedEntry.setColumn1(column1);
		notCachedEntry.setColumn2(column2);

		return addNotCachedEntry(notCachedEntry);
	}

	public Class<?> getEntityCacheClass() {
		return NotCachedEntryImpl.class;
	}

	public List<NotCachedEntry> getNotCachedEntriesByColumns(
		long column1, long column2) {

		return notCachedEntryPersistence.findByC_C(column1, column2);
	}

}