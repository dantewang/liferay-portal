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

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.model.ReleaseModel;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.impl.ReleaseModelImpl;

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

	public List<ReleaseModel> newReleaseModels() throws IOException {
		List<ReleaseModel> releases = new ArrayList<>();

		try (InputStream is = ReleaseDataFactory.class.getResourceAsStream(
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
						_newReleaseModel(servletContextName, schemaVersion));
				}
			}
		}

		return releases;
	}

	private ReleaseModelImpl _newReleaseModel(
			String servletContextName, String schemaVersion)
		throws IOException {

		ReleaseModelImpl releaseModelImpl = new ReleaseModelImpl();

		releaseModelImpl.setReleaseId(counter.get());

		releaseModelImpl.setCreateDate(new Date());
		releaseModelImpl.setModifiedDate(new Date());
		releaseModelImpl.setServletContextName(servletContextName);
		releaseModelImpl.setSchemaVersion(schemaVersion);
		releaseModelImpl.setBuildDate(new Date());
		releaseModelImpl.setVerified(true);

		return releaseModelImpl;
	}

}