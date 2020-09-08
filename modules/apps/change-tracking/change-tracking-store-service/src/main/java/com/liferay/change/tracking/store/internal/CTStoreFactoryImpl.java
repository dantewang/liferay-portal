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

package com.liferay.change.tracking.store.internal;

import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.store.model.CTSContent;
import com.liferay.change.tracking.store.service.CTSContentLocalService;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.change.tracking.store.CTStoreFactory;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;

import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Shuyang Zhou
 */
@Component(service = CTStoreFactory.class)
public class CTStoreFactoryImpl implements CTStoreFactory {

	@Override
	public Store createCTStore(Store store, String storeType) {
		return new CTStore(
			_ctEntryLocalService,
			_classNameLocalService.getClassNameId(CTSContent.class),
			_ctsContentLocalService, store, storeType);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		String filterString = StringBundler.concat(
			"(&(objectClass=", Store.class.getName(), ")(store.type=*))");

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, filterString, new StoreServiceTrackerCustomizer());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private BundleContext _bundleContext;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private CTSContentLocalService _ctsContentLocalService;

	private ServiceRegistration<Store> _serviceRegistration;
	private ServiceTracker<Store, Store> _serviceTracker;

	private class StoreServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<Store, Store> {

		@Override
		public Store addingService(ServiceReference<Store> serviceReference) {
			if (GetterUtil.getBoolean(
					serviceReference.getProperty("ct.aware"))) {

				return null;
			}

			Store store = createCTStore(
				_bundleContext.getService(serviceReference),
				GetterUtil.getString(
					serviceReference.getProperty("store.type")));

			Dictionary<String, Object> properties = new HashMapDictionary<>();

			properties.put("ct.aware", "true");
			properties.put(
				"store.type", serviceReference.getProperty("store.type"));

			_serviceRegistration = _bundleContext.registerService(
				Store.class, store, properties);

			return store;
		}

		@Override
		public void modifiedService(
			ServiceReference<Store> serviceReference, Store store) {
		}

		@Override
		public void removedService(
			ServiceReference<Store> serviceReference, Store store) {

			_bundleContext.ungetService(serviceReference);

			_serviceRegistration.unregister();
		}

	}

}