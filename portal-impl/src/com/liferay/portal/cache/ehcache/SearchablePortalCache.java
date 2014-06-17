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

import com.liferay.portal.kernel.cache.IndexedFieldExtractor;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheWrapper;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Field;
import org.apache.lucene.index.FieldInfo;

/**
 * @author Tina Tian
 */
public class SearchablePortalCache <K extends Serializable, V>
	extends PortalCacheWrapper<K, V> {

	public static final String FIELD_UID = "key";

	public SearchablePortalCache(
		PortalCache<K, V> portalCache,
		IndexedFieldExtractor<K, V> indexedFieldExtractor) {

		super(portalCache);

		_indexedFiledExtractor = indexedFieldExtractor;
	}

	@Override
	public void put(K key, V value) {
		doPut(key, value, false, -1);
	}

	@Override
	public void put(K key, V value, int timeToLive) {
		if (timeToLive < 0) {
			throw new IllegalArgumentException("Time to live is negative");
		}

		doPut(key, value, false, timeToLive);
	}

	@Override
	public void putQuiet(K key, V value) {
		doPut(key, value, true, -1);
	}

	@Override
	public void putQuiet(K key, V value, int timeToLive) {
		if (timeToLive < 0) {
			throw new IllegalArgumentException("Time to live is negative");
		}

		doPut(key, value, true, timeToLive);
	}

	@Override
	public void remove(K key) {
		portalCache.remove(key);

		try {
			CacheSearchManager.removeDocument(getName(), key.toString());
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void removeAll() {
		portalCache.removeAll();

		try {
			CacheSearchManager.clear(getName());
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	protected void doPut(K key, V value, boolean quiet, int timeToLive) {
		if (key == null) {
			throw new IllegalArgumentException("Key is null");
		}

		if (value == null) {
			throw new IllegalArgumentException("Value is null");
		}

		if (quiet) {
			if (timeToLive >= 0) {
				portalCache.putQuiet(key, value, timeToLive);
			}
			else {
				portalCache.putQuiet(key, value);
			}
		}
		else {
			if (timeToLive >= 0) {
				portalCache.put(key, value, timeToLive);
			}
			else {
				portalCache.put(key, value);
			}
		}

		Map<String, String> indexedFields =
			_indexedFiledExtractor.getIndexedFields(key, value);

		List<Field> fields = new ArrayList<Field>();

		for (Map.Entry<String, String> entry : indexedFields.entrySet()) {
			Field field = new Field(
				entry.getKey(), entry.getValue(), Field.Store.NO,
				Field.Index.NOT_ANALYZED_NO_NORMS);

			field.setIndexOptions(FieldInfo.IndexOptions.DOCS_ONLY);

			fields.add(field);
		}

		try {
			CacheSearchManager.addDocument(
				getName(), String.valueOf(key), fields.toArray(new Field[0]));
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private IndexedFieldExtractor<K, V> _indexedFiledExtractor;

}