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

package com.liferay.portal.kernel.security.permission;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Hai Yu
 */
public class ResourceActionsBagUtil {

	public static boolean containsKey(String name) {
		return _resourceActionsBag.containsKey(name);
	}

	public static List<String> getModelResourceActions(String name) {
		ResourceActionsBag modelResourceActionsBag = getResourceActionsBag(
			name);

		if (modelResourceActionsBag == null) {
			return new ArrayList<>();
		}

		return modelResourceActionsBag.getModelResourceActions();
	}

	public static List<String> getModelResourceGroupDefaultActions(
		String name) {

		ResourceActionsBag modelResourceActionsBag = getResourceActionsBag(
			name);

		if (modelResourceActionsBag == null) {
			return new ArrayList<>();
		}

		return modelResourceActionsBag.getModelResourceGroupDefaultActions();
	}

	public static List<String> getModelResourceGuestDefaultActions(
		String name) {

		ResourceActionsBag modelResourceActionsBag = getResourceActionsBag(
			name);

		if (modelResourceActionsBag == null) {
			return new ArrayList<>();
		}

		return modelResourceActionsBag.getModelResourceGuestDefaultActions();
	}

	public static List<String> getModelResourceGuestUnsupportedActions(
		String name) {

		ResourceActionsBag modelResourceActionsBag = getResourceActionsBag(
			name);

		if (modelResourceActionsBag == null) {
			return new ArrayList<>();
		}

		return modelResourceActionsBag.
			getModelResourceGuestUnsupportedActions();
	}

	public static List<String> getModelResourceOwnerDefaultActions(
		String name) {

		ResourceActionsBag modelResourceActionsBag = getResourceActionsBag(
			name);

		if (modelResourceActionsBag == null) {
			return new ArrayList<>();
		}

		return modelResourceActionsBag.getModelResourceOwnerDefaultActions();
	}

	public static List<String> getPortletResourceActions(String name) {
		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag =
			_resourceActionsBag.getPortletResourceActionsBag(name);

		return portletResourceActionsBag.getPortletResourceActions();
	}

	public static List<String> getPortletResourceGroupDefaultActions(
		String name) {

		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag =
			_resourceActionsBag.getPortletResourceActionsBag(name);

		return portletResourceActionsBag.
			getPortletResourceGroupDefaultActions();
	}

	public static List<String> getPortletResourceGuestDefaultActions(
		String name) {

		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag =
			_resourceActionsBag.getPortletResourceActionsBag(name);

		return portletResourceActionsBag.
			getPortletResourceGuestDefaultActions();
	}

	public static List<String> getPortletResourceGuestUnsupportedActions(
		String name) {

		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag =
			_resourceActionsBag.getPortletResourceActionsBag(name);

		return portletResourceActionsBag.
			getPortletResourceGuestUnsupportedActions();
	}

	public static List<String> getPortletResourceLayoutManagerActions(
		String name) {

		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag =
			_resourceActionsBag.getPortletResourceActionsBag(name);

		return portletResourceActionsBag.
			getPortletResourceLayoutManagerActions();
	}

	public static List<String> getResourceActions(String name) {
		if (name.indexOf(CharPool.PERIOD) != -1) {
			return getModelResourceActions(name);
		}

		return getPortletResourceActions(name);
	}

	public static List<String> getResourceActions(
		String portletResource, String modelResource) {

		List<String> actions = null;

		if (Validator.isNull(modelResource)) {
			actions = getPortletResourceActions(portletResource);
		}
		else {
			actions = getModelResourceActions(modelResource);
		}

		return actions;
	}

	public static ResourceActionsBag getResourceActionsBag(String name) {
		return _resourceActionsBag.getResourceActionsBag(name);
	}

	public static List<String> getResourceGuestUnsupportedActions(
		String portletResource, String modelResource) {

		if (Validator.isNull(modelResource)) {
			return getPortletResourceGuestUnsupportedActions(portletResource);
		}
		else if (Validator.isNull(portletResource)) {
			return getModelResourceGuestUnsupportedActions(modelResource);
		}
		else if (containsKey(modelResource)) {
			return getModelResourceGuestUnsupportedActions(modelResource);
		}
		else if (containsKey(portletResource)) {
			return getPortletResourceGuestUnsupportedActions(portletResource);
		}

		return Collections.emptyList();
	}

	public static Set<String> keySet() {
		return _resourceActionsBag.keySet();
	}

	public static ResourceActionsBag putResourceActionsBags(
		String name, ResourceActionsBag resourceActionsBag, boolean force) {

		return _resourceActionsBag.putResourceActionsBags(
			name, resourceActionsBag, force);
	}

	public static ResourceActionsBag remove(String name) {
		return _resourceActionsBag.remove(name);
	}

	public void setResourceActionsBag(ResourceActionsBag resourceActionsBag) {
		_resourceActionsBag = resourceActionsBag;
	}

	private static ResourceActionsBag _resourceActionsBag;

}