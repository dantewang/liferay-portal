/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginAnalyticsCloudTest} from '../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../fixtures/loginTest';
import getRandomString from '../../utils/getRandomString';
import {syncAnalyticsCloud} from '../analytics-settings-web/utils/analytics-settings';
import {blogsPagesTest} from '../blogs-web/fixtures/blogsPagesTest';
import {contentDashboardPagesTest} from '../content-dashboard-web/fixtures/contentDashboardPagesTest';
import {
	Individual,
	createIndividuals,
	generateIndividual,
} from '../osb-faro-web/utils/individuals';
import {IndividualIdentity, RangeSelectors} from './types';
import {createBlogsEventsForEveryDayByRangeSelector} from './utils/events';

const test = mergeTests(
	apiHelpersTest,
	blogsPagesTest,
	contentDashboardPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	featureFlagsTest({
		'LPD-28830': true,
	}),
	loginAnalyticsCloudTest(),
	loginTest()
);

const assetTitle = getRandomString();

let assetId: string = null;
let channel = null;
let individualIdentities: IndividualIdentity[] | null = null;
let individuals: Individual[] | null = null;

test.beforeEach(async ({apiHelpers, page, site}) => {
	const channelName = 'My Property - ' + getRandomString();

	const result = await syncAnalyticsCloud({
		apiHelpers,
		channelName,
		page,
		siteName: site.name,
	});

	channel = result.channel;

	await test.step('Create Individuals', async () => {
		individuals = [
			generateIndividual({
				name: 'userA',
			}),
		];

		await createIndividuals({
			apiHelpers,
			individuals,
		});

		individualIdentities = individuals.map(({id}) => ({
			createDate: new Date().toISOString(),
			id,
			individualId: id,
		}));

		const anonymousIndividual = {
			createDate: new Date().toISOString(),
			id: getRandomString(),
			individualId: null,
		};

		individualIdentities.push(anonymousIndividual);

		await apiHelpers.jsonWebServicesOSBAsah.createIdentities(
			individualIdentities
		);
	});

	await test.step('Create a Blog', async () => {
		const {id} = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: assetTitle,
		});

		assetId = id;

		await page.waitForTimeout(1000);
	});
});

test('User is able to see data plotted on Interactions By Page Chart by all, anonymous and known individuals', async ({
	apiHelpers,
	page,
}) => {
	await test.step('Generate Blog events for known / anonymous individuals', async () => {
		await createBlogsEventsForEveryDayByRangeSelector({
			apiHelpers,
			assetId,
			assetTitle,
			channel,
			individualIdentities,
			page,
			rangeSelector: RangeSelectors.Last30Days,
		});
	});
});
