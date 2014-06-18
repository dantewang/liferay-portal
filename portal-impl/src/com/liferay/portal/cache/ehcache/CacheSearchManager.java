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

package com.liferay.portal.cache.ehcache;

import java.io.IOException;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.document.Field;
import org.apache.lucene.search.Query;

/**
 * @author Tina Tian
 */
public class CacheSearchManager {

	public static void addDocument(String name, String key, Field ... fields)
		throws IOException {

		_instance._addDocument(name, key, fields);
	}

	public static void clear(String name) throws IOException {
		_instance._clear(name);
	}

	public static void close() throws IOException {
		_instance._close();
	}

	public static void close(String name) throws IOException {
		_instance._close(name);
	}

	public static void getIndexAccessor(String name) throws IOException {
		_instance._getIndexAccessor(name);
	}

	public static void removeDocument(String name, String key)
		throws IOException {

		_instance._removeDocument(name, key);
	}

	public static Set<String> search(String name, Query query)
		throws IOException {

		return _instance._search(name, query);
	}

	private void _addDocument(String name, String key, Field... fields)
		throws IOException {

		CacheIndexAccessor indexAccessor = _indexAccessors.get(name);

		if (indexAccessor == null) {
			synchronized(this) {
				indexAccessor = _indexAccessors.get(name);

				if (indexAccessor == null) {
					indexAccessor = new CacheIndexAccessor();

					_indexAccessors.put(name, indexAccessor);
				}
			}
		}

		indexAccessor.addDocument(key, fields);
	}

	private void _clear(String name) throws IOException {
		CacheIndexAccessor indexAccessor = _indexAccessors.get(name);

		if (indexAccessor != null) {
			indexAccessor.clear();
		}
	}

	private void _close() throws IOException {
		for (CacheIndexAccessor indexAccessor : _indexAccessors.values()) {
			indexAccessor.close();
		}
	}

	private void _close(String name) throws IOException {
		CacheIndexAccessor indexAccessor = _indexAccessors.get(name);

		if (indexAccessor != null) {
			indexAccessor.close();
		}
	}

	private CacheIndexAccessor _getIndexAccessor(String name)
		throws IOException {

		CacheIndexAccessor indexAccessor = _indexAccessors.get(name);

		if (indexAccessor == null) {
			synchronized(this) {
				indexAccessor = _indexAccessors.get(name);

				if (indexAccessor == null) {
					indexAccessor = new CacheIndexAccessor();

					_indexAccessors.put(name, indexAccessor);
				}
			}
		}

		return indexAccessor;
	}

	private void _removeDocument(String name, String key) throws IOException {
		CacheIndexAccessor indexAccessor = _indexAccessors.get(name);

		if (indexAccessor != null) {
			indexAccessor.removeDocument(key);
		}
	}

	private Set<String> _search(String name, Query query) throws IOException {
		CacheIndexAccessor indexAccessor = _indexAccessors.get(name);

		if (indexAccessor == null) {
			return Collections.emptySet();
		}

		return indexAccessor.search(query);
	}

	private static CacheSearchManager _instance = new CacheSearchManager();

	private Map<String, CacheIndexAccessor> _indexAccessors =
		new ConcurrentHashMap<String, CacheIndexAccessor>();

}