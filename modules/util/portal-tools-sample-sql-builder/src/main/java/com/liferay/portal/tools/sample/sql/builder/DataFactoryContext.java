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

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.OutputStreamWriter;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedWriter;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.UserPersonalSite;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.ClassNameModelImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.model.WikiPage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;

import java.time.ZoneId;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @author Lily Chi
 */
public class DataFactoryContext {

	public DataFactoryContext(Properties properties) throws Exception {
		_initContextValue(properties);

		_counter = new SimpleCounter(_maxGroupsCount + 1);

		_classNameModels = _initClassNameModels();

		_companyId = _counter.get();
		_defaultUserId = _counter.get();
		_sampleUserId = _counter.get();
	}

	public void closeCSVWriters() throws IOException {
		for (Writer writer : _csvWriters.values()) {
			writer.close();
		}
	}

	public ClassNameModel getClassNameModel(String className) {
		return _classNameModels.get(className);
	}

	public Collection<ClassNameModel> getClassNameModels() {
		return _classNameModels.values();
	}

	public long getCompanyId() {
		return _companyId;
	}

	public SimpleCounter getCounter() {
		return _counter;
	}

	public Writer getCSVWriter(String csvFileName) {
		Writer writer = _csvWriters.get(csvFileName);

		if (writer == null) {
			throw new IllegalArgumentException(
				"Unknown CSV file name: " + csvFileName);
		}

		return writer;
	}

	public long getDefaultUserId() {
		return _defaultUserId;
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

	public int getMaxCPDefinitionCount() {
		return _maxCPDefinitionCount;
	}

	public int getMaxCPInstanceCount() {
		return _maxCPInstanceCount;
	}

	public int getMaxCProductCount() {
		return _maxCProductCount;
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

	public int getMaxGroupCount() {
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

	public String getVirtualHostname() {
		return _virtualHostname;
	}

	private String _getMBDiscussionCombinedClassName(Class<?> clazz) {
		return StringBundler.concat(
			MBDiscussion.class.getName(), StringPool.UNDERLINE,
			clazz.getName());
	}

	private Map<String, ClassNameModel> _initClassNameModels() {
		Map<String, ClassNameModel> classNameModels = new HashMap<>();

		List<String> models = ModelHintsUtil.getModels();

		models.add(UserPersonalSite.class.getName());

		models.add(_getMBDiscussionCombinedClassName(BlogsEntry.class));
		models.add(_getMBDiscussionCombinedClassName(WikiPage.class));

		for (String model : models) {
			ClassNameModel classNameModel = new ClassNameModelImpl();

			long classNameId = _counter.get();

			classNameModel.setClassNameId(classNameId);

			classNameModel.setValue(model);

			classNameModels.put(model, classNameModel);
		}

		return classNameModels;
	}

	private void _initContextValue(Properties properties)
		throws FileNotFoundException {

		String timeZoneId = properties.getProperty("sample.sql.db.time.zone");

		if (Validator.isNotNull(timeZoneId)) {
			TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(timeZoneId)));
		}
		else {
			TimeZone timeZone = TimeZone.getDefault();

			timeZoneId = timeZone.getID();

			properties.setProperty("sample.sql.db.time.zone", timeZoneId);
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
		_maxCPDefinitionCount = GetterUtil.getInteger(
			properties.getProperty(
				"sample.sql.max.commerce.product.definition.count"));
		_maxCPInstanceCount = GetterUtil.getInteger(
			properties.getProperty(
				"sample.sql.max.commerce.product.instance.count"));
		_maxCProductCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.commerce.product.count"));
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
		_virtualHostname = properties.getProperty(
			"sample.sql.virtual.hostname");

		File outputDir = new File(
			properties.getProperty("sample.sql.output.dir"));

		outputDir.mkdirs();

		String[] csvFileNames = StringUtil.split(
			properties.getProperty("sample.sql.output.csv.file.names"));

		for (String csvFileName : csvFileNames) {
			_csvWriters.put(
				csvFileName,
				new UnsyncBufferedWriter(
					new OutputStreamWriter(
						new FileOutputStream(
							new File(outputDir, csvFileName.concat(".csv")))),
					_WRITER_BUFFER_SIZE) {

					@Override
					public void flush() {

						// Disable FreeMarker from flushing

					}

				});
		}
	}

	private static final int _WRITER_BUFFER_SIZE = 16 * 1024;

	private final Map<String, ClassNameModel> _classNameModels;
	private final long _companyId;
	private final SimpleCounter _counter;
	private final Map<String, Writer> _csvWriters = new HashMap<>();
	private final long _defaultUserId;
	private int _maxAssetCategoryCount;
	private int _maxAssetEntryToAssetCategoryCount;
	private int _maxAssetEntryToAssetTagCount;
	private int _maxAssetPublisherPageCount;
	private int _maxAssetTagCount;
	private int _maxAssetVocabularyCount;
	private int _maxBlogsEntryCommentCount;
	private int _maxBlogsEntryCount;
	private int _maxCPDefinitionCount;
	private int _maxCPInstanceCount;
	private int _maxCProductCount;
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
	private String _virtualHostname;

}