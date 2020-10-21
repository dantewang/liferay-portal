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
import com.liferay.portal.kernel.security.permission.ResourceActionsBagPool;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
	public Set<String> getGroupDefaultActions() {
		return _groupDefaultActions;
	}

	@Override
	public Set<String> getGuestDefaultActions() {
		return _guestDefaultActions;
	}

	@Override
	public Set<String> getGuestUnsupportedActions() {
		return _guestUnsupportedActions;
	}

	@Override
	public Set<String> getLayoutManagerActions() {
		return _layoutManagerActions;
	}

	@Override
	public Set<String> getOwnerDefaultActions() {
		return _ownerDefaultActions;
	}

	@Override
	public ResourceActionsBag getPortletResourceActionsBag(String name) {
		ResourceActionsBag resourceActionsBag = ResourceActionsBagPool.get(
			name);

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

		return ResourceActionsBagPool.put(name, resourceActionsBag, false);
	}

	@Override
	public Set<String> getSupportsActions() {
		return _supportsActions;
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

	private final Set<String> _groupDefaultActions;
	private final Set<String> _guestDefaultActions;
	private final Set<String> _guestUnsupportedActions;
	private final Set<String> _layoutManagerActions;
	private final Set<String> _ownerDefaultActions;
	private final Set<String> _supportsActions;

}