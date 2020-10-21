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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsBag;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hai Yu
 */
public class ResourceActionsBagImpl implements ResourceActionsBag {

	public ResourceActionsBagImpl() {
		_groupDefaultActions = new HashSet<>();
		_guestDefaultActions = new HashSet<>();
		_guestUnsupportedActions = new HashSet<>();
		_layoutManagerActions = new HashSet<>();
		_ownerDefaultActions = new HashSet<>();
		_supportsActions = new HashSet<>();
	}

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
	public ResourceActionsBag getPortletResourceActionsBag(String name) {
		ResourceActionsBag resourceActionsBag = getResourceActionsBag(name);

		if (resourceActionsBag != null) {
			return resourceActionsBag;
		}

		Portlet portlet = PortletLocalServiceUtil.getPortletById(name);

		Set<String> portletActions = new HashSet<>();
		Set<String> groupDefaultActions = new HashSet<>();
		Set<String> guestDefaultActions = new HashSet<>();
		Set<String> layoutManagerActions = new HashSet<>();
		Set<String> guestUnsupportedActions = new HashSet<>();

		portletActions.addAll(_getPortletMimeTypeActions(name, portlet));

		_checkPortletLayoutManagerActions(portletActions);

		portletActions.add(ActionKeys.ACCESS_IN_CONTROL_PANEL);

		groupDefaultActions.add(ActionKeys.VIEW);

		guestDefaultActions.add(ActionKeys.VIEW);

		Collections.addAll(
			layoutManagerActions,
			new String[] {
				ActionKeys.ADD_TO_PAGE, ActionKeys.CONFIGURATION,
				ActionKeys.PERMISSIONS, ActionKeys.PREFERENCES, ActionKeys.VIEW
			});

		Collections.addAll(
			guestUnsupportedActions,
			new String[] {ActionKeys.CONFIGURATION, ActionKeys.PERMISSIONS});

		resourceActionsBag = new ResourceActionsBagImpl(
			groupDefaultActions, guestDefaultActions, guestUnsupportedActions,
			layoutManagerActions, new HashSet<>(), portletActions);

		return putResourceActionsBags(name, resourceActionsBag, false);
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

	private void _checkPortletLayoutManagerActions(Set<String> actions) {
		if (!actions.contains(ActionKeys.ACCESS_IN_CONTROL_PANEL) &&
			!actions.contains(ActionKeys.ADD_TO_PAGE)) {

			actions.add(ActionKeys.ADD_TO_PAGE);
		}

		actions.add(ActionKeys.CONFIGURATION);
		actions.add(ActionKeys.PERMISSIONS);
		actions.add(ActionKeys.PREFERENCES);
		actions.add(ActionKeys.VIEW);
	}

	private Set<String> _getPortletMimeTypeActions(
		String name, Portlet portlet) {

		Set<String> actions = new LinkedHashSet<>();

		if (portlet != null) {
			Map<String, Set<String>> portletModes = portlet.getPortletModes();

			Set<String> mimeTypePortletModes = portletModes.get(
				ContentTypes.TEXT_HTML);

			if (mimeTypePortletModes != null) {
				for (String actionId : mimeTypePortletModes) {
					if (StringUtil.equalsIgnoreCase(actionId, "edit")) {
						actions.add(ActionKeys.PREFERENCES);
					}
					else if (StringUtil.equalsIgnoreCase(
								actionId, "edit_guest")) {

						actions.add(ActionKeys.GUEST_PREFERENCES);
					}
					else {
						actions.add(StringUtil.toUpperCase(actionId));
					}
				}
			}
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to obtain resource actions for unknown portlet " +
						name);
			}
		}

		return actions;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceActionsBagImpl.class);

	private static final Map<String, ResourceActionsBag> _resourceActionsBags =
		new ConcurrentHashMap<>();

	private final Set<String> _groupDefaultActions;
	private final Set<String> _guestDefaultActions;
	private final Set<String> _guestUnsupportedActions;
	private final Set<String> _layoutManagerActions;
	private final Set<String> _ownerDefaultActions;
	private final Set<String> _supportsActions;

}