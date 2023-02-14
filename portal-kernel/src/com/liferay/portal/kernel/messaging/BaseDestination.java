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

package com.liferay.portal.kernel.messaging;

import com.liferay.petra.string.StringPool;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael C. Han
 * @author Shuyang Zhou
 * @author Brian Wing Shun Chan
 */
public abstract class BaseDestination implements Destination {

	@Override
	public void close() {
		close(false);
	}

	@Override
	public void close(boolean force) {
	}

	@Override
	public void copyMessageListeners(Destination destination) {
		for (MessageListener messageListener : messageListeners) {
			InvokerMessageListener invokerMessageListener =
				(InvokerMessageListener)messageListener;

			destination.register(
				invokerMessageListener.getMessageListener(),
				invokerMessageListener.getClassLoader());
		}
	}

	@Override
	public void destroy() {
		close(true);

		unregisterMessageListeners();
	}

	@Override
	public DestinationStatistics getDestinationStatistics() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDestinationType() {
		return _destinationType;
	}

	@Override
	public int getMessageListenerCount() {
		return messageListeners.size();
	}

	@Override
	public Set<MessageListener> getMessageListeners() {
		return Collections.unmodifiableSet(messageListeners);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isRegistered() {
		if (getMessageListenerCount() > 0) {
			return true;
		}

		return false;
	}

	@Override
	public void open() {
	}

	@Override
	public boolean register(MessageListener messageListener) {
		InvokerMessageListener invokerMessageListener =
			new InvokerMessageListener(messageListener);

		return registerMessageListener(invokerMessageListener);
	}

	@Override
	public boolean register(
		MessageListener messageListener, ClassLoader classLoader) {

		InvokerMessageListener invokerMessageListener =
			new InvokerMessageListener(messageListener, classLoader);

		return registerMessageListener(invokerMessageListener);
	}

	@Override
	public void send(Message message) {
		throw new UnsupportedOperationException();
	}

	public void setDestinationType(String destinationType) {
		_destinationType = destinationType;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean unregister(MessageListener messageListener) {
		InvokerMessageListener invokerMessageListener =
			new InvokerMessageListener(messageListener);

		return unregisterMessageListener(invokerMessageListener);
	}

	public boolean unregister(
		MessageListener messageListener, ClassLoader classLoader) {

		InvokerMessageListener invokerMessageListener =
			new InvokerMessageListener(messageListener, classLoader);

		return unregisterMessageListener(invokerMessageListener);
	}

	@Override
	public void unregisterMessageListeners() {
		for (MessageListener messageListener : messageListeners) {
			unregisterMessageListener((InvokerMessageListener)messageListener);
		}
	}

	protected boolean registerMessageListener(
		InvokerMessageListener invokerMessageListener) {

		return messageListeners.add(invokerMessageListener);
	}

	protected boolean unregisterMessageListener(
		InvokerMessageListener invokerMessageListener) {

		return messageListeners.remove(invokerMessageListener);
	}

	protected Set<MessageListener> messageListeners = Collections.newSetFromMap(
		new ConcurrentHashMap<>());
	protected String name = StringPool.BLANK;

	private String _destinationType;

}