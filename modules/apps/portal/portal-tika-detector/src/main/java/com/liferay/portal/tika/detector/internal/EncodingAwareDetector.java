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

package com.liferay.portal.tika.detector.internal;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.Charset;

import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.txt.UniversalEncodingDetector;

/**
 * @author Shuyang Zhou
 */
public class EncodingAwareDetector implements Detector {

	@Override
	public MediaType detect(InputStream inputStream, Metadata metadata)
		throws IOException {

		UniversalEncodingDetector universalEncodingDetector =
			new UniversalEncodingDetector();

		Charset charset = universalEncodingDetector.detect(
			inputStream, metadata);

		if (charset == null) {
			return MediaType.OCTET_STREAM;
		}

		String charsetName = charset.name();

		if (!charsetName.equals("")) {
			metadata.set("Content-Encoding", charsetName);
			metadata.set("Content-Type", "text/plain; charset=" + charsetName);
		}

		return MediaType.TEXT_PLAIN;
	}

}