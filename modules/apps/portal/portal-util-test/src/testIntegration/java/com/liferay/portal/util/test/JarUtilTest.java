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

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.JarUtil;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class JarUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDownloadMySQLJarFromPortalProperties() throws Exception {
		DB db = DBManagerUtil.getDB();

		Assume.assumeTrue(
			"Database type is not MySQL", db.getDBType() == DBType.MYSQL);

		_testDownloadMySQLJarFromPortalProperties(
			PropsUtil.get(
				PropsKeys.SETUP_DATABASE_JAR_SHA1,
				new Filter(PropsValues.JDBC_DEFAULT_DRIVER_CLASS_NAME)));

		_testDownloadMySQLJarFromPortalProperties(_FAKE_SHA1);
	}

	private void _testDownloadMySQLJarFromPortalProperties(String sha1)
		throws Exception {

		URL url = new URL(
			PropsUtil.get(
				PropsKeys.SETUP_DATABASE_JAR_URL,
				new Filter(PropsValues.JDBC_DEFAULT_DRIVER_CLASS_NAME)));

		File tempFile = FileUtil.createTempFile();

		Path tempFilePath = tempFile.toPath();

		try {
			JarUtil.downloadAndInstallJar(url, tempFilePath, sha1);

			if (sha1.equals(_FAKE_SHA1)) {
				Assert.fail(
					"Download should fail when invalid sha1 is provided");
			}
		}
		catch (Exception exception) {
			if (!sha1.equals(_FAKE_SHA1)) {
				throw exception;
			}

			String message = exception.getMessage();

			Assert.assertTrue(
				message, message.contains("due to integrity check failure"));
		}
		finally {
			Files.delete(tempFilePath);
		}
	}

	private static final String _FAKE_SHA1 = "FAKE_SHA1";

}