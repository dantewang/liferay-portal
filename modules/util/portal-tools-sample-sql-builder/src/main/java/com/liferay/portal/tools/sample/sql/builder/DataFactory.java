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
public class DataFactory extends BaseDataFactory {

	public DataFactory(InitContext initContext) throws Exception {
		super(initContext);

		_userDataFactory = new UserDataFactory(initContext);

		_journalDataFactory = new JournalDataFactory(
			initContext, _userDataFactory);
		_assetDataFactory = new AssetDataFactory(initContext, _userDataFactory);

		_blogDataFactory = new BlogDataFactory(initContext);
		_counterDataFactory = new CounterDataFactory(initContext);
		_dLDataFactory = new DLDataFactory(initContext, _userDataFactory);
		_dDLDataFactory = new DDLDataFactory(initContext);
		_layoutDataFactory = new LayoutDataFactory(initContext);
		_messageBoardDataFactory = new MessageBoardDataFactory(initContext);
		_portletPreferenceDataFactory = new PortletPreferenceDataFactory(
			initContext);

		_portletPreferenceDataFactory.setAssetDataFactory(_assetDataFactory);

		_resourcePermissionDataFactory = new ResourcePermissionDataFactory(
			initContext);

		_resourcePermissionDataFactory.setUserDataFactory(_userDataFactory);

		_socialActivityDataFactory = new SocialActivityDataFactory(initContext);
		_subscriptionDataFactory = new SubscriptionDataFactory(initContext);
		_wikiDataFactory = new WikiDataFactory(initContext);

		_assetDataFactory.setJournalDataFactory(_journalDataFactory);
		_counterDataFactory.setResourcePermissionDataFactory(
			_resourcePermissionDataFactory);
		_counterDataFactory.setSocialActivityDataFactory(
			_socialActivityDataFactory);
		_dDLDataFactory.setUserDataFactory(_userDataFactory);
		_messageBoardDataFactory.setUserDataFactory(_userDataFactory);

		_dataFactories.put("assetDataFactory", _assetDataFactory);
		_dataFactories.put("blogDataFactory", _blogDataFactory);
		_dataFactories.put("counterDataFactory", _counterDataFactory);
		_dataFactories.put("dDLDataFactory", _dDLDataFactory);
		_dataFactories.put("dLDataFactory", _dLDataFactory);
		_dataFactories.put("journalDataFactory", _journalDataFactory);
		_dataFactories.put("layoutDataFactory", _layoutDataFactory);
		_dataFactories.put("messageBoardDataFactory", _messageBoardDataFactory);
		_dataFactories.put(
			"portletPreferenceDataFactory", _portletPreferenceDataFactory);
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
	private final CounterDataFactory _counterDataFactory;
	private final Map<String, Object> _dataFactories = new HashMap<>();
	private final DDLDataFactory _dDLDataFactory;
	private final DLDataFactory _dLDataFactory;
	private final JournalDataFactory _journalDataFactory;
	private final LayoutDataFactory _layoutDataFactory;
	private final MessageBoardDataFactory _messageBoardDataFactory;
	private final PortletPreferenceDataFactory _portletPreferenceDataFactory;
	private final ResourcePermissionDataFactory _resourcePermissionDataFactory;
	private final SocialActivityDataFactory _socialActivityDataFactory;
	private final SubscriptionDataFactory _subscriptionDataFactory;
	private final UserDataFactory _userDataFactory;
	private final WikiDataFactory _wikiDataFactory;

}