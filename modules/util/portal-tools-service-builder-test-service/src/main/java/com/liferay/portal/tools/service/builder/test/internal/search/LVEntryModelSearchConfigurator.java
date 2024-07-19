/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.internal.search;

import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchConfigurator;
import com.liferay.portal.tools.service.builder.test.model.LVEntry;

import org.osgi.service.component.annotations.Component;

/**
 * @author Dante Wang
 */
@Component(service = ModelSearchConfigurator.class)
public class LVEntryModelSearchConfigurator
	implements ModelSearchConfigurator<LVEntry> {

	@Override
	public String getClassName() {
		return LVEntry.class.getName();
	}

	@Override
	public ModelIndexerWriterContributor<LVEntry>
		getModelIndexerWriterContributor() {

		return _modelIndexWriterContributor;
	}

	private final ModelIndexerWriterContributor<LVEntry>
		_modelIndexWriterContributor = ProxyFactory.newDummyInstance(
			ModelIndexerWriterContributor.class);

}