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

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.model.ReleaseModel;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.model.impl.ReleaseModelImpl;
import com.liferay.portal.upgrade.PortalUpgradeProcess;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class ReleaseDataFactory extends BaseDataFactory {

	public static ReleaseDataFactory getInstance() {
		return _releaseDataFactory;
	}

	public List<ReleaseModel> newReleaseModels() throws IOException {
		List<ReleaseModel> releases = new ArrayList<>();

		Version latestSchemaVersion =
			PortalUpgradeProcess.getLatestSchemaVersion();

		releases.add(
			_newReleaseModel(
				ReleaseConstants.DEFAULT_ID,
				ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME,
				latestSchemaVersion.toString(), ReleaseInfo.getBuildNumber(),
				false, ReleaseConstants.TEST_STRING));

		try (InputStream is = DataFactory.class.getResourceAsStream(
				"dependencies/releases.txt");
			Reader reader = new InputStreamReader(is);
			UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(reader)) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				String[] parts = StringUtil.split(line, CharPool.COLON);

				if (parts.length > 0) {
					String servletContextName = parts[0];
					String schemaVersion = parts[1];

					releases.add(
						_newReleaseModel(
							counter.get(), servletContextName, schemaVersion, 0,
							true, null));
				}
			}
		}

		return releases;
	}

	private ReleaseDataFactory() {
	}

	private ReleaseModelImpl _newReleaseModel(
			long releaseId, String servletContextName, String schemaVersion,
			int buildNumber, boolean verified, String testString)
		throws IOException {

		ReleaseModelImpl releaseModelImpl = new ReleaseModelImpl();

		// PK fields

		releaseModelImpl.setReleaseId(releaseId);

		// Audit fields

		releaseModelImpl.setCreateDate(new Date());
		releaseModelImpl.setModifiedDate(new Date());

		// Other fields

		releaseModelImpl.setServletContextName(servletContextName);
		releaseModelImpl.setSchemaVersion(schemaVersion);
		releaseModelImpl.setBuildNumber(buildNumber);
		releaseModelImpl.setBuildDate(new Date());
		releaseModelImpl.setVerified(verified);
		releaseModelImpl.setTestString(testString);

		return releaseModelImpl;
	}

	private static ReleaseDataFactory _releaseDataFactory =
		new ReleaseDataFactory();

}