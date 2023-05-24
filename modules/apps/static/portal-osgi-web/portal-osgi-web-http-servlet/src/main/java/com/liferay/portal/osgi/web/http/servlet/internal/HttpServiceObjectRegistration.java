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

/*******************************************************************************
 * Copyright (c) Dec 1, 2014 Liferay, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Liferay, Inc. - initial API and implementation and/or initial
 *                    documentation
 ******************************************************************************/

package com.liferay.portal.osgi.web.http.servlet.internal;

import com.liferay.portal.osgi.web.http.servlet.internal.context.HttpContextHelperFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Dante Wang
 * @author Liferay, Inc.
 */
public class HttpServiceObjectRegistration {

	public HttpServiceObjectRegistration(
		Object serviceKey, ServiceRegistration<?> serviceRegistration,
		HttpContextHelperFactory factory, Bundle bundle) {

		this.serviceKey = serviceKey;
		this.serviceRegistration = serviceRegistration;
		this.factory = factory;
		this.bundle = bundle;
	}

	public final Bundle bundle;
	public final HttpContextHelperFactory factory;
	public final Object serviceKey;
	public final ServiceRegistration<?> serviceRegistration;

}