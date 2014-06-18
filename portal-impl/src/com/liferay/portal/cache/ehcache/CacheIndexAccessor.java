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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.index.TermVectorMapper;
import org.apache.lucene.index.memory.MemoryIndex;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.store.AlreadyClosedException;

/**
 * @author Tina Tian
 */
public class CacheIndexAccessor {

	public CacheIndexAccessor() throws IOException {
		_memoryIndexSearcherManager = new MemoryIndexSearcherManager();
	}

	public void addDocument(String key, Field ... fields) throws IOException {
		MemoryIndex memoryIndex = new MemoryIndex();

		for (Field field : fields) {
			memoryIndex.addField(
				field.name(), _keywordTokenStream(field.stringValue()));
		}

		IndexSearcher indexSearcher = memoryIndex.createSearcher();

		_indexes.put(
			key,
			new LiferayMemoryIndexReader(indexSearcher.getIndexReader(), key));

		_memoryIndexSearcherManager.invalidate();
	}

	public void clear() throws IOException {
		_indexes.clear();

		_memoryIndexSearcherManager.invalidate();
	}

	public void close() throws IOException {
		_indexes.clear();

		_memoryIndexSearcherManager.invalidate();
	}

	public void removeDocument(String key) throws IOException {
		_indexes.remove(key);

		_memoryIndexSearcherManager.invalidate();
	}

	public Set<String> search(Query query) throws IOException {
		if (query == null) {
			throw new IllegalArgumentException("query must not be null");
		}

		IndexSearcher indexSearcher = _memoryIndexSearcherManager.acquire();

		final Set<String> results = new HashSet<String>();

		indexSearcher.search(
			query,
			new Collector() {

				private IndexReader _indexReader;

				@Override
				public void collect(int doc) throws IOException {
					LiferayMemoryIndexReader liferayIndexReader =
						(LiferayMemoryIndexReader)_indexReader;

					results.add(liferayIndexReader.getKey());
				}

				@Override
				public void setScorer(Scorer scorer) {
				}

				@Override
				public boolean acceptsDocsOutOfOrder() {
					return false;
				}

				@Override
				public void setNextReader(IndexReader reader, int i) {
					_indexReader = reader;
				}

			});

		return results;
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

	private Map<String, LiferayMemoryIndexReader> _indexes =
		new ConcurrentHashMap<String, LiferayMemoryIndexReader>();
	private MemoryIndexSearcherManager _memoryIndexSearcherManager;

	private class LiferayMemoryIndexReader extends IndexReader {

		public LiferayMemoryIndexReader(IndexReader indexReader, String key) {
			_indexReader = indexReader;
			_key = key;
		}

		public String getKey() {
			return _key;
		}

		public int docFreq(Term term) throws IOException {
			return _indexReader.docFreq(term);
		}

		public TermEnum terms() throws IOException {
			return _indexReader.terms();
		}

		public TermEnum terms(Term term) throws IOException {
			return _indexReader.terms(term);
		}

		public TermPositions termPositions() throws IOException {
			return _indexReader.termPositions();
		}

		public TermDocs termDocs() throws IOException {
			return _indexReader.termDocs();
		}

		public TermFreqVector[] getTermFreqVectors(int docNumber)
			throws IOException {

			return _indexReader.getTermFreqVectors(docNumber);
		}

		public void getTermFreqVector(int docNumber, TermVectorMapper mapper)
			throws IOException {

			_indexReader.getTermFreqVector(docNumber, mapper);
		}

		public void getTermFreqVector(
				int docNumber, String field, TermVectorMapper mapper)
			throws IOException {

			_indexReader.getTermFreqVector(docNumber, field, mapper);
		}

		public TermFreqVector getTermFreqVector(int docNumber, String fieldName)
			throws IOException {

			return _indexReader.getTermFreqVector(docNumber, fieldName);
		}

		public byte[] norms(String fieldName) throws IOException {
			return _indexReader.norms(fieldName);
		}

		public void norms(String fieldName, byte[] bytes, int offset)
			throws IOException {

			_indexReader.norms(fieldName, bytes, offset);
		}

		protected void doSetNorm(int doc, String fieldName, byte value)
			throws IOException {

			throw new UnsupportedOperationException();
		}

		public int numDocs() {
			return _indexReader.numDocs();
		}

		public int maxDoc() {
			return _indexReader.maxDoc();
		}

		public Document document(int n) throws IOException {
			return _indexReader.document(n);
		}

		public Document document(int n, FieldSelector fieldSelector)
			throws IOException {

			return _indexReader.document(n, fieldSelector);
		}

		public boolean isDeleted(int n) {
			return _indexReader.isDeleted(n);
		}

		public boolean hasDeletions() {
			return _indexReader.hasDeletions();
		}

		protected void doDelete(int docNum) {
			throw new UnsupportedOperationException();
		}

		protected void doUndeleteAll() {
			throw new UnsupportedOperationException();
		}

		protected void doCommit(Map<String, String> commitUserData) {
			throw new UnsupportedOperationException();
		}

		protected void doClose() {
			throw new UnsupportedOperationException();
		}

		public Collection<String> getFieldNames(
			IndexReader.FieldOption fieldOption) {

			return _indexReader.getFieldNames(fieldOption);
		}

		private String _key;
		private IndexReader _indexReader;

	}

	private class MemoryIndexSearcherManager {

		public MemoryIndexSearcherManager() throws IOException {
			_indexSearcher = _createIndexSearcher();
		}

		public IndexSearcher acquire() throws IOException {
			if (_invalid) {
				synchronized (this) {
					if (_invalid) {
						IndexSearcher indexSearcher = _indexSearcher;

						if (indexSearcher == null) {
							throw new AlreadyClosedException(
								"Index searcher manager is closed");
						}

						_indexSearcher = _createIndexSearcher();

						_invalid = false;
					}
				}
			}

			return _indexSearcher;
		}

		public synchronized void close() throws IOException {
			_indexSearcher = null;
		}

		public synchronized void invalidate() {
			_invalid = true;
		}

		private IndexSearcher _createIndexSearcher() {
			List<LiferayMemoryIndexReader> indexReaders =
				new ArrayList<LiferayMemoryIndexReader>(_indexes.values());

			IndexReader indexReader = new MultiReader(
				indexReaders.toArray(new IndexReader[0]));

			return new IndexSearcher(indexReader);
		}

		private volatile IndexSearcher _indexSearcher;
		private volatile boolean _invalid;

	}

}