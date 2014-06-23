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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.memory.MemoryIndex;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;

/**
 * @author Tina Tian
 */
public class CacheIndexAccessor {

	public void addDocument(String key, Field ... fields) throws IOException {
		MemoryIndex memoryIndex = new MemoryIndex();

		for (Field field : fields) {
			memoryIndex.addField(
				field.name(), _keywordTokenStream(field.stringValue()));
		}

		_indexes.put(key, memoryIndex.createSearcher());
	}

	public void clear() throws IOException {
		_indexes.clear();
	}

	public void close() throws IOException {
		_indexes.clear();
	}

	public void removeDocument(String key) throws IOException {
		_indexes.remove(key);
	}

	public Set<String> search(Query query) {
		if (query == null) {
			throw new IllegalArgumentException("query must not be null");
		}

		Set<String> result = new HashSet<String>();

		for (Map.Entry<String, IndexSearcher> entry : _indexes.entrySet()) {
			IndexSearcher indexSearcher = entry.getValue();

			if (search(indexSearcher, query) > 0.0f) {
				result.add(entry.getKey());
			}
		}

		return result;
	}

	private <T> TokenStream _keywordTokenStream(final T keyword) {
		if (keyword == null) {
			throw new IllegalArgumentException("keywords must not be null");
		}

		return new TokenStream() {
			private final CharTermAttribute termAtt = addAttribute(
				CharTermAttribute.class);
			private final OffsetAttribute offsetAtt = addAttribute(
				OffsetAttribute.class);

			@Override
			public boolean incrementToken() {
				if (offsetAtt.endOffset()> 0) {
					return false;
				}

				String term = keyword.toString();

				clearAttributes();

				termAtt.setEmpty().append(term);

				offsetAtt.setOffset(0, termAtt.length());

				return true;
			}
		};
	}

	private float search(IndexSearcher searcher, Query query) {
		try {
			final float[] scores = new float[1]; // inits to 0.0f (no match)

			searcher.search(
				query,
				new Collector() {

					private Scorer scorer;

					@Override
					public void collect(int doc) throws IOException {
						scores[0] = scorer.score();
					}

					@Override
					public void setScorer(Scorer scorer) {
						this.scorer = scorer;
					}

					@Override
					public boolean acceptsDocsOutOfOrder() {
						return true;
					}

					@Override
					public void setNextReader(IndexReader reader, int i) {
					}

				});

			return scores[0];
		}
		catch (IOException e) { // can never happen (RAMDirectory)
			throw new RuntimeException(e);
		}
		finally {

		  // searcher.close();

		/*
		   * Note that it is harmless and important for good performance to
		   * NOT close the index reader!!! This avoids all sorts of
		   * unnecessary baggage and locking in the Lucene IndexReader
		   * superclass, all of which is completely unnecessary for this main
		   * memory index data structure without thread-safety claims.
		   *
		   * Wishing IndexReader would be an interface...
		   *
		   * Actually with the new tight createSearcher() API auto-closing is now
		   * made impossible, hence searcher.close() would be harmless and also
		   * would not degrade performance...
		   */
		}
	}

	private Map<String, IndexSearcher> _indexes =
		new ConcurrentHashMap<String, IndexSearcher>();

}