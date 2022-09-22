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

package com.liferay.layout.internal.list.retriever;

import com.liferay.layout.list.retriever.LayoutListRetriever;
import com.liferay.layout.list.retriever.LayoutListRetrieverTracker;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.reflect.GenericUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Eudaldo Alonso
 */
@Component(immediate = true, service = LayoutListRetrieverTracker.class)
public class LayoutListRetrieverTrackerImpl
	implements LayoutListRetrieverTracker {

	@Override
	public LayoutListRetriever<?, ?> getLayoutListRetriever(String type) {
		return _layoutListRetrievers.get(type);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext,
			(Class<LayoutListRetriever<?, ?>>)
				(Class<?>)LayoutListRetriever.class,
			new LayoutListRetrieverServiceTrackerCustomizer(bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private final Map<String, LayoutListRetriever<?, ?>> _layoutListRetrievers =
		new ConcurrentHashMap<>();
	private ServiceTracker<LayoutListRetriever<?, ?>, LayoutListRetriever<?, ?>>
		_serviceTracker;

	private class LayoutListRetrieverServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<LayoutListRetriever<?, ?>, LayoutListRetriever<?, ?>> {

		public LayoutListRetrieverServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public LayoutListRetriever<?, ?> addingService(
			ServiceReference<LayoutListRetriever<?, ?>> serviceReference) {

			LayoutListRetriever<?, ?> layoutListRetriever =
				_bundleContext.getService(serviceReference);

			_layoutListRetrievers.put(
				GenericUtil.getGenericClassName(layoutListRetriever),
				layoutListRetriever);

			return layoutListRetriever;
		}

		@Override
		public void modifiedService(
			ServiceReference<LayoutListRetriever<?, ?>> serviceReference,
			LayoutListRetriever<?, ?> layoutListRetriever) {
		}

		@Override
		public void removedService(
			ServiceReference<LayoutListRetriever<?, ?>> serviceReference,
			LayoutListRetriever<?, ?> layoutListRetriever) {

				_layoutListRetrievers.remove(
						GenericUtil.getGenericClassName(layoutListRetriever));

				_bundleContext.ungetService(serviceReference);

		}

		private final BundleContext _bundleContext;

	}

}