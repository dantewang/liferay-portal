/*******************************************************************************
 * Copyright (c) 2014 Raymond Augé and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 ******************************************************************************/

package com.liferay.portal.osgi.web.http.servlet.internal.customizer;

import com.liferay.portal.osgi.web.http.servlet.internal.HttpServiceRuntimeController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ContextController;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Raymond Augé
 */
public abstract class BaseServiceTrackerCustomizer<S, T>
	implements ServiceTrackerCustomizer<S, T> {

	public BaseServiceTrackerCustomizer(
		BundleContext bundleContext, ContextController contextController,
		HttpServiceRuntimeController httpServiceRuntimeController) {

		this.bundleContext = bundleContext;
		this.contextController = contextController;
		this.httpServiceRuntimeController = httpServiceRuntimeController;
	}

	protected BundleContext bundleContext;
	protected ContextController contextController;
	protected HttpServiceRuntimeController httpServiceRuntimeController;

}