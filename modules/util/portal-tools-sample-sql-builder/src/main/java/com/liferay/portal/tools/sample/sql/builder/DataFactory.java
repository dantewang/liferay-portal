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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class DataFactory {

	public DataFactory(DataFactoryContext dataFactoryContext) throws Exception {
		UserDataFactory userDataFactory = new UserDataFactory(
			dataFactoryContext);
		BlogDataFactory blogDataFactory = new BlogDataFactory(
			dataFactoryContext);
		LayoutDataFactory layoutDataFactory = new LayoutDataFactory(
			dataFactoryContext);
		ReleaseDataFactory releaseDataFactory = new ReleaseDataFactory(
			dataFactoryContext);
		SocialActivityDataFactory socialActivityDataFactory =
			new SocialActivityDataFactory(dataFactoryContext);
		SubscriptionDataFactory subscriptionDataFactory =
			new SubscriptionDataFactory(dataFactoryContext);
		WikiDataFactory wikiDataFactory = new WikiDataFactory(
			dataFactoryContext);

		DLDataFactory dLDataFactory = new DLDataFactory(
			dataFactoryContext, userDataFactory);

		DDLDDMDataFactory dDLDDMDataFactory = new DDLDDMDataFactory(
			dataFactoryContext, userDataFactory);

		MessageBoardDataFactory messageBoardDataFactory =
			new MessageBoardDataFactory(dataFactoryContext, userDataFactory);

		JournalDataFactory journalDataFactory = new JournalDataFactory(
			dataFactoryContext, userDataFactory);

		AssetDataFactory assetDataFactory = new AssetDataFactory(
			dataFactoryContext, journalDataFactory, userDataFactory);

		PortletPreferenceDataFactory portletPreferenceDataFactory =
			new PortletPreferenceDataFactory(
				dataFactoryContext, assetDataFactory);

		CommerceDataFactory commerceDataFactory = new CommerceDataFactory(
			dataFactoryContext, assetDataFactory, userDataFactory);

		ResourcePermissionDataFactory resourcePermissionDataFactory =
			new ResourcePermissionDataFactory(
				dataFactoryContext, commerceDataFactory, userDataFactory);

		CounterDataFactory counterDataFactory = new CounterDataFactory(
			dataFactoryContext, resourcePermissionDataFactory,
			socialActivityDataFactory);

		_dataFactories.put("assetDataFactory", assetDataFactory);
		_dataFactories.put("blogDataFactory", blogDataFactory);
		_dataFactories.put("commerceDataFactory", commerceDataFactory);
		_dataFactories.put("counterDataFactory", counterDataFactory);
		_dataFactories.put("dDLDDMDataFactory", dDLDDMDataFactory);
		_dataFactories.put("dLDataFactory", dLDataFactory);
		_dataFactories.put("journalDataFactory", journalDataFactory);
		_dataFactories.put("layoutDataFactory", layoutDataFactory);
		_dataFactories.put("messageBoardDataFactory", messageBoardDataFactory);
		_dataFactories.put(
			"portletPreferenceDataFactory", portletPreferenceDataFactory);
		_dataFactories.put("releaseDataFactory", releaseDataFactory);
		_dataFactories.put(
			"resourcePermissionDataFactory", resourcePermissionDataFactory);
		_dataFactories.put(
			"socialActivityDataFactory", socialActivityDataFactory);
		_dataFactories.put("subscriptionDataFactory", subscriptionDataFactory);
		_dataFactories.put("userDataFactory", userDataFactory);
		_dataFactories.put("wikiDataFactory", wikiDataFactory);
	}

	public Map<String, Object> getDataFactories() {
		return _dataFactories;
	}

	private final Map<String, Object> _dataFactories = new HashMap<>();

}