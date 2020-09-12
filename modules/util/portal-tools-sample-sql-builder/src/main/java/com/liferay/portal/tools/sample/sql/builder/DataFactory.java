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

/**
 * @author Brian Wing Shun Chan
 */
public class DataFactory extends BaseDDMDataFactory {

	public BaseDataFactory getDataFactoryInstance(String name) {
		if (name.equals("assetDataFactory")) {
			return AssetDataFactory.getInstance();
		}
		else if (name.equals("blogDataFactory")) {
			return BlogDataFactory.getInstance();
		}
		else if (name.equals("commerceDataFactory")) {
			return CommerceDataFactory.getInstance();
		}
		else if (name.equals("counterDataFactory")) {
			return CounterDataFactory.getInstance();
		}
		else if (name.equals("ddlDDMDataFactory")) {
			return DDLDDMDataFactory.getInstance();
		}
		else if (name.equals("dlDataFactory")) {
			return DLDataFactory.getInstance();
		}
		else if (name.equals("fragmentDataFactory")) {
			return FragmentDataFactory.getInstance();
		}
		else if (name.equals("journalDataFactory")) {
			return JournalDataFactory.getInstance();
		}
		else if (name.equals("layoutDataFactory")) {
			return LayoutDataFactory.getInstance();
		}
		else if (name.equals("messageBoardDataFactory")) {
			return MessageBoardDataFactory.getInstance();
		}
		else if (name.equals("portletPreferenceDataFactory")) {
			return PortletPreferenceDataFactory.getInstance();
		}
		else if (name.equals("releaseDataFactory")) {
			return ReleaseDataFactory.getInstance();
		}
		else if (name.equals("socialActivityDataFactory")) {
			return SocialActivityDataFactory.getInstance();
		}
		else if (name.equals("subscriptionDataFactory")) {
			return SubscriptionDataFactory.getInstance();
		}
		else if (name.equals("userDataFactory")) {
			return UserDataFactory.getInstance();
		}
		else if (name.equals("wikiDataFactory")) {
			return WikiDataFactory.getInstance();
		}

		return ClassNameDataFactory.getInstance();
	}

}