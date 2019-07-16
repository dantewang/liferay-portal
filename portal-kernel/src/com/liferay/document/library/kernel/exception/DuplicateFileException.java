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

package com.liferay.document.library.kernel.exception;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class DuplicateFileException extends PortalException {

	public DuplicateFileException() {
	}

	public DuplicateFileException(
		long companyId, long repositoryId, String fileName) {

		super(
			StringBundler.concat(
				"{companyId=", companyId, ", repositoryId=", repositoryId,
				", fileName=", fileName, "}"));
	}

	public DuplicateFileException(
		long companyId, long repositoryId, String fileName, String version) {

		super(
			StringBundler.concat(
				"{companyId=", companyId, ", repositoryId=", repositoryId,
				", fileName=", fileName, ", version=", version, "}"));
	}

	public DuplicateFileException(
		long companyId, long repositoryId, String fileName, String version,
		Throwable cause) {

		super(
			StringBundler.concat(
				"{companyId=", companyId, ", repositoryId=", repositoryId,
				", fileName=", fileName, ", version=", version, ", cause=",
				cause, "}"),
			cause);
	}

	public DuplicateFileException(
		long companyId, long repositoryId, String fileName, Throwable cause) {

		super(
			StringBundler.concat(
				"{companyId=", companyId, ", repositoryId=", repositoryId,
				", fileName=", fileName, ", cause=", cause, "}"),
			cause);
	}

	public DuplicateFileException(String msg) {
		super(msg);
	}

	public DuplicateFileException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DuplicateFileException(Throwable cause) {
		super(cause);
	}

}