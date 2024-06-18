/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.messaging;

import com.liferay.adaptive.media.web.internal.configuration.AMConfiguration;
import com.liferay.adaptive.media.web.internal.constants.AMDestinationNames;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.messaging.DestinationDefinition;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	configurationPid = "com.liferay.adaptive.media.web.internal.configuration.AMConfiguration",
	property = "destination.name=" + AMDestinationNames.ADAPTIVE_MEDIA_PROCESSOR,
	service = DestinationDefinition.class
)
public class AMDestinationDefinition implements DestinationDefinition {

	@Override
	public String getDestinationName() {
		return AMDestinationNames.ADAPTIVE_MEDIA_PROCESSOR;
	}

	@Override
	public String getDestinationType() {
		return DESTINATION_TYPE_PARALLEL;
	}

	@Override
	public int getWorkersCoreSize() {
		return _amConfiguration.workersCoreSize();
	}

	@Override
	public int getWorkersMaxSize() {
		return _amConfiguration.workersMaxSize();
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_amConfiguration = ConfigurableUtil.createConfigurable(
			AMConfiguration.class, properties);
	}

	private volatile AMConfiguration _amConfiguration;

}