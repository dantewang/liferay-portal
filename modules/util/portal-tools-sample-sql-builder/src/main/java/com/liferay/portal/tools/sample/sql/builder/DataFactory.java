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
		_userDataFactory = new UserDataFactory(dataFactoryContext);
		_blogDataFactory = new BlogDataFactory(dataFactoryContext);
		_layoutDataFactory = new LayoutDataFactory(dataFactoryContext);
		_releaseDataFactory = new ReleaseDataFactory(dataFactoryContext);
		_socialActivityDataFactory = new SocialActivityDataFactory(
			dataFactoryContext);
		_subscriptionDataFactory = new SubscriptionDataFactory(
			dataFactoryContext);
		_wikiDataFactory = new WikiDataFactory(dataFactoryContext);

		_dLDataFactory = new DLDataFactory(
			dataFactoryContext, _userDataFactory);

		_dDLDDMDataFactory = new DDLDDMDataFactory(
			dataFactoryContext, _userDataFactory);

		_messageBoardDataFactory = new MessageBoardDataFactory(
			dataFactoryContext, _userDataFactory);

		_journalDataFactory = new JournalDataFactory(
			dataFactoryContext, _userDataFactory);

		_assetDataFactory = new AssetDataFactory(
			dataFactoryContext, _journalDataFactory, _userDataFactory);

		_portletPreferenceDataFactory = new PortletPreferenceDataFactory(
			dataFactoryContext, _assetDataFactory);

		_commerceDataFactory = new CommerceDataFactory(
			dataFactoryContext, _assetDataFactory, _userDataFactory);

		_resourcePermissionDataFactory = new ResourcePermissionDataFactory(
			dataFactoryContext, _commerceDataFactory, _userDataFactory);

		_counterDataFactory = new CounterDataFactory(
			dataFactoryContext, _resourcePermissionDataFactory,
			_socialActivityDataFactory);

		_dataFactories.put("assetDataFactory", _assetDataFactory);
		_dataFactories.put("blogDataFactory", _blogDataFactory);
		_dataFactories.put("commerceDataFactory", _commerceDataFactory);
		_dataFactories.put("counterDataFactory", _counterDataFactory);
		_dataFactories.put("dDLDDMDataFactory", _dDLDDMDataFactory);
		_dataFactories.put("dLDataFactory", _dLDataFactory);
		_dataFactories.put("journalDataFactory", _journalDataFactory);
		_dataFactories.put("layoutDataFactory", _layoutDataFactory);
		_dataFactories.put("messageBoardDataFactory", _messageBoardDataFactory);
		_dataFactories.put(
			"portletPreferenceDataFactory", _portletPreferenceDataFactory);
		_dataFactories.put("releaseDataFactory", _releaseDataFactory);
		_dataFactories.put(
			"resourcePermissionDataFactory", _resourcePermissionDataFactory);
		_dataFactories.put(
			"socialActivityDataFactory", _socialActivityDataFactory);
		_dataFactories.put("subscriptionDataFactory", _subscriptionDataFactory);
		_dataFactories.put("userDataFactory", _userDataFactory);
		_dataFactories.put("wikiDataFactory", _wikiDataFactory);
	}

	public Map<String, Object> getDataFactories() {
		return _dataFactories;
	}

	private final AssetDataFactory _assetDataFactory;
	private final BlogDataFactory _blogDataFactory;
	private final CommerceDataFactory _commerceDataFactory;
	private final CounterDataFactory _counterDataFactory;
	private final Map<String, Object> _dataFactories = new HashMap<>();
	private final DDLDDMDataFactory _dDLDDMDataFactory;
	private final DLDataFactory _dLDataFactory;
	private final JournalDataFactory _journalDataFactory;
	private final LayoutDataFactory _layoutDataFactory;
	private final MessageBoardDataFactory _messageBoardDataFactory;
	private final PortletPreferenceDataFactory _portletPreferenceDataFactory;
	private final ReleaseDataFactory _releaseDataFactory;
	private final ResourcePermissionDataFactory _resourcePermissionDataFactory;
	private final SocialActivityDataFactory _socialActivityDataFactory;
	private final SubscriptionDataFactory _subscriptionDataFactory;
	private final UserDataFactory _userDataFactory;
	private final WikiDataFactory _wikiDataFactory;

}