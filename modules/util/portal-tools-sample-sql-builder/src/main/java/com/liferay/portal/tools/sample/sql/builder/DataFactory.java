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

		JournalDataFactory journalDataFactory = new JournalDataFactory(
			dataFactoryContext, userDataFactory);

		AssetDataFactory assetDataFactory = new AssetDataFactory(
			dataFactoryContext, journalDataFactory, userDataFactory);

		CommerceDataFactory commerceDataFactory = new CommerceDataFactory(
			dataFactoryContext, assetDataFactory, userDataFactory);

		ResourcePermissionDataFactory resourcePermissionDataFactory =
			new ResourcePermissionDataFactory(
				dataFactoryContext, commerceDataFactory, userDataFactory);

		SocialActivityDataFactory socialActivityDataFactory =
			new SocialActivityDataFactory(dataFactoryContext);

		_dataFactories.put("assetDataFactory", assetDataFactory);
		_dataFactories.put(
			"blogDataFactory", new BlogDataFactory(dataFactoryContext));
		_dataFactories.put("commerceDataFactory", commerceDataFactory);
		_dataFactories.put(
			"counterDataFactory",
			new CounterDataFactory(
				dataFactoryContext, resourcePermissionDataFactory,
				socialActivityDataFactory));
		_dataFactories.put(
			"dDLDDMDataFactory",
			new DDLDDMDataFactory(dataFactoryContext, userDataFactory));
		_dataFactories.put(
			"dLDataFactory",
			new DLDataFactory(dataFactoryContext, userDataFactory));
		_dataFactories.put("journalDataFactory", journalDataFactory);
		_dataFactories.put(
			"layoutDataFactory", new LayoutDataFactory(dataFactoryContext));
		_dataFactories.put(
			"messageBoardDataFactory",
			new MessageBoardDataFactory(dataFactoryContext, userDataFactory));
		_dataFactories.put(
			"portletPreferenceDataFactory",
			new PortletPreferenceDataFactory(
				dataFactoryContext, assetDataFactory));
		_dataFactories.put(
			"releaseDataFactory", new ReleaseDataFactory(dataFactoryContext));
		_dataFactories.put(
			"resourcePermissionDataFactory", resourcePermissionDataFactory);
		_dataFactories.put(
			"socialActivityDataFactory", socialActivityDataFactory);
		_dataFactories.put(
			"subscriptionDataFactory",
			new SubscriptionDataFactory(dataFactoryContext));
		_dataFactories.put("userDataFactory", userDataFactory);
		_dataFactories.put(
			"wikiDataFactory", new WikiDataFactory(dataFactoryContext));
	}

	public Map<String, Object> getDataFactories() {
		return _dataFactories;
	}

	private final Map<String, Object> _dataFactories = new HashMap<>();

}