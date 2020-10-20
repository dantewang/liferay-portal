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

import java.util.Collections;
import java.util.Set;

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