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

package com.liferay.bean.portlet.cdi.extension.internal;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * @author Dante Wang
 */
public class InjectionTargetWrapper<T> implements InjectionTarget<T> {

	public InjectionTargetWrapper(InjectionTarget<T> injectionTarget) {
		_injectionTarget = injectionTarget;
	}

	@Override
	public void dispose(T instance) {
		_injectionTarget.dispose(instance);
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		return _injectionTarget.getInjectionPoints();
	}

	@Override
	public void inject(T instance, CreationalContext<T> ctx) {
		_injectionTarget.inject(instance, ctx);
	}

	@Override
	public void postConstruct(T instance) {
		_injectionTarget.postConstruct(instance);
	}

	@Override
	public void preDestroy(T instance) {
		_injectionTarget.preDestroy(instance);

		_callback.run();
	}

	@Override
	public T produce(CreationalContext<T> creationalContext) {
		return _injectionTarget.produce(creationalContext);
	}

	public void setCallback(Runnable callback) {
		_callback = callback;
	}

	private Runnable _callback;
	private final InjectionTarget<T> _injectionTarget;

}