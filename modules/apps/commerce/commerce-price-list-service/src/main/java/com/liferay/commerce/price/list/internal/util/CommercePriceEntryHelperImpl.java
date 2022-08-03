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

package com.liferay.commerce.price.list.internal.util;

import com.liferay.commerce.price.list.helper.CommercePriceEntryHelper;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.service.persistence.CommercePriceEntryPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dante Wang
 */
@Component(enabled = false, service = AopService.class)
public class CommercePriceEntryHelperImpl
	implements AopService, CommercePriceEntryHelper {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePriceEntry setHasTierPrice(
			long commercePriceEntryId, boolean hasTierPrice)
		throws PortalException {

		return setHasTierPrice(commercePriceEntryId, hasTierPrice, true);
	}

	@Override
	public CommercePriceEntry setHasTierPrice(
			long commercePriceEntryId, boolean hasTierPrice,
			boolean bulkPricing)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		commercePriceEntry.setHasTierPrice(hasTierPrice);
		commercePriceEntry.setBulkPricing(bulkPricing);

		return _commercePriceEntryPersistence.update(commercePriceEntry);
	}

	@Reference
	private CommercePriceEntryPersistence _commercePriceEntryPersistence;

}