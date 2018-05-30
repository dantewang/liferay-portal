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

import com.liferay.portal.kernel.servlet.TryFinallyFilter;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.UnsyncPrintWriterPool;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portlet.internal.PortletAsyncContextImpl;
import com.liferay.portlet.ResourceRequestImpl;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

			PortletAsyncContextImpl portletAsyncContextImpl =
				(PortletAsyncContextImpl)
					resourceRequestImpl.getPortletAsyncContext();

			AsyncListener unsyncPrintWriterPoolCleanUpAsyncListener =
				new AsyncListener() {

				@Override
				public void onComplete(AsyncEvent asyncEvent)
					throws IOException {

					UnsyncPrintWriterPool.cleanUp();

					portletAsyncContextImpl.setUnsyncPrintWriterPoolListener(
						null);
				}

				@Override
				public void onTimeout(AsyncEvent asyncEvent)
					throws IOException {
				}

				@Override
				public void onError(AsyncEvent asyncEvent)
					throws IOException {
				}

				@Override
				public void onStartAsync(AsyncEvent asyncEvent)
					throws IOException {
				}
			};

			portletAsyncContextImpl.setUnsyncPrintWriterPoolListener(
				unsyncPrintWriterPoolCleanUpAsyncListener);

			AsyncContext asyncContext = request.getAsyncContext();

			// TODO: Memory leak of the ThreadLocal
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