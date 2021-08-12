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

package com.liferay.portal.web.internal.session.replication;

import com.liferay.petra.io.Deserializer;
import com.liferay.petra.io.Serializer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpSessionWrapper;

import java.io.Serializable;

import java.nio.ByteBuffer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

/**
 * @author Dante Wang
 */
public class SessionReplicationHttpSessionWrapper extends HttpSessionWrapper {

	public SessionReplicationHttpSessionWrapper(HttpSession session) {
		super(session);
	}

	@Override
	public Object getAttribute(String name) {
		Object value = super.getAttribute(
			_SERIALIZED_ATTRIBUTE_PREFIX.concat(name));

		if (value == null) {
			return super.getAttribute(name);
		}

		Deserializer deserializer = new Deserializer(
			ByteBuffer.wrap((byte[])value));

		try {
			return deserializer.readObject();
		}
		catch (ClassNotFoundException classNotFoundException) {
			_log.error("Unable to deserialize object", classNotFoundException);

			return null;
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (!(value instanceof Serializable) ||
			_safeClasses.contains(value.getClass())) {

			super.setAttribute(name, value);
		}

		// TODO: also skip classes loaded from App Server's class loader or
		//  JDK's class loader??

		Serializer serializer = new Serializer();

		serializer.writeObject((Serializable)value);

		ByteBuffer byteBuffer = serializer.toByteBuffer();

		super.setAttribute(
			_SERIALIZED_ATTRIBUTE_PREFIX.concat(name), byteBuffer.array());
	}

	private static final String _SERIALIZED_ATTRIBUTE_PREFIX =
		SessionReplicationHttpSessionWrapper.class.getName() + "_";

	private static final Log _log = LogFactoryUtil.getLog(
		SessionReplicationHttpSessionWrapper.class);

	private static final Set<Class<?>> _safeClasses = new HashSet<>(
		Arrays.asList(
			Boolean.class, Byte.class, Character.class, Double.class,
			Float.class, Integer.class, Long.class, Short.class, String.class));

}