/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.test.util;

import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.test.rule.Inject;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;

/**
 * @author Jiaxu Wei
 */
public abstract class BaseDLAppWithRegisteringMessageListenerTestCase
	extends BaseDLAppTestCase {

	@After
	public void tearDown() throws Exception {
		if (_messageListener != null) {
			Destination destination = _messageBus.getDestination(
				DestinationNames.DOCUMENT_LIBRARY_SYNC_EVENT_PROCESSOR);

			destination.unregister(_messageListener);

			_messageListener = null;
		}

		super.tearDown();
	}

	protected AtomicInteger registerDLSyncEventProcessorMessageListener(
		final String targetEvent) {

		final AtomicInteger counter = new AtomicInteger();

		_messageListener = new MessageListener() {

			@Override
			public void receive(Message message) {
				Object event = message.get("event");

				if (targetEvent.equals(event)) {
					counter.incrementAndGet();
				}
			}

		};

		Destination destination = _messageBus.getDestination(
			DestinationNames.DOCUMENT_LIBRARY_SYNC_EVENT_PROCESSOR);

		destination.register(_messageListener);

		return counter;
	}

	@Inject
	private MessageBus _messageBus;

	private MessageListener _messageListener;

}