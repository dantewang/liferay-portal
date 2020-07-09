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

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.model.MBThreadModel;
import com.liferay.subscription.model.SubscriptionConstants;
import com.liferay.subscription.model.SubscriptionModel;
import com.liferay.subscription.model.impl.SubscriptionModelImpl;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageModel;

import java.util.Date;

/**
 * @author Lily Chi
 */
public class SubscriptionDataFactory extends BaseDataFactory {

	public SubscriptionModel newSubscriptionModel(
		BlogsEntryModel blogsEntryModel) {

		return newSubscriptionModel(
			getClassNameId(BlogsEntry.class), blogsEntryModel.getEntryId());
	}

	public SubscriptionModel newSubscriptionModel(MBThreadModel mBThreadModel) {
		return newSubscriptionModel(
			getClassNameId(MBThread.class), mBThreadModel.getThreadId());
	}

	public SubscriptionModel newSubscriptionModel(WikiPageModel wikiPageModel) {
		return newSubscriptionModel(
			getClassNameId(WikiPage.class), wikiPageModel.getResourcePrimKey());
	}

	protected SubscriptionModel newSubscriptionModel(
		long classNameId, long classPK) {

		SubscriptionModel subscriptionModel = new SubscriptionModelImpl();

		subscriptionModel.setSubscriptionId(counter.get());
		subscriptionModel.setCompanyId(COMPANY_ID);
		subscriptionModel.setUserId(SAMPLE_USER_ID);
		subscriptionModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		subscriptionModel.setCreateDate(new Date());
		subscriptionModel.setModifiedDate(new Date());
		subscriptionModel.setClassNameId(classNameId);
		subscriptionModel.setClassPK(classPK);
		subscriptionModel.setFrequency(SubscriptionConstants.FREQUENCY_INSTANT);

		return subscriptionModel;
	}

}