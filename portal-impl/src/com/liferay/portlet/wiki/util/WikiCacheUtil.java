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

package com.liferay.portlet.wiki.util;

import com.liferay.portal.cache.ehcache.CacheSearchManager;
import com.liferay.portal.cache.ehcache.SearchablePortalCache;
import com.liferay.portal.kernel.cache.IndexedFieldExtractor;
import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.wiki.PageContentException;
import com.liferay.portlet.wiki.model.WikiPage;
import com.liferay.portlet.wiki.model.WikiPageDisplay;
import com.liferay.portlet.wiki.service.WikiPageLocalServiceUtil;

import java.io.IOException;
import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletURL;

import org.apache.commons.lang.time.StopWatch;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * @author Jorge Ferrer
 */
public class WikiCacheUtil {

	public static void clearCache(long nodeId) {
//		_portalCache.removeAll();

		Term term = new Term("nodeId", StringUtil.toHexString(nodeId));

		Query query = new TermQuery(term);

		try {
			Set<String> keys = CacheSearchManager.search(_CACHE_NAME, query);

			for (String key : keys) {
				_portalCache.remove(key);
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void clearCache(long nodeId, String title) {
//		clearCache(nodeId);

		Term term1 = new Term("nodeId", StringUtil.toHexString(nodeId));
		Term term2 = new Term("title", title);

		Query query1 = new TermQuery(term1);
		Query query2 = new TermQuery(term2);

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(query1, BooleanClause.Occur.MUST);
		booleanQuery.add(query2, BooleanClause.Occur.MUST);

		try {
			Set<String> keys = CacheSearchManager.search(
				_CACHE_NAME, booleanQuery);

			for (String key : keys) {
				_portalCache.remove(key);
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static WikiPageDisplay getDisplay(
		long nodeId, String title, PortletURL viewPageURL,
		PortletURL editPageURL, String attachmentURLPrefix) {

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		String key = _encodeKey(nodeId, title, viewPageURL.toString());

		WikiPageDisplay pageDisplay = (WikiPageDisplay)_portalCache.get(key);

		if (pageDisplay == null) {
			pageDisplay = _getPageDisplay(
				nodeId, title, viewPageURL, editPageURL, attachmentURLPrefix);

			_portalCache.put(key, pageDisplay);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"getDisplay for {" + nodeId + ", " + title + ", " +
					viewPageURL + ", " + editPageURL + "} takes " +
						stopWatch.getTime() + " ms");
		}

		return pageDisplay;
	}

	public static Map<String, Boolean> getOutgoingLinks(WikiPage page)
		throws PageContentException {

		String key = _encodeKey(
			page.getNodeId(), page.getTitle(), _OUTGOING_LINKS);

		Map<String, Boolean> links = (Map<String, Boolean>)_portalCache.get(
			key);

		if (links == null) {
			links = WikiUtil.getLinks(page);

			_portalCache.put(key, (Serializable)links);
		}

		return links;
	}

	private static String _encodeKey(
		long nodeId, String title, String postfix) {

		StringBundler sb = new StringBundler(7);

		sb.append(_CACHE_NAME);
		sb.append(StringPool.POUND);
		sb.append(StringUtil.toHexString(nodeId));
		sb.append(StringPool.POUND);
		sb.append(title);

		if (postfix != null) {
			sb.append(StringPool.POUND);
			sb.append(postfix);
		}

		return sb.toString();
	}

	private static String _getNodeId(String key) {
		int index1 = key.indexOf(StringPool.POUND);
		int index2 = key.indexOf(StringPool.POUND, index1 + 1);

		return key.substring(index1 + 1, index2);
	}

	private static WikiPageDisplay _getPageDisplay(
		long nodeId, String title, PortletURL viewPageURL,
		PortletURL editPageURL, String attachmentURLPrefix) {

		try {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Get page display for {" + nodeId + ", " + title + ", " +
						viewPageURL + ", " + editPageURL + "}");
			}

			return WikiPageLocalServiceUtil.getPageDisplay(
				nodeId, title, viewPageURL, editPageURL, attachmentURLPrefix);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get page display for {" + nodeId + ", " + title +
						", " + viewPageURL + ", " + editPageURL + "}");
			}

			return null;
		}
	}

	private static String _getTitle(String key) {
		int index1 = key.indexOf(StringPool.POUND);
		int index2 = key.indexOf(StringPool.POUND, index1 + 1);
		int index3 = key.indexOf(StringPool.POUND, index2 + 1);

		if (index3 < 0) {
			return key.substring(index2 + 1);
		}
		else {
			return key.substring(index2 + 1, index3);
		}
	}

	private static final String _CACHE_NAME = WikiCacheUtil.class.getName();

	private static final String _OUTGOING_LINKS = "OUTGOING_LINKS";

	private static Log _log = LogFactoryUtil.getLog(WikiCacheUtil.class);

	private static PortalCache<String, Serializable> _portalCache;

	static {
		_portalCache = MultiVMPoolUtil.getCache(_CACHE_NAME);

		_portalCache = new SearchablePortalCache<String, Serializable>(
			_portalCache,
			new IndexedFieldExtractor<String, Serializable>() {

				@Override
				public Map<String, String> getIndexedFields(
					String key, Serializable value) {

					Map<String, String> indexedFields =
						new HashMap<String, String>();

					indexedFields.put("nodeId", _getNodeId(key));
					indexedFields.put("title", _getTitle(key));

					return indexedFields;
				}

			});
	}

}