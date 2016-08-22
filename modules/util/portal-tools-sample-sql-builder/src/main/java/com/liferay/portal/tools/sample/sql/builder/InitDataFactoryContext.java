package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;


import java.text.Format;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @author Lily Chi
 */
public class InitDataFactoryContext {
	public static void initContext(Properties properties) {
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
	}

	public static String getAssetPublisherQueryName() {
		return _assetPublisherQueryName;
	}

	public static int getMaxAssetCategoryCount() {
		return _maxAssetCategoryCount;
	}

	public static int getMaxAssetEntryToAssetCategoryCount() {
		return _maxAssetEntryToAssetCategoryCount;
	}

	public static int getMaxAssetEntryToAssetTagCount() {
		return _maxAssetEntryToAssetTagCount;
	}

	public static int getMaxAssetPublisherPageCount() {
		return _maxAssetPublisherPageCount;
	}

	public static int getMaxAssetTagCount() {
		return _maxAssetTagCount;
	}

	public static int getMaxAssetVocabularyCount() {
		return _maxAssetVocabularyCount;
	}

	public static int getMaxBlogsEntryCommentCount() {
		return _maxBlogsEntryCommentCount;
	}

	public static int getMaxBlogsEntryCount() {
		return _maxBlogsEntryCount;
	}

	public static int getMaxDDLCustomFieldCount() {
		return _maxDDLCustomFieldCount;
	}

	public static int getMaxDDLRecordCount() {
		return _maxDDLRecordCount;
	}

	public static int getMaxDDLRecordSetCount() {
		return _maxDDLRecordSetCount;
	}

	public static int getMaxDLFileEntryCount() {
		return _maxDLFileEntryCount;
	}

	public static int getMaxDLFileEntrySize() {
		return _maxDLFileEntrySize;
	}

	public static int getMaxDLFolderCount() {
		return _maxDLFolderCount;
	}

	public static int getMaxDLFolderDepth() {
		return _maxDLFolderDepth;
	}

	public static int getMaxGroupsCount() {
		return _maxGroupsCount;
	}

	public static int getMaxJournalArticleCount() {
		return _maxJournalArticleCount;
	}

	public static int getMaxJournalArticlePageCount() {
		return _maxJournalArticlePageCount;
	}

	public static int getMaxJournalArticleVersionCount() {
		return _maxJournalArticleVersionCount;
	}

	public static int getMaxMBCategoryCount() {
		return _maxMBCategoryCount;
	}

	public static int getMaxMBMessageCount() {
		return _maxMBMessageCount;
	}

	public static int getMaxMBThreadCount() {
		return _maxMBThreadCount;
	}

	public static int getMaxUserCount() {
		return _maxUserCount;
	}

	public static int getMaxUserToGroupCount() {
		return _maxUserToGroupCount;
	}

	public static int getMaxWikiNodeCount() {
		return _maxWikiNodeCount;
	}

	public static int getMaxWikiPageCommentCount() {
		return _maxWikiPageCommentCount;
	}

	public static int getMaxWikiPageCount() {
		return _maxWikiPageCount;
	}

	public static Format getSimpleDateFormat() {
		return _simpleDateFormat;
	}
	
	
	private static String _assetPublisherQueryName;
	private static int _maxAssetCategoryCount;
	private static int _maxAssetEntryToAssetCategoryCount;
	private static int _maxAssetEntryToAssetTagCount;
	private static int _maxAssetPublisherPageCount;
	private static int _maxAssetTagCount;
	private static int _maxAssetVocabularyCount;
	private static int _maxBlogsEntryCommentCount;
	private static int _maxBlogsEntryCount;
	private static int _maxDDLCustomFieldCount;
	private static int _maxDDLRecordCount;
	private static int _maxDDLRecordSetCount;
	private static int _maxDLFileEntryCount;
	private static int _maxDLFileEntrySize;
	private static int _maxDLFolderCount;
	private static int _maxDLFolderDepth;
	private static int _maxGroupsCount;
	private static int _maxJournalArticleCount;
	private static int _maxJournalArticlePageCount;
	private static int _maxJournalArticleVersionCount;
	private static int _maxMBCategoryCount;
	private static int _maxMBMessageCount;
	private static int _maxMBThreadCount;
	private static int _maxUserCount;
	private static int _maxUserToGroupCount;
	private static int _maxWikiNodeCount;
	private static int _maxWikiPageCommentCount;
	private static int _maxWikiPageCount;
	private static Format _simpleDateFormat =
		FastDateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
