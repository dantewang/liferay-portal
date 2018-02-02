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

package com.liferay.portlet;

import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.util.JavaConstants;

import java.io.Serializable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

/**
 * @author Leon Chi
 */
public class ActionScopedRequestAttributesPool {

	public static final String ACTION_SCOPED_REQUEST_ATTRIBUTES_SESSION_DATA =
		"action.scoped.request.attributes.session.data";

	public static String handleActionScopedRequestAttributesPool(
		PortletRequest portletRequest) {

		PortletConfig portletConfig =
			(PortletConfig)portletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_CONFIG);

		Map<String, String[]> containerRuntimeOptions =
			portletConfig.getContainerRuntimeOptions();

		String[] actionScopedRequestAttributesValues =
			containerRuntimeOptions.get(
				"javax.portlet.actionScopedRequestAttributes");

		if ((actionScopedRequestAttributesValues != null) &&
			(Boolean.parseBoolean(actionScopedRequestAttributesValues[0]) ==
				false)) {

			return null;
		}

		PortletRequestImpl portletRequestImpl =
			(PortletRequestImpl)portletRequest;

		String lifecycle = portletRequestImpl.getLifecycle();

		String actionScopeIdParameter = portletRequest.getParameter(
			PortletRequest.ACTION_SCOPE_ID);

		PortletSession portletSession = portletRequest.getPortletSession();

		ActionScopedRequestAttributesSessionData sessionData =
			(ActionScopedRequestAttributesSessionData)portletSession.
				getAttribute(ACTION_SCOPED_REQUEST_ATTRIBUTES_SESSION_DATA);

		if (lifecycle.equals(PortletRequest.ACTION_PHASE)) {
			sessionData = new ActionScopedRequestAttributesSessionData();

			portletRequestImpl.setActionScopedRequestAttributesPool(
				sessionData.actionScopedRequestAttributesPool);

			portletSession.setAttribute(
				ACTION_SCOPED_REQUEST_ATTRIBUTES_SESSION_DATA, sessionData);

			return sessionData.actionScopeId;
		}
		else if (lifecycle.equals(PortletRequest.EVENT_PHASE)) {
			if ((actionScopeIdParameter == null) ||
				((sessionData != null) && (sessionData.rendered == true))) {

				sessionData = new ActionScopedRequestAttributesSessionData();

				portletRequestImpl.setActionScopedRequestAttributesPool(
					sessionData.actionScopedRequestAttributesPool);

				portletSession.setAttribute(
					ACTION_SCOPED_REQUEST_ATTRIBUTES_SESSION_DATA, sessionData);

				return sessionData.actionScopeId;
			}

			if ((actionScopeIdParameter != null) && (sessionData != null) &&
				actionScopeIdParameter.equals(sessionData.actionScopeId) &&
				(sessionData.rendered == false)) {

				portletRequestImpl.setActionScopedRequestAttributesPool(
					sessionData.actionScopedRequestAttributesPool);

				return sessionData.actionScopeId;
			}
		}
		else if (lifecycle.equals(PortletRequest.RENDER_PHASE)) {
			if (actionScopeIdParameter == null) {
				portletSession.removeAttribute(
					ACTION_SCOPED_REQUEST_ATTRIBUTES_SESSION_DATA);
			}

			if ((actionScopeIdParameter != null) && (sessionData != null) &&
				actionScopeIdParameter.equals(sessionData.actionScopeId)) {

				sessionData.rendered = true;

				portletRequestImpl.setActionScopedRequestAttributesPool(
					sessionData.actionScopedRequestAttributesPool);
			}
		}
		else if (lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
			if ((actionScopeIdParameter != null) && (sessionData != null) &&
				actionScopeIdParameter.equals(sessionData.actionScopeId)) {

				portletRequestImpl.setActionScopedRequestAttributesPool(
					sessionData.actionScopedRequestAttributesPool);
			}
		}

		return null;
	}

	private static class ActionScopedRequestAttributesSessionData
		implements Serializable {

		public ActionScopedRequestAttributesSessionData() {
			UUID uuid = new UUID(
				SecureRandomUtil.nextLong(), SecureRandomUtil.nextLong());

			actionScopeId = uuid.toString();

			actionScopedRequestAttributesPool = new ConcurrentHashMap<>();
		}

		public final Map<String, Object> actionScopedRequestAttributesPool;
		public final String actionScopeId;
		public boolean rendered = false;

	}

}