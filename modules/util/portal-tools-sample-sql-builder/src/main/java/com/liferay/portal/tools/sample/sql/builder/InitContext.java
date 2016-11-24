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

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.ClassNameModelImpl;
import com.liferay.util.SimpleCounter;

import java.text.Format;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @author Lily Chi
 */
public class InitContext {

	public InitContext(Properties properties) throws Exception {
		_initContextValue(properties);

		_counter = new SimpleCounter(_maxGroupsCount + 1);

		_classNameModels = _initClassNameModels();

		_accountId = _counter.get();
		_companyId = _counter.get();
		_defaultUserId = _counter.get();
		_sampleUserId = _counter.get();
	}

	public long getAccountId() {
		return _accountId;
	}

	public String getAssetPublisherQueryName() {
		return _assetPublisherQueryName;
	}

	public Map<String, ClassNameModel> getClassNameModels() {
		return _classNameModels;
	}

	public Collection<ClassNameModel> getClassNameModelValues() {
		return _classNameModels.values();
	}

	public long getCompanyId() {
		return _companyId;
	}

	public SimpleCounter getCounter() {
		return _counter;
	}

	public long getDefaultUserId() {
		return _defaultUserId;
	}

	public SimpleCounter getFutureDateCounter() {
		return _futureDateCounter;
	}

	public int getMaxAssetCategoryCount() {
		return _maxAssetCategoryCount;
	}

	public int getMaxAssetEntryToAssetCategoryCount() {
		return _maxAssetEntryToAssetCategoryCount;
	}

	public int getMaxAssetEntryToAssetTagCount() {
		return _maxAssetEntryToAssetTagCount;
	}

	public int getMaxAssetPublisherPageCount() {
		return _maxAssetPublisherPageCount;
	}

	public int getMaxAssetTagCount() {
		return _maxAssetTagCount;
	}

	public int getMaxAssetVocabularyCount() {
		return _maxAssetVocabularyCount;
	}

	public int getMaxBlogsEntryCommentCount() {
		return _maxBlogsEntryCommentCount;
	}

	public int getMaxBlogsEntryCount() {
		return _maxBlogsEntryCount;
	}

	public int getMaxDDLCustomFieldCount() {
		return _maxDDLCustomFieldCount;
	}

	public int getMaxDDLRecordCount() {
		return _maxDDLRecordCount;
	}

	public int getMaxDDLRecordSetCount() {
		return _maxDDLRecordSetCount;
	}

	public int getMaxDLFileEntryCount() {
		return _maxDLFileEntryCount;
	}

	public int getMaxDLFileEntrySize() {
		return _maxDLFileEntrySize;
	}

	public int getMaxDLFolderCount() {
		return _maxDLFolderCount;
	}

	public int getMaxDLFolderDepth() {
		return _maxDLFolderDepth;
	}

	public int getMaxGroupsCount() {
		return _maxGroupsCount;
	}

	public int getMaxJournalArticleCount() {
		return _maxJournalArticleCount;
	}

	public int getMaxJournalArticlePageCount() {
		return _maxJournalArticlePageCount;
	}

	public int getMaxJournalArticleSize() {
		return _maxJournalArticleSize;
	}

	public int getMaxJournalArticleVersionCount() {
		return _maxJournalArticleVersionCount;
	}

	public int getMaxMBCategoryCount() {
		return _maxMBCategoryCount;
	}

	public int getMaxMBMessageCount() {
		return _maxMBMessageCount;
	}

	public int getMaxMBThreadCount() {
		return _maxMBThreadCount;
	}

	public int getMaxUserCount() {
		return _maxUserCount;
	}

	public int getMaxUserToGroupCount() {
		return _maxUserToGroupCount;
	}

	public int getMaxWikiNodeCount() {
		return _maxWikiNodeCount;
	}

	public int getMaxWikiPageCommentCount() {
		return _maxWikiPageCommentCount;
	}

	public int getMaxWikiPageCount() {
		return _maxWikiPageCount;
	}

	public long getSampleUserId() {
		return _sampleUserId;
	}

	public Format getSimpleDateFormat() {
		return _simpleDateFormat;
	}

	public String getVirtualHostname() {
		return _virtualHostname;
	}

	private Map<String, ClassNameModel> _initClassNameModels() {
		Map<String, ClassNameModel> classNameModels = new HashMap<>();
		List<String> models = ModelHintsUtil.getModels();

		for (String model : models) {
			ClassNameModel classNameModel = new ClassNameModelImpl();

			long classNameId = _counter.get();

			classNameModel.setClassNameId(classNameId);

			classNameModel.setValue(model);

			classNameModels.put(model, classNameModel);
		}

		return classNameModels;
	}

	private void _initContextValue(Properties properties) {
		String timeZoneId = properties.getProperty("sample.sql.db.time.zone");

		if (Validator.isNotNull(timeZoneId)) {
			TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

			if (timeZone != null) {
				TimeZone.setDefault(timeZone);

				_simpleDateFormat =
					FastDateFormatFactoryUtil.getSimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss", timeZone);
			}
		}

		_assetPublisherQueryName = GetterUtil.getString(
			properties.getProperty("sample.sql.asset.publisher.query.name"));

		if (!_assetPublisherQueryName.equals("assetCategories")) {
			_assetPublisherQueryName = "assetTags";
		}

		_maxAssetCategoryCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.asset.category.count"));
		_maxAssetEntryToAssetCategoryCount = GetterUtil.getInteger(
			properties.getProperty(
				"sample.sql.max.asset.entry.to.asset.category.count"));
		_maxAssetEntryToAssetTagCount = GetterUtil.getInteger(
			properties.getProperty(
				"sample.sql.max.asset.entry.to.asset.tag.count"));
		_maxAssetPublisherPageCount = GetterUtil.getInteger(
			properties.getProperty(
				"sample.sql.max.asset.publisher.page.count"));
		_maxAssetTagCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.asset.tag.count"));
		_maxAssetVocabularyCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.asset.vocabulary.count"));
		_maxBlogsEntryCommentCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.blogs.entry.comment.count"));
		_maxBlogsEntryCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.blogs.entry.count"));
		_maxDDLCustomFieldCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.ddl.custom.field.count"));
		_maxDDLRecordCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.ddl.record.count"));
		_maxDDLRecordSetCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.ddl.record.set.count"));
		_maxDLFileEntryCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.dl.file.entry.count"));
		_maxDLFileEntrySize = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.dl.file.entry.size"));
		_maxDLFolderCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.dl.folder.count"));
		_maxDLFolderDepth = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.dl.folder.depth"));
		_maxGroupsCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.group.count"));
		_maxJournalArticleCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.journal.article.count"));
		_maxJournalArticlePageCount = GetterUtil.getInteger(
			properties.getProperty(
				"sample.sql.max.journal.article.page.count"));
		_maxJournalArticleSize = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.journal.article.size"));
		_maxJournalArticleVersionCount = GetterUtil.getInteger(
			properties.getProperty(
				"sample.sql.max.journal.article.version.count"));
		_maxMBCategoryCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.mb.category.count"));
		_maxMBMessageCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.mb.message.count"));
		_maxMBThreadCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.mb.thread.count"));
		_maxUserCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.user.count"));
		_maxUserToGroupCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.user.to.group.count"));
		_maxWikiNodeCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.wiki.node.count"));
		_maxWikiPageCommentCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.wiki.page.comment.count"));
		_maxWikiPageCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.wiki.page.count"));
		_virtualHostname = GetterUtil.getString(
			properties.getProperty("sample.sql.virtual.hostname"));
	}

	private final long _accountId;
	private String _assetPublisherQueryName;
	private final Map<String, ClassNameModel> _classNameModels;
	private final long _companyId;
	private final SimpleCounter _counter;
	private final long _defaultUserId;
	private final SimpleCounter _futureDateCounter = new SimpleCounter();
	private int _maxAssetCategoryCount;
	private int _maxAssetEntryToAssetCategoryCount;
	private int _maxAssetEntryToAssetTagCount;
	private int _maxAssetPublisherPageCount;
	private int _maxAssetTagCount;
	private int _maxAssetVocabularyCount;
	private int _maxBlogsEntryCommentCount;
	private int _maxBlogsEntryCount;
	private int _maxDDLCustomFieldCount;
	private int _maxDDLRecordCount;
	private int _maxDDLRecordSetCount;
	private int _maxDLFileEntryCount;
	private int _maxDLFileEntrySize;
	private int _maxDLFolderCount;
	private int _maxDLFolderDepth;
	private int _maxGroupsCount;
	private int _maxJournalArticleCount;
	private int _maxJournalArticlePageCount;
	private int _maxJournalArticleSize;
	private int _maxJournalArticleVersionCount;
	private int _maxMBCategoryCount;
	private int _maxMBMessageCount;
	private int _maxMBThreadCount;
	private int _maxUserCount;
	private int _maxUserToGroupCount;
	private int _maxWikiNodeCount;
	private int _maxWikiPageCommentCount;
	private int _maxWikiPageCount;
	private final long _sampleUserId;
	private Format _simpleDateFormat =
		FastDateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String _virtualHostname;

}