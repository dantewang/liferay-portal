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

import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.message.boards.model.MBThreadModel;
import com.liferay.subscription.constants.SubscriptionConstants;
import com.liferay.subscription.model.SubscriptionModel;
import com.liferay.subscription.model.impl.SubscriptionModelImpl;
import com.liferay.wiki.model.WikiPageModel;

import java.util.Date;

/**
 * @author Lily Chi
 */
public class SubscriptionDataFactory extends BaseDataFactory {

	public static SubscriptionDataFactory getInstance() {
		return _subscriptionDataFactory;
	}

	public SubscriptionModel newSubscriptionModel(
		BlogsEntryModel blogsEntryModel, long classNameId) {

		return _newSubscriptionModel(classNameId, blogsEntryModel.getEntryId());
	}

	public SubscriptionModel newSubscriptionModel(
		MBThreadModel mBThreadModel, long classNameId) {

		return _newSubscriptionModel(classNameId, mBThreadModel.getThreadId());
	}

	public SubscriptionModel newSubscriptionModel(
		WikiPageModel wikiPageModel, long classNameId) {

		return _newSubscriptionModel(
			classNameId, wikiPageModel.getResourcePrimKey());
	}

	private SubscriptionDataFactory() {
	}

	private SubscriptionModel _newSubscriptionModel(
		long classNameId, long classPK) {

		SubscriptionModel subscriptionModel = new SubscriptionModelImpl();

		// PK fields

		subscriptionModel.setSubscriptionId(counter.get());

		// Audit fields

		subscriptionModel.setCompanyId(COMPANY_ID);
		subscriptionModel.setUserId(SAMPLE_USER_ID);
		subscriptionModel.setUserName(SAMPLE_USER_NAME);
		subscriptionModel.setCreateDate(new Date());
		subscriptionModel.setModifiedDate(new Date());

		// Other fields

		subscriptionModel.setClassNameId(classNameId);
		subscriptionModel.setClassPK(classPK);
		subscriptionModel.setFrequency(SubscriptionConstants.FREQUENCY_INSTANT);

		return subscriptionModel;
	}

	private static SubscriptionDataFactory _subscriptionDataFactory =
		new SubscriptionDataFactory();

}