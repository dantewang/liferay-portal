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

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.NotCachedEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing NotCachedEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class NotCachedEntryCacheModel
	implements CacheModel<NotCachedEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NotCachedEntryCacheModel)) {
			return false;
		}

		NotCachedEntryCacheModel notCachedEntryCacheModel =
			(NotCachedEntryCacheModel)object;

		if (nestedSetsTreeEntryId ==
				notCachedEntryCacheModel.nestedSetsTreeEntryId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, nestedSetsTreeEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(7);

		sb.append("{nestedSetsTreeEntryId=");
		sb.append(nestedSetsTreeEntryId);
		sb.append(", column1=");
		sb.append(column1);
		sb.append(", column2=");
		sb.append(column2);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public NotCachedEntry toEntityModel() {
		NotCachedEntryImpl notCachedEntryImpl = new NotCachedEntryImpl();

		notCachedEntryImpl.setNestedSetsTreeEntryId(nestedSetsTreeEntryId);
		notCachedEntryImpl.setColumn1(column1);
		notCachedEntryImpl.setColumn2(column2);

		notCachedEntryImpl.resetOriginalValues();

		return notCachedEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		nestedSetsTreeEntryId = objectInput.readLong();

		column1 = objectInput.readLong();

		column2 = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(nestedSetsTreeEntryId);

		objectOutput.writeLong(column1);

		objectOutput.writeLong(column2);
	}

	public long nestedSetsTreeEntryId;
	public long column1;
	public long column2;

}