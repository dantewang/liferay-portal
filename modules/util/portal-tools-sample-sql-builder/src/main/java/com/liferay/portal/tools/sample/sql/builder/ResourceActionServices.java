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

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.persistence.ResourceActionPersistence;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.model.impl.ResourceActionImpl;
import com.liferay.portal.security.permission.ResourceActionsImpl;
import com.liferay.portal.service.impl.ResourceActionLocalServiceImpl;
import com.liferay.portal.xml.SAXReaderImpl;
import com.liferay.util.SimpleCounter;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Dante Wang
 */
public class ResourceActionServices {

	public static ResourceActionLocalService getResourceActionLocalService() {
		return _resourceActionLocalService;
	}

	public static Map<String, Set<ResourceAction>> getResourceActionMap() {
		return _resourceActionMap;
	}

	public static ResourceActions getResourceActions() {
		return _resourceActions;
	}

	public static void init(SimpleCounter counter) {
		UnsecureSAXReaderUtil unsecureSAXReaderUtil =
			new UnsecureSAXReaderUtil();

		unsecureSAXReaderUtil.setSAXReader(new SAXReaderImpl());

		_resourceActionLocalService = new ResourceActionLocalServiceImpl() {
			{
				counterLocalService =
					(CounterLocalService)ProxyUtil.newProxyInstance(
						ResourceActionServices.class.getClassLoader(),
						new Class<?>[] {CounterLocalService.class},
						(proxy, method, args) -> {
							String methodName = method.getName();

							if (Objects.equals(methodName, "increment")) {
								return counter.get();
							}

							return null;
						});

				resourceActionLocalService = this;

				resourceActionPersistence =
					(ResourceActionPersistence)ProxyUtil.newProxyInstance(
						ResourceActionServices.class.getClassLoader(),
						new Class<?>[] {ResourceActionPersistence.class},
						(proxy, method, args) -> {
							String methodName = method.getName();

							if (Objects.equals(methodName, "create")) {
								ResourceAction resourceAction =
									new ResourceActionImpl();

								resourceAction.setPrimaryKey((long)args[0]);

								return resourceAction;
							}

							if (Objects.equals(methodName, "update")) {
								ResourceAction resourceAction =
									(ResourceAction)args[0];

								Set<ResourceAction> resourceActionSet =
									_resourceActionMap.computeIfAbsent(
										resourceAction.getName(),
										key -> new LinkedHashSet<>());

								resourceActionSet.add(resourceAction);

								return resourceAction;
							}

							return null;
						});

				resourcePermissionLocalService = ProxyFactory.newDummyInstance(
					ResourcePermissionLocalService.class);
			}

			@Override
			public List<ResourceAction> getResourceActions(String name) {
				return Collections.emptyList();
			}

		};

		_resourceActions = new ResourceActionsImpl() {
			{
				portletLocalService = ProxyFactory.newDummyInstance(
					PortletLocalService.class);

				resourceActionLocalService = _resourceActionLocalService;
			}
		};
	}

	private static ResourceActionLocalService _resourceActionLocalService;
	private static final Map<String, Set<ResourceAction>> _resourceActionMap =
		new HashMap<>();
	private static ResourceActions _resourceActions;

}