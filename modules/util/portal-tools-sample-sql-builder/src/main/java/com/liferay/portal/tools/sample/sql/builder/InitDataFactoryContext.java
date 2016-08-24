package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import com.liferay.portlet.PortletPreferencesImpl;

import com.liferay.util.SimpleCounter;
import java.io.IOException;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @author Lily Chi
 */
public class InitDataFactoryContext {

	public static long getAccountId() {
		return _accountId;
	}

	public static String getAssetPublisherQueryName() {
		return _assetPublisherQueryName;
	}

	public static Map<String, ClassNameModel> getClassNameModels() {
		return _classNameModels;
	}

	public static long getCompanyId() {
		return _companyId;
	}

	public static SimpleCounter getCounter() {
		return _counter;
	}

	public static long getDefaultUserId() {
		return _defaultUserId;
	}

	public static SimpleCounter getFutureDateCounter() {
		return _futureDateCounter;
	}

	public static long getGlobalGroupId() {
		return _globalGroupId;
	}

	public static long getGuestGroupId() {
		return _guestGroupId;
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

	public static SimpleCounter getResourcePermissionCounter() {
		return _resourcePermissionCounter;
	}

	public static long getSampleUserId() {
		return _sampleUserId;
	}

	public static Format getSimpleDateFormat() {
		return _simpleDateFormat;
	}

	public static SimpleCounter getSocialActivityCounter() {
		return _socialActivityCounter;
	}

	public static SimpleCounter getTimeCounter() {
		return _timeCounter;
	}

	public static SimpleCounter getUserScreenNameCounter() {
		return _userScreenNameCounter;
	}

	public static String getDlDDMStructureContent() {
		return _dlDDMStructureContent;
	}

	public static String getDlDDMStructureLayoutContent() {
		return _dlDDMStructureLayoutContent;
	}

	public static String getJournalDDMStructureContent() {
		return _journalDDMStructureContent;
	}

	public static String getJournalDDMStructureLayoutContent() {
		return _journalDDMStructureLayoutContent;
	}

	public static PortletPreferencesImpl getDefaultAssetPublisherPortletPreference() {
		return _defaultAssetPublisherPortletPreference;
	}

	public static AccountModel getAccountModel() {
		return _accountModel;
	}

	public static CompanyModel getCompanyModel() {
		return _companyModel;
	}

	public static String getJournalArticleContent() {
		return _journalArticleContent;
	}

	public static List<String> getFirstNames() {
		return _firstNames;
	}

	public static List<String> getLastNames() {
		return _lastNames;
	}

	public static GroupModel getGlobalGroupModel() {
		return _globalGroupModel;
	}

	public static GroupModel getGuestGroupModel() {
		return _guestGroupModel;
	}

	public static List<GroupModel> getGroupModels() {
		return _groupModels;
	}

	public static UserModel getDefaultUserModel() {
		return _defaultUserModel;
	}

	public static UserModel getGuestUserModel() {
		return _guestUserModel;
	}

	public static UserModel getSampleUserModel() {
		return _sampleUserModel;
	}

	public static VirtualHostModel getVirtualHostModel() {
		return _virtualHostModel;
	}

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
		
		int maxJournalArticleSize = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.journal.article.size"));

		_journalArticleContent = InitDataFactoryUtil.initJournalArticleContent(
			maxJournalArticleSize);
		
		_virtualHostModel = InitDataFactoryUtil.initVirtualHostModel(
				properties.getProperty("sample.sql.virtual.hostname"),
				_counter.get(),_companyId);
	}

	public static void initParameter() {
		_classNameModels = InitDataFactoryUtil.initClassNameModels(_counter);
		_accountId = _counter.get();
		_companyId = _counter.get();
		_defaultUserId = _counter.get();
		_globalGroupId = _counter.get();
		_guestGroupId = _counter.get();
		_sampleUserId = _counter.get();
	}
	
	public static void initResource(Class<?> clazz,PortletPreferencesFactory portletPreferencesFactory) throws Exception{
		_dlDDMStructureContent = InitDataFactoryUtil.getResource(
			clazz, "ddm_structure_basic_document.json");
		_dlDDMStructureLayoutContent = InitDataFactoryUtil.getResource(
			clazz, "ddm_structure_layout_basic_document.json");
		_journalDDMStructureContent = InitDataFactoryUtil.getResource(
			clazz, "ddm_structure_basic_web_content.json");
		_journalDDMStructureLayoutContent = InitDataFactoryUtil.getResource(
			clazz, "ddm_structure_layout_basic_web_content.json");

		String defaultAssetPublisherPreference = StringUtil.read(
			InitDataFactoryUtil.getResourceInputStream(
				clazz, "default_asset_publisher_preference.xml"));

		_defaultAssetPublisherPortletPreference =
			(PortletPreferencesImpl)portletPreferencesFactory.fromDefaultXML(
				defaultAssetPublisherPreference);
	}
	
	public static void initCompanyModels(){
		_companyModel = InitDataFactoryUtil.initCompanyModel(
					_companyId,_accountId);
		_accountModel = InitDataFactoryUtil.initAccountModel(
			_companyId,_accountId);
	}
	
	public static void initUserNames(Class<?> clazz) throws IOException{
		_firstNames = InitDataFactoryUtil.initUserFirstNames(clazz);

		_lastNames = InitDataFactoryUtil.initUserLastNames(clazz);
	}
	
	public static void initGroupModels() throws Exception {
		_globalGroupModel = InitDataFactoryUtil.initGroupModel(
				_globalGroupId,InitDataFactoryUtil.getClassNameId(
						Company.class,_classNameModels),_companyId, 
					GroupConstants.GLOBAL, false,_companyId,
					_sampleUserId);

		_guestGroupModel = InitDataFactoryUtil.initGroupModel(
			_guestGroupId, InitDataFactoryUtil.getGroupClassNameId(),
			_guestGroupId, GroupConstants.GUEST,
			true, _companyId,_sampleUserId);

		_groupModels = new ArrayList<>(
			InitDataFactoryContext.getMaxGroupsCount());

		for (int i = 1; i <= InitDataFactoryContext.getMaxGroupsCount(); i++) {
			GroupModel groupModel = InitDataFactoryUtil.initGroupModel(
				i, InitDataFactoryUtil.getGroupClassNameId(), i, "Site " + i, true,
				_companyId, _sampleUserId);
				_groupModels.add(groupModel);
		}
	}
	
	public static void initUserModels(String userName){
				
		_defaultUserModel = InitDataFactoryUtil.newUserModel(_defaultUserId, 
				StringPool.BLANK,StringPool.BLANK, StringPool.BLANK, true,
				_counter.get(),	_companyId);

		_guestUserModel = InitDataFactoryUtil.newUserModel(_counter.get(),
				"Test", "Test", "Test",false, _counter.get(),_companyId);

		_sampleUserModel = InitDataFactoryUtil.newUserModel(_sampleUserId,  
				userName, userName, userName, false,_counter.get(),_companyId);

	}

	private static long _accountId;
	private static String _assetPublisherQueryName;
	private static Map<String, ClassNameModel> _classNameModels;
	private static long _companyId;
	private static final SimpleCounter _counter;
	private static long _defaultUserId;
	private static final SimpleCounter _futureDateCounter;
	private static long _globalGroupId;
	private static long _guestGroupId;
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
	private static final SimpleCounter _resourcePermissionCounter;
	private static long _sampleUserId;
	private static Format _simpleDateFormat =
		FastDateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleCounter _socialActivityCounter;
	private static final SimpleCounter _timeCounter;
	private static final SimpleCounter _userScreenNameCounter;
	private static String _dlDDMStructureContent;
	private static String _dlDDMStructureLayoutContent;
	private static String _journalDDMStructureContent;
	private static String _journalDDMStructureLayoutContent;
	private static PortletPreferencesImpl
		_defaultAssetPublisherPortletPreference;
	private static AccountModel _accountModel;
	private static CompanyModel _companyModel;
	private static String _journalArticleContent;
	private static List<String> _firstNames;
	private static List<String> _lastNames;
	private static GroupModel _globalGroupModel;
	private static GroupModel _guestGroupModel;
	private static List<GroupModel> _groupModels;
	private static UserModel _defaultUserModel;
	private static UserModel _guestUserModel;
	private static UserModel _sampleUserModel;
	private static VirtualHostModel _virtualHostModel;
	static {
		_counter = new SimpleCounter(
			InitDataFactoryContext.getMaxGroupsCount() + 1);
		_timeCounter = new SimpleCounter();
		_futureDateCounter = new SimpleCounter();
		_resourcePermissionCounter = new SimpleCounter();
		_socialActivityCounter = new SimpleCounter();
		_userScreenNameCounter = new SimpleCounter();
	}

}