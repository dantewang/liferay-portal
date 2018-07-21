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

package com.liferay.portal.servlet.filters.unsyncprintwriterpool;

import com.liferay.portal.kernel.portlet.LiferayPortletAsyncContext;
import com.liferay.portal.kernel.servlet.TryFinallyFilter;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.UnsyncPrintWriterPool;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portlet.internal.ResourceRequestImpl;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Shuyang Zhou
 */
public class UnsyncPrintWriterPoolFilter
	extends BasePortalFilter implements TryFinallyFilter {

	@Override
	public void doFilterFinally(
		HttpServletRequest request, HttpServletResponse response,
		Object object) {

		if (!request.isAsyncSupported() || !request.isAsyncStarted()) {
			UnsyncPrintWriterPool.cleanUp();
		}
		else {
			ResourceRequestImpl resourceRequestImpl =
				(ResourceRequestImpl)request.getAttribute(
					JavaConstants.JAVAX_PORTLET_REQUEST);

			LiferayPortletAsyncContext portletAsyncContext =
				(LiferayPortletAsyncContext)
					resourceRequestImpl.getPortletAsyncContext();

			AsyncListener unsyncPrintWriterPoolCleanUpAsyncListener =
				new AsyncListener() {

					@Override
					public void onComplete(AsyncEvent asyncEvent)
						throws IOException {

						_cleanUp();
					}

					@Override
					public void onError(AsyncEvent asyncEvent)
						throws IOException {

						_cleanUp();
					}

					@Override
					public void onStartAsync(AsyncEvent asyncEvent)
						throws IOException {
					}

					@Override
					public void onTimeout(AsyncEvent asyncEvent)
						throws IOException {

						_cleanUp();
					}

					private void _cleanUp() {
						UnsyncPrintWriterPool.cleanUp();

						portletAsyncContext.removeListener(this);
					}

				};

			portletAsyncContext.addListener(
				unsyncPrintWriterPoolCleanUpAsyncListener);

			AsyncContext asyncContext = request.getAsyncContext();

			asyncContext.addListener(unsyncPrintWriterPoolCleanUpAsyncListener);
		}
	}

	@Override
	public Object doFilterTry(
		HttpServletRequest request, HttpServletResponse response) {

		UnsyncPrintWriterPool.setEnabled(true);

		return null;
	}

}