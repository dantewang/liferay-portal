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

package com.liferay.layout.internal.service;

import com.liferay.friendly.url.exception.DuplicateFriendlyURLEntryException;
import com.liferay.friendly.url.exception.FriendlyURLLengthException;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dante Wang
 */
@Component(immediate = true, service = ServiceWrapper.class)
public class FriendlyURLEntryLocalServiceWrapper
	extends com.liferay.friendly.url.service.
				FriendlyURLEntryLocalServiceWrapper {

	@Override
	public void validate(
			long groupId, long classNameId, long classPK, String urlTitle)
		throws PortalException {

		long layoutClassNameId = _portal.getClassNameId(Layout.class);

		if ((classNameId != layoutClassNameId) || (classPK <= 0)) {
			super.validate(groupId, classNameId, classPK, urlTitle);

			return;
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			FriendlyURLEntryLocalization.class.getName(), "urlTitle");

		String normalizedUrlTitle =
			_friendlyURLNormalizer.normalizeWithEncoding(urlTitle);

		if (normalizedUrlTitle.length() > maxLength) {
			throw new FriendlyURLLengthException(
				urlTitle + " is longer than " + maxLength);
		}

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchFriendlyURLEntryLocalization(
				groupId, classNameId, normalizedUrlTitle);

		if ((friendlyURLEntryLocalization == null) ||
			(friendlyURLEntryLocalization.getClassPK() == classPK)) {

			return;
		}

		Layout layout1 = _layoutLocalService.fetchLayout(classPK);
		Layout layout2 = _layoutLocalService.fetchLayout(
			friendlyURLEntryLocalization.getClassPK());

		if (layout1.isPrivateLayout() == layout2.isPrivateLayout()) {
			throw new DuplicateFriendlyURLEntryException(
				friendlyURLEntryLocalization.toString());
		}
	}

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}