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

package com.liferay.portal.tools.service.builder.test.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by SOAP remote services.
 *
 * @author Brian Wing Shun Chan
 * @deprecated As of Athanasius (7.3.x), with no direct replacement
 * @generated
 */
@Deprecated
public class NotCachedEntrySoap implements Serializable {

	public static NotCachedEntrySoap toSoapModel(NotCachedEntry model) {
		NotCachedEntrySoap soapModel = new NotCachedEntrySoap();

		soapModel.setNestedSetsTreeEntryId(model.getNestedSetsTreeEntryId());
		soapModel.setColumn1(model.getColumn1());
		soapModel.setColumn2(model.getColumn2());

		return soapModel;
	}

	public static NotCachedEntrySoap[] toSoapModels(NotCachedEntry[] models) {
		NotCachedEntrySoap[] soapModels = new NotCachedEntrySoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static NotCachedEntrySoap[][] toSoapModels(
		NotCachedEntry[][] models) {

		NotCachedEntrySoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels =
				new NotCachedEntrySoap[models.length][models[0].length];
		}
		else {
			soapModels = new NotCachedEntrySoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static NotCachedEntrySoap[] toSoapModels(
		List<NotCachedEntry> models) {

		List<NotCachedEntrySoap> soapModels = new ArrayList<NotCachedEntrySoap>(
			models.size());

		for (NotCachedEntry model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new NotCachedEntrySoap[soapModels.size()]);
	}

	public NotCachedEntrySoap() {
	}

	public long getPrimaryKey() {
		return _nestedSetsTreeEntryId;
	}

	public void setPrimaryKey(long pk) {
		setNestedSetsTreeEntryId(pk);
	}

	public long getNestedSetsTreeEntryId() {
		return _nestedSetsTreeEntryId;
	}

	public void setNestedSetsTreeEntryId(long nestedSetsTreeEntryId) {
		_nestedSetsTreeEntryId = nestedSetsTreeEntryId;
	}

	public long getColumn1() {
		return _column1;
	}

	public void setColumn1(long column1) {
		_column1 = column1;
	}

	public long getColumn2() {
		return _column2;
	}

	public void setColumn2(long column2) {
		_column2 = column2;
	}

	private long _nestedSetsTreeEntryId;
	private long _column1;
	private long _column2;

}