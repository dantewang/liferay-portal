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

/**
 * @author Hai Yu
 */
public class ResourceActionsBagUtil {

	public static List<String> getModelResourceActions(String name) {
		ResourceActionsBag modelResourceActionsBag = ResourceActionsBagPool.get(
			name);

		if (modelResourceActionsBag == null) {
			return new ArrayList<>();
		}

		return new ArrayList<>(modelResourceActionsBag.getSupportsActions());
	}

	public static List<String> getModelResourceGroupDefaultActions(
		String name) {

		ResourceActionsBag modelResourceActionsBag = ResourceActionsBagPool.get(
			name);

		if (modelResourceActionsBag == null) {
			return new ArrayList<>();
		}

		return new ArrayList<>(
			modelResourceActionsBag.getGroupDefaultActions());
	}

	public static List<String> getModelResourceGuestDefaultActions(
		String name) {

		ResourceActionsBag modelResourceActionsBag = ResourceActionsBagPool.get(
			name);

		if (modelResourceActionsBag == null) {
			return new ArrayList<>();
		}

		return new ArrayList<>(
			modelResourceActionsBag.getGuestDefaultActions());
	}

	public static List<String> getModelResourceGuestUnsupportedActions(
		String name) {

		ResourceActionsBag modelResourceActionsBag = ResourceActionsBagPool.get(
			name);

		if (modelResourceActionsBag == null) {
			return new ArrayList<>();
		}

		return new ArrayList<>(
			modelResourceActionsBag.getGuestUnsupportedActions());
	}

	public static List<String> getModelResourceOwnerDefaultActions(
		String name) {

		ResourceActionsBag modelResourceActionsBag = ResourceActionsBagPool.get(
			name);

		if (modelResourceActionsBag == null) {
			return new ArrayList<>();
		}

		return new ArrayList<>(
			modelResourceActionsBag.getOwnerDefaultActions());
	}

	public static List<String> getPortletResourceActions(String name) {
		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag =
			_resourceActionsBag.getPortletResourceActionsBag(name);

		return new ArrayList<>(portletResourceActionsBag.getSupportsActions());
	}

	public static List<String> getPortletResourceGroupDefaultActions(
		String name) {

		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag =
			_resourceActionsBag.getPortletResourceActionsBag(name);

		return new ArrayList<>(
			portletResourceActionsBag.getGroupDefaultActions());
	}

	public static List<String> getPortletResourceGuestDefaultActions(
		String name) {

		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag =
			_resourceActionsBag.getPortletResourceActionsBag(name);

		return new ArrayList<>(
			portletResourceActionsBag.getGuestDefaultActions());
	}

	public static List<String> getPortletResourceGuestUnsupportedActions(
		String name) {

		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag =
			_resourceActionsBag.getPortletResourceActionsBag(name);

		return new ArrayList<>(
			portletResourceActionsBag.getGuestUnsupportedActions());
	}

	public static List<String> getPortletResourceLayoutManagerActions(
		String name) {

		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag =
			_resourceActionsBag.getPortletResourceActionsBag(name);

		return new ArrayList<>(
			portletResourceActionsBag.getLayoutManagerActions());
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

	public static List<String> getResourceGuestUnsupportedActions(
		String portletResource, String modelResource) {

		if (Validator.isNull(modelResource)) {
			return getPortletResourceGuestUnsupportedActions(portletResource);
		}
		else if (Validator.isNull(portletResource)) {
			return getModelResourceGuestUnsupportedActions(modelResource);
		}
		else if (ResourceActionsBagPool.containsKey(modelResource)) {
			return getModelResourceGuestUnsupportedActions(modelResource);
		}
		else if (ResourceActionsBagPool.containsKey(portletResource)) {
			return getPortletResourceGuestUnsupportedActions(portletResource);
		}

		return Collections.emptyList();
	}

	public void setResourceActionsBag(ResourceActionsBag resourceActionsBag) {
		_resourceActionsBag = resourceActionsBag;
	}

	private static ResourceActionsBag _resourceActionsBag;

}