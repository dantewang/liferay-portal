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

package com.liferay.document.library.layout.set.prototype.internal.upgrade.v1_0_0;

import com.liferay.portal.kernel.upgrade.BaseUpgradeLayoutSetPrototype;
import com.liferay.portal.kernel.util.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.language.LanguageResources;

/**
 * @author Leon Chi
 */
public class UpgradeLayoutSetPrototype extends BaseUpgradeLayoutSetPrototype {

	@Override
	protected void doUpgrade() throws Exception {
		updateLayoutPrototype(
			"%Intranet Site%", _getResourceBundleLoader(),
			"layout-set-prototype-intranet-site-title",
			"layout-set-prototype-intranet-site-description");
	}

	private ResourceBundleLoader _getResourceBundleLoader() {
		Class<?> clazz = getClass();

		return new AggregateResourceBundleLoader(
			ResourceBundleUtil.getResourceBundleLoader(
				"content.Language", clazz.getClassLoader()),
			LanguageResources.RESOURCE_BUNDLE_LOADER);
	}

}