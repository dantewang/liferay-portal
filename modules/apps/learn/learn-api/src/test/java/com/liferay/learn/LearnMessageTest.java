/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.learn;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Dante Wang
 */
public class LearnMessageTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testLearnMessage() {
		String url = "https://example.com/path";

		String message1 = "Hello";
		String message2 = "こんにちは";

		JSONObject jsonObject = JSONUtil.put(
			"general",
			JSONUtil.put(
				"en_US",
				JSONUtil.put(
					"message", "Hello"
				).put(
					"url", url
				)
			).put(
				"ja_JP", JSONUtil.put("message", "こんにちは")
			));

		LearnMessage learnMessage1 = new LearnMessage(
			jsonObject, "general", "en_US");

		Assert.assertEquals(message1, learnMessage1.getMessage());
		Assert.assertEquals(url, learnMessage1.getURL());

		LearnMessage learnMessage2 = new LearnMessage(
			jsonObject, "general", "ja_JP");

		Assert.assertEquals(message2, learnMessage2.getMessage());
		Assert.assertEquals(url, learnMessage2.getURL());

		LearnMessage learnMessage3 = new LearnMessage(
			jsonObject, "general", "zh_CN");

		Assert.assertEquals(message1, learnMessage3.getMessage());
		Assert.assertEquals(url, learnMessage3.getURL());
	}

}