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

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.model.CounterModel;
import com.liferay.counter.model.impl.CounterModelImpl;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.social.kernel.model.SocialActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lily Chi
 */
public class CounterDataFactory extends BaseDataFactory {

	public long getCounterNext() {
		return counter.get();
	}

	public List<Integer> getSequence(int size) {
		List<Integer> sequence = new ArrayList<>(size);

		for (int i = 1; i <= size; i++) {
			sequence.add(i);
		}

		return sequence;
	}

	public List<CounterModel> newCounterModels() {
		List<CounterModel> counterModels = new ArrayList<>();

		// Counter

		CounterModel counterModel = new CounterModelImpl();

		counterModel.setName(Counter.class.getName());
		counterModel.setCurrentId(counter.get());

		counterModels.add(counterModel);

		// FriendlyURLEntryLocalization

		counterModel = new CounterModelImpl();

		counterModel.setName(FriendlyURLEntryLocalization.class.getName());
		counterModel.setCurrentId(counter.get());

		counterModels.add(counterModel);

		// ResourcePermission

		counterModel = new CounterModelImpl();

		counterModel.setName(ResourcePermission.class.getName());
		counterModel.setCurrentId(resourcePermissionCounter.get());

		counterModels.add(counterModel);

		// SocialActivity

		counterModel = new CounterModelImpl();

		counterModel.setName(SocialActivity.class.getName());
		counterModel.setCurrentId(socialActivityCounter.get());

		counterModels.add(counterModel);

		return counterModels;
	}

}