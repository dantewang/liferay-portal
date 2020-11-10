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

import java.util.List;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Hai Yu
 */
@ProviderType
public interface ResourceActionsBag {

	public boolean containsKey(String name);

	public List<String> getModelResourceActions();

	public List<String> getModelResourceGroupDefaultActions();

	public List<String> getModelResourceGuestDefaultActions();

	public List<String> getModelResourceGuestUnsupportedActions();

	public List<String> getModelResourceOwnerDefaultActions();

	public List<String> getPortletResourceActions();

	public ResourceActionsBag getPortletResourceActionsBag(String name);

	public List<String> getPortletResourceGroupDefaultActions();

	public List<String> getPortletResourceGuestDefaultActions();

	public List<String> getPortletResourceGuestUnsupportedActions();

	public List<String> getPortletResourceLayoutManagerActions();

	public ResourceActionsBag getResourceActionsBag(String name);

	public Set<String> keySet();

	public ResourceActionsBag putResourceActionsBags(
		String name, ResourceActionsBag resourceActionsBag, boolean force);

	public ResourceActionsBag remove(String name);

}