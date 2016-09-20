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

package com.liferay.portal.kernel.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dante Wang
 */
public class UnifiedDirectCallFilterResult {

	public UnifiedDirectCallFilterResult(
		HttpServletRequest request, HttpServletResponse response,
		boolean continueChain) {

		this(request, response, null, continueChain);
	}

	public UnifiedDirectCallFilterResult(
		HttpServletRequest request, HttpServletResponse response, Object object,
		boolean continueChain) {

		_request = request;
		_response = response;
		_object = object;
		_continueChain = continueChain;
	}

	public Object getObject() {
		return _object;
	}

	public HttpServletRequest getRequest() {
		return _request;
	}

	public HttpServletResponse getResponse() {
		return _response;
	}

	public boolean isContinue() {
		return _continueChain;
	}

	private final boolean _continueChain;
	private final Object _object;
	private final HttpServletRequest _request;
	private final HttpServletResponse _response;

}