import {Page} from '@playwright/test';

import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {IndividualIdentity, RangeSelectors} from '../types';

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
export async function createBlogsEventsForEveryDayByRangeSelector({
	apiHelpers,
	assetId,
	assetTitle,
	channel,
	individualIdentities,
	page,
	rangeSelector,
}: {
	apiHelpers: ApiHelpers;
	assetId: string;
	assetTitle: string;
	channel: {
		id: string;
	};
	individualIdentities: IndividualIdentity[];
	page: Page;
	rangeSelector: RangeSelectors;
}) {
	const canonicalUrl = 'https://www.liferay.com';
	const date = new Date();

	let blogEvents = [];

	const pageEvents = individualIdentities.map((identity) => ({
		canonicalUrl,
		channelId: channel.id,
		eventDate: date.toISOString(),
		title: 'DXP Page 1',
		userId: identity.id,
		views: Number(rangeSelector),
	}));

	const sessions = individualIdentities.map((identity) => ({
		channelId: channel.id,
		id: identity.id,
		sessionEnd: date.toISOString(),
		sessionStart: date.toISOString(),
		userId: identity.id,
	}));

	for (let i = 0; i <= Number(rangeSelector); i++) {
		blogEvents = [
			...blogEvents,
			...individualIdentities.map((identity) => ({
				assetId,
				assetTitle,
				canonicalUrl,
				channelId: channel.id,
				clicks: 1,
				comments: 2,
				eventDate: date.toISOString(),
				ratings: 1,
				ratingsScore: 1,
				readTime: 1,
				sessions: 1,
				userId: identity.id,
				views: 1,
			})),
		];

		date.setDate(date.getDate() - 1);
	}

	await apiHelpers.jsonWebServicesOSBAsah.createBlogsDaily(blogEvents);

	await apiHelpers.jsonWebServicesOSBAsah.createSessions(sessions);

	await apiHelpers.jsonWebServicesOSBAsah.createPagesDaily(pageEvents);

	await page.waitForTimeout(1000);
}
