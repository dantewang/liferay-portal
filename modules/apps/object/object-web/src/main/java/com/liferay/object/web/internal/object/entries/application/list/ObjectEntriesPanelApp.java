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

package com.liferay.object.web.internal.object.entries.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Locale;

import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class ObjectEntriesPanelApp extends BasePanelApp {

	public ObjectEntriesPanelApp(ObjectDefinition objectDefinition) {
		_objectDefinition = objectDefinition;

		String filter = StringBundler.concat(
			"(&(javax.portlet.name=", _objectDefinition.getPortletId(),
			")(objectClass=", Portlet.class, "))");

		try {
			_serviceTracker = new ServiceTracker<>(
				_bundleContext, _bundleContext.createFilter(filter),
				new ServiceTrackerCustomizer<Portlet, Portlet>() {

					@Override
					public Portlet addingService(
						ServiceReference<Portlet> serviceReference) {

						_portlet = _bundleContext.getService(serviceReference);

						return _portlet;
					}

					@Override
					public void modifiedService(
						ServiceReference<Portlet> serviceReference,
						Portlet portlet) {
					}

					@Override
					public void removedService(
						ServiceReference<Portlet> serviceReference,
						Portlet portlet) {

						_portlet = null;

						_bundleContext.ungetService(serviceReference);
					}

				});
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			throw new RuntimeException(invalidSyntaxException);
		}
	}

	@Override
	public String getKey() {
		return super.getKey() + StringPool.POUND +
			_objectDefinition.getObjectDefinitionId();
	}

	@Override
	public String getLabel(Locale locale) {
		return _objectDefinition.getPluralLabel(locale);
	}

	@Override
	public Portlet getPortlet() {
		_openServiceTracker();

		return _portlet;
	}

	@Override
	public String getPortletId() {
		return _objectDefinition.getPortletId();
	}

	@Override
	public PortletURL getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		return PortletURLBuilder.create(
			super.getPortletURL(httpServletRequest)
		).setParameter(
			"objectDefinitionId", _objectDefinition.getObjectDefinitionId()
		).buildPortletURL();
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (permissionChecker.getCompanyId() !=
				_objectDefinition.getCompanyId()) {

			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Override
	protected Group getGroup(HttpServletRequest httpServletRequest) {
		if (StringUtil.equals(
				_objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_COMPANY)) {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return themeDisplay.getControlPanelGroup();
		}

		return super.getGroup(httpServletRequest);
	}

	private void _openServiceTracker() {
		boolean opened = _opened;

		if (opened) {
			return;
		}

		synchronized (_opened) {
			if (_opened) {
				return;
			}

			_serviceTracker.open();

			_opened = true;
		}
	}

	private static final Bundle _bundle = FrameworkUtil.getBundle(
		ObjectEntriesPanelApp.class);
	private static final BundleContext _bundleContext =
		_bundle.getBundleContext();

	private final ObjectDefinition _objectDefinition;
	private Boolean _opened = false;
	private Portlet _portlet = null;
	private final ServiceTracker<Portlet, Portlet> _serviceTracker;

}