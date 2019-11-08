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
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.model.impl.ClassNameModelImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.model.WikiPage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public abstract class BaseDataFactory {

	public static void closeCSVWriters() throws IOException {
		for (Writer writer : _csvWriters.values()) {
			writer.close();
		}
	}

	public static Writer getCSVWriter(String csvFileName) {
		Writer writer = _csvWriters.get(csvFileName);

		if (writer == null) {
			throw new IllegalArgumentException(
				"Unknown CSV file name: " + csvFileName);
		}

		return writer;
	}

	public long getClassNameId(Class<?> clazz) {
		ClassNameModel classNameModel = getClassNameModel(clazz.getName());

		return classNameModel.getClassNameId();
	}

	public ClassNameModel getClassNameModel(String className) {
		return classNameModels.get(className);
	}

	public Collection<ClassNameModel> getClassNameModels() {
		return classNameModels.values();
	}

	public int getMaxAssetPublisherPageCount() {
		return PropsValues.MAX_ASSETPUBLISHER_PAGE_COUNT;
	}

	public int getMaxBlogsEntryCommentCount() {
		return PropsValues.MAX_BLOGS_ENTRY_COMMENT_COUNT;
	}

	public int getMaxDDLRecordCount() {
		return PropsValues.MAX_DDL_RECORDER_COUNT;
	}

	public int getMaxDDLRecordSetCount() {
		return PropsValues.MAX_DDL_RECORDER_SET_COUNT;
	}

	public int getMaxDLFolderDepth() {
		return PropsValues.MAX_DL_FOLDER_DEPTH;
	}

	public int getMaxGroupCount() {
		return PropsValues.MAX_GROUP_COUNT;
	}

	public int getMaxJournalArticleCount() {
		return PropsValues.MAX_JOURNAL_ARTICLE_COUNT;
	}

	public int getMaxJournalArticlePageCount() {
		return PropsValues.MAX_JOURNAL_ARTICLE_PAGE_COUNT;
	}

	public int getMaxJournalArticleVersionCount() {
		return PropsValues.MAX_JOURNAL_ARTICLE_VERSION_COUNT;
	}

	public int getMaxWikiPageCommentCount() {
		return PropsValues.MAX_WIKI_PAGE_COMMENT_COUNT;
	}

	public InputStream getResourceInputStream(String resourceName) {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			_DEPENDENCIES_DIR + resourceName);
	}

	public Date nextFutureDate() {
		return new Date(_FUTURE_TIME + (_FUTURE_COUNTER.get() * Time.SECOND));
	}

	protected BaseDataFactory() {
		counter = new SimpleCounter(PropsValues.MAX_GROUP_COUNT + 1);
		classNameModels = _initClassNameModels();
		companyId = counter.get();
		defaultUserId = counter.get();
		sampleUserId = counter.get();
	}

	protected final Map<String, ClassNameModel> classNameModels;
	protected final long companyId;
	protected final SimpleCounter counter;
	protected final long defaultUserId;
	protected final long sampleUserId;

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

			long classNameId = counter.get();

			classNameModel.setClassNameId(classNameId);

			classNameModel.setValue(model);

			classNameModels.put(model, classNameModel);
		}

		return classNameModels;
	}

	private static final String _DEPENDENCIES_DIR =
		"com/liferay/portal/tools/sample/sql/builder/dependencies/";

	private static final SimpleCounter _FUTURE_COUNTER = new SimpleCounter();

	private static final long _FUTURE_TIME =
		System.currentTimeMillis() + Time.YEAR;

	private static final int _WRITER_BUFFER_SIZE = 16 * 1024;

	private static final Map<String, Writer> _csvWriters = new HashMap<>();

	static {
		File outputDir = new File(PropsValues.OUTPUT_DIR);

		outputDir.mkdirs();

		String[] csvFileNames = PropsValues.CSV_NAMES;

		for (String csvFileName : csvFileNames) {
			try {
				_csvWriters.put(
					csvFileName,
					new UnsyncBufferedWriter(
						new OutputStreamWriter(
							new FileOutputStream(
								new File(
									outputDir, csvFileName.concat(".csv")))),
						_WRITER_BUFFER_SIZE) {

						@Override
						public void flush() {

							// Disable FreeMarker from flushing

						}

					});
			}
			catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			}
		}
	}

}