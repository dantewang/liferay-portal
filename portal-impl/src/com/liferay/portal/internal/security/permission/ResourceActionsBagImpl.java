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

package com.liferay.portal.internal.security.permission;

import com.liferay.portal.kernel.security.permission.ResourceActionsBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hai Yu
 */
public class ResourceActionsBagImpl implements ResourceActionsBag {

	public ResourceActionsBagImpl(
		Set<String> groupDefaultActions, Set<String> guestDefaultActions,
		Set<String> guestUnsupportedActions, Set<String> layoutManagerActions,
		Set<String> ownerDefaultActions, Set<String> supportsActions) {

		_groupDefaultActions = Collections.unmodifiableSet(groupDefaultActions);
		_guestDefaultActions = Collections.unmodifiableSet(guestDefaultActions);
		_guestUnsupportedActions = Collections.unmodifiableSet(
			guestUnsupportedActions);
		_layoutManagerActions = Collections.unmodifiableSet(
			layoutManagerActions);
		_ownerDefaultActions = Collections.unmodifiableSet(ownerDefaultActions);
		_supportsActions = Collections.unmodifiableSet(supportsActions);
	}

	@Override
	public List<String> getModelResourceActions() {
		return new ArrayList<>(_supportsActions);
	}

	@Override
	public List<String> getModelResourceGroupDefaultActions() {
		return new ArrayList<>(_groupDefaultActions);
	}

	@Override
	public List<String> getModelResourceGuestDefaultActions() {
		return new ArrayList<>(_guestDefaultActions);
	}

	@Override
	public List<String> getModelResourceGuestUnsupportedActions() {
		return new ArrayList<>(_guestUnsupportedActions);
	}

	@Override
	public List<String> getModelResourceOwnerDefaultActions() {
		return new ArrayList<>(_ownerDefaultActions);
	}

	@Override
	public List<String> getPortletResourceActions() {
		return new ArrayList<>(_supportsActions);
	}

	@Override
	public List<String> getPortletResourceGroupDefaultActions() {
		return new ArrayList<>(_groupDefaultActions);
	}

	@Override
	public List<String> getPortletResourceGuestDefaultActions() {
		return new ArrayList<>(_guestDefaultActions);
	}

	@Override
	public List<String> getPortletResourceGuestUnsupportedActions() {
		return new ArrayList<>(_guestUnsupportedActions);
	}

	@Override
	public List<String> getPortletResourceLayoutManagerActions() {
		return new ArrayList<>(_layoutManagerActions);
	}

	@Override
	public ResourceActionsBag getResourceActionsBag(String name) {
		return _resourceActionsBags.get(name);
	}

	@Override
	public ResourceActionsBag putResourceActionsBags(
		String name, ResourceActionsBag resourceActionsBag, boolean force) {

		synchronized (_resourceActionsBags) {
			return _resourceActionsBags.compute(
				name,
				(key, value) -> {
					if (force || (value == null)) {
						_resourceActionsBags.put(name, resourceActionsBag);

						return resourceActionsBag;
					}

					return value;
				});
		}
	}

	private static final Map<String, ResourceActionsBag> _resourceActionsBags =
		new ConcurrentHashMap<>();

	private final Set<String> _groupDefaultActions;
	private final Set<String> _guestDefaultActions;
	private final Set<String> _guestUnsupportedActions;
	private final Set<String> _layoutManagerActions;
	private final Set<String> _ownerDefaultActions;
	private final Set<String> _supportsActions;

}