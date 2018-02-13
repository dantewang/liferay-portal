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

import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

/**
 * @author Leon Chi
 */
public class ActionScopedRequestAttributesUtil {

	public static final String ACTION_SCOPED_REQUEST_ATTRIBUTES_SESSION_DATA =
		"action.scoped.request.attributes.session.data";

	public static String handleActionScopedRequestAttributes(
		PortletRequest portletRequest) {

		PortletConfig portletConfig =
			(PortletConfig)portletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_CONFIG);

		Map<String, String[]> containerRuntimeOptions =
			portletConfig.getContainerRuntimeOptions();

		String[] actionScopedRequestAttributesValues =
			containerRuntimeOptions.get(
				"javax.portlet.actionScopedRequestAttributes");

		if ((actionScopedRequestAttributesValues == null) ||
			!Boolean.parseBoolean(actionScopedRequestAttributesValues[0])) {

			return null;
		}

		PortletRequestImpl portletRequestImpl =
			(PortletRequestImpl)portletRequest;

		String lifecycle = portletRequestImpl.getLifecycle();

		String actionScopeId = portletRequest.getParameter(
			PortletRequest.ACTION_SCOPE_ID);

		PortletSession portletSession = portletRequest.getPortletSession();

		SessionData sessionData = (SessionData)portletSession.getAttribute(
			ACTION_SCOPED_REQUEST_ATTRIBUTES_SESSION_DATA);

		if (lifecycle.equals(PortletRequest.ACTION_PHASE)) {
			sessionData = new SessionData();

			portletRequestImpl.setActionScopedRequestAttributes(
				sessionData.actionScopedRequestAttributes);

			portletSession.setAttribute(
				ACTION_SCOPED_REQUEST_ATTRIBUTES_SESSION_DATA, sessionData);

			return sessionData.actionScopeId;
		}
		else if (lifecycle.equals(PortletRequest.EVENT_PHASE)) {
			if ((actionScopeId == null) ||
				((sessionData != null) && sessionData.rendered)) {

				sessionData = new SessionData();

				portletRequestImpl.setActionScopedRequestAttributes(
					sessionData.actionScopedRequestAttributes);

				portletSession.setAttribute(
					ACTION_SCOPED_REQUEST_ATTRIBUTES_SESSION_DATA, sessionData);

				return sessionData.actionScopeId;
			}

			if ((actionScopeId != null) && (sessionData != null) &&
				actionScopeId.equals(sessionData.actionScopeId) &&
				!sessionData.rendered) {

				portletRequestImpl.setActionScopedRequestAttributes(
					sessionData.actionScopedRequestAttributes);

				return sessionData.actionScopeId;
			}
		}
		else if (lifecycle.equals(PortletRequest.RENDER_PHASE)) {
			if (actionScopeId == null) {
				portletSession.removeAttribute(
					ACTION_SCOPED_REQUEST_ATTRIBUTES_SESSION_DATA);
			}

			if ((actionScopeId != null) && (sessionData != null) &&
				actionScopeId.equals(sessionData.actionScopeId)) {

				sessionData.rendered = true;

				portletRequestImpl.setActionScopedRequestAttributes(
					sessionData.actionScopedRequestAttributes);
			}
		}
		else if (lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
			if ((actionScopeId != null) && (sessionData != null) &&
				actionScopeId.equals(sessionData.actionScopeId)) {

				portletRequestImpl.setActionScopedRequestAttributes(
					sessionData.actionScopedRequestAttributes);
			}
		}

		return null;
	}

	private static class SessionData implements Serializable {

		public final Map<String, Object> actionScopedRequestAttributes =
			new ConcurrentHashMap<>();
		public final String actionScopeId = PortalUUIDUtil.generate();
		public boolean rendered = false;

	}

}