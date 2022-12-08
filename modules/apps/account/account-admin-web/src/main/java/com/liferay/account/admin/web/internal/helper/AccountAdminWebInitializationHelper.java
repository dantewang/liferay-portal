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

package com.liferay.account.admin.web.internal.helper;

import com.liferay.account.admin.web.internal.display.AccountEntryDisplay;
import com.liferay.account.manager.CurrentAccountEntryManager;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(service = {})
public class AccountAdminWebInitializationHelper {

	@Activate
	protected void activate() {
		AccountEntryDisplay.setCurrentAccountEntryManager(
			_currentAccountEntryManager);
	}

	@Reference
	private CurrentAccountEntryManager _currentAccountEntryManager;

}