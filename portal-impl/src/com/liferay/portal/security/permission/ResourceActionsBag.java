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

package com.liferay.portal.security.permission;

import java.util.Set;

/**
 * @author Dante Wang
 */
public class ResourceActionsBag {

	public ResourceActionsBag(
		Set<String> supportsActions, Set<String> groupDefaultActions,
		Set<String> guestDefaultActions, Set<String> guestUnsupportedActions,
		Set<String> layoutManagerActions, Set<String> ownerDefaultActions) {

		_supportsActions = supportsActions;
		_groupDefaultActions = groupDefaultActions;
		_guestDefaultActions = guestDefaultActions;
		_guestUnsupportedActions = guestUnsupportedActions;
		_layoutManagerActions = layoutManagerActions;
		_ownerDefaultActions = ownerDefaultActions;
	}

	public Set<String> getGroupDefaultActions() {
		return _groupDefaultActions;
	}

	public Set<String> getGuestDefaultActions() {
		return _guestDefaultActions;
	}

	public Set<String> getGuestUnsupportedActions() {
		return _guestUnsupportedActions;
	}

	public Set<String> getLayoutManagerActions() {
		return _layoutManagerActions;
	}

	public Set<String> getOwnerDefaultActions() {
		return _ownerDefaultActions;
	}

	public Set<String> getSupportsActions() {
		return _supportsActions;
	}

	private final Set<String> _groupDefaultActions;
	private final Set<String> _guestDefaultActions;
	private final Set<String> _guestUnsupportedActions;
	private final Set<String> _layoutManagerActions;
	private final Set<String> _ownerDefaultActions;
	private final Set<String> _supportsActions;

}