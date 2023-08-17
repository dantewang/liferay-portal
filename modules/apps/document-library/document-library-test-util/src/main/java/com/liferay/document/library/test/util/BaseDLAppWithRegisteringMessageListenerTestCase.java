/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.test.util;

import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.messaging.MessageListener;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jiaxu Wei
 */
public abstract class BaseDLAppWithRegisteringMessageListenerTestCase
	extends BaseDLAppTestCase {

	protected AtomicInteger registerDLSyncEventProcessorMessageListener(
		final String targetEvent) {

		final AtomicInteger counter = new AtomicInteger();

		Destination destination = MessageBusUtil.getDestination(
			DestinationNames.DOCUMENT_LIBRARY_SYNC_EVENT_PROCESSOR);

		destination.register(
			new MessageListener() {

				@Override
				public void receive(Message message) {
					Object event = message.get("event");

					if (targetEvent.equals(event)) {
						counter.incrementAndGet();
					}
				}

			});

		return counter;
	}

}