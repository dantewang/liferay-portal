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
public class TemplateContextFactory {

	public static Map<String, Object> createContext(
			DataFactoryContext dataFactoryContext)
		throws Exception {

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

		Map<String, Object> context = new HashMap<>();

		context.put("assetDataFactory", assetDataFactory);
		context.put("blogDataFactory", new BlogDataFactory(dataFactoryContext));
		context.put("commerceDataFactory", commerceDataFactory);
		context.put(
			"counterDataFactory",
			new CounterDataFactory(
				dataFactoryContext, resourcePermissionDataFactory,
				socialActivityDataFactory));
		context.put(
			"dDLDDMDataFactory",
			new DDLDDMDataFactory(dataFactoryContext, userDataFactory));
		context.put(
			"dLDataFactory",
			new DLDataFactory(dataFactoryContext, userDataFactory));
		context.put("journalDataFactory", journalDataFactory);
		context.put(
			"layoutDataFactory", new LayoutDataFactory(dataFactoryContext));
		context.put(
			"messageBoardDataFactory",
			new MessageBoardDataFactory(dataFactoryContext, userDataFactory));
		context.put(
			"portletPreferenceDataFactory",
			new PortletPreferenceDataFactory(
				dataFactoryContext, assetDataFactory));
		context.put(
			"releaseDataFactory", new ReleaseDataFactory(dataFactoryContext));
		context.put(
			"resourcePermissionDataFactory", resourcePermissionDataFactory);
		context.put("socialActivityDataFactory", socialActivityDataFactory);
		context.put(
			"subscriptionDataFactory",
			new SubscriptionDataFactory(dataFactoryContext));
		context.put("userDataFactory", userDataFactory);
		context.put("wikiDataFactory", new WikiDataFactory(dataFactoryContext));

		context.put("dataFactoryContext", dataFactoryContext);

		return context;
	}

}