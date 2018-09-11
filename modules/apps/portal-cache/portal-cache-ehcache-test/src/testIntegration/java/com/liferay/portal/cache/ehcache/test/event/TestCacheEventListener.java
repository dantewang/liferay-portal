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

package com.liferay.portal.cache.ehcache.test.event;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

/**
 * @author Dante Wang
 */
public class TestCacheEventListener implements CacheEventListener {

	@Override
	public Object clone() throws CloneNotSupportedException {
		return null;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void notifyElementEvicted(Ehcache cache, Element element) {
	}

	@Override
	public void notifyElementExpired(Ehcache cache, Element element) {
	}

	@Override
	public void notifyElementPut(Ehcache cache, Element element)
		throws CacheException {
	}

	@Override
	public void notifyElementRemoved(Ehcache cache, Element element)
		throws CacheException {
	}

	@Override
	public void notifyElementUpdated(Ehcache cache, Element element)
		throws CacheException {
	}

	@Override
	public void notifyRemoveAll(Ehcache cache) {
	}

}