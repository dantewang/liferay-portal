/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission;

import com.liferay.portal.kernel.model.ResourceAction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Dante Wang
 */
public class ResourceActionsCache {

	public static Map<String, Double> getModelResourceWeightsMap() {
		return _modelResourceWeightsMap;
	}

	public static Set<String> getOrganizationModelResources() {
		return _organizationModelResources;
	}

	public static Set<String> getPortalModelResources() {
		return _portalModelResources;
	}

	public static Map<String, String> getPortletRootModelResourcesMap() {
		return _portletRootModelResourcesMap;
	}

	public static Map<String, ResourceActionsBag> getResourceActionsBagMap() {
		return _resourceActionsBagsMap;
	}

	public static Map<String, ResourceAction> getResourceActionsMap() {
		return _resourceActionsMap;
	}

	public static Map<String, Set<String>> getResourceReferencesMap() {
		return _resourceReferencesMap;
	}

	public static class ResourceActionsBag {

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

		private final Set<String> _groupDefaultActions = new HashSet<>();
		private final Set<String> _guestDefaultActions = new HashSet<>();
		private final Set<String> _guestUnsupportedActions = new HashSet<>();
		private final Set<String> _layoutManagerActions = new HashSet<>();
		private final Set<String> _ownerDefaultActions = new HashSet<>();
		private final Set<String> _supportsActions = new HashSet<>();

	}

	private static final Map<String, Double> _modelResourceWeightsMap =
		new HashMap<>();
	private static final Set<String> _organizationModelResources =
		new HashSet<>();
	private static final Set<String> _portalModelResources = new HashSet<>();
	private static final Map<String, String> _portletRootModelResourcesMap =
		new HashMap<>();

	private static final Map<String, ResourceActionsBag>
		_resourceActionsBagsMap = new HashMap<String, ResourceActionsBag>() {

			@Override
			public ResourceActionsBag remove(Object key) {
				synchronized (_resourceActionsBagsMap) {
					_modelResourceWeightsMap.remove(key);

					_organizationModelResources.remove((String)key);

					_portalModelResources.remove((String)key);

					_portletRootModelResourcesMap.remove(key);

					_resourceReferencesMap.remove(key);

					return super.remove(key);
				}
			}

		};

	private static final Map<String, ResourceAction> _resourceActionsMap =
		new ConcurrentHashMap<String, ResourceAction>() {

			@Override
			public ResourceAction remove(Object key) {
				ResourceAction resourceAction = super.remove(key);

				if (resourceAction == null) {
					return null;
				}

				ResourceActionsBag resourceActionsBag =
					_resourceActionsBagsMap.get(resourceAction.getName());

				if (resourceActionsBag == null) {
					return resourceAction;
				}

				Set<String> supportsActions =
					resourceActionsBag.getSupportsActions();

				supportsActions.remove(resourceAction.getActionId());

				if (supportsActions.isEmpty()) {
					_resourceActionsBagsMap.remove(resourceAction.getName());
				}

				return resourceAction;
			}

		};

	private static final Map<String, Set<String>> _resourceReferencesMap =
		new HashMap<>();

}