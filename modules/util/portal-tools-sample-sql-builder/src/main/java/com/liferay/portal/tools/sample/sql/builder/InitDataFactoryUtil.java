package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.AccountModelImpl;
import com.liferay.portal.model.impl.ClassNameModelImpl;
import com.liferay.portal.model.impl.CompanyModelImpl;
import com.liferay.portal.model.impl.GroupModelImpl;
import com.liferay.portal.model.impl.RoleModelImpl;
import com.liferay.portal.model.impl.UserModelImpl;
import com.liferay.portal.model.impl.VirtualHostModelImpl;
import com.liferay.util.SimpleCounter;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class InitDataFactoryUtil {

	public static String getResource(Class<?> clazz, String resourceName)
		throws Exception {

		List<String> lines = new ArrayList<>();

		StringUtil.readLines(
			getResourceInputStream(clazz, resourceName), lines);

		return StringUtil.merge(lines, StringPool.SPACE);
	}

	public static InputStream getResourceInputStream(
		Class<?> clazz, String resourceName) {

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			_DEPENDENCIES_DIR + resourceName);
	}

	public static Map<String, ClassNameModel> initClassNameModels(
		SimpleCounter simpleCounter) {

		Map<String, ClassNameModel> classNameModels = new HashMap<>();
		List<String> models = ModelHintsUtil.getModels();
		SimpleCounter counter = simpleCounter;

		for (String model : models) {
			ClassNameModel classNameModel = new ClassNameModelImpl();

			long classNameId = counter.get();

			classNameModel.setClassNameId(classNameId);

			classNameModel.setValue(model);

			classNameModels.put(model, classNameModel);
		}

		return classNameModels;
	}
	
	public static AccountModel initAccountModel(
		long companyId, long accountId) {

		AccountModel accountModel = new AccountModelImpl();

		accountModel.setAccountId(accountId);
		accountModel.setCompanyId(companyId);
		accountModel.setCreateDate(new Date());
		accountModel.setModifiedDate(new Date());
		accountModel.setName("Liferay");
		accountModel.setLegalName("Liferay, Inc.");

		return accountModel;
	}

	public static CompanyModel initCompanyModel(
		long companyId, long accountId) {

		CompanyModel companyModel = new CompanyModelImpl();

		companyModel.setCompanyId(companyId);
		companyModel.setAccountId(accountId);
		companyModel.setWebId("liferay.com");
		companyModel.setMx("liferay.com");
		companyModel.setActive(true);

		return companyModel;
	}
	
	public static String initJournalArticleContent(int maxJournalArticleSize) 
	{
		StringBundler sb = new StringBundler(6);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><dynamic-element name=\"content");
		sb.append("\" type=\"text_area\" index-type=\"keyword\" index=\"0\">");
		sb.append("<dynamic-content language-id=\"en_US\"><![CDATA[");

		if (maxJournalArticleSize <= 0) {
			maxJournalArticleSize = 1;
		}

		char[] chars = new char[maxJournalArticleSize];

		for (int i = 0; i < maxJournalArticleSize; i++) {
			chars[i] = (char)(CharPool.LOWER_CASE_A + (i % 26));
		}

		sb.append(new String(chars));

		sb.append("]]></dynamic-content></dynamic-element></root>");

		return sb.toString();
	}
	
		public static String getResourcePermissionModelName(String... classNames) {
		if (ArrayUtil.isEmpty(classNames)) {
			return StringPool.BLANK;
		}

		Arrays.sort(classNames);

		StringBundler sb = new StringBundler(classNames.length * 2);

		for (String className : classNames) {
			sb.append(className);
			sb.append(StringPool.DASH);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static String nextDDLCustomFieldName(
		long groupId, int customFieldIndex) {

		StringBundler sb = new StringBundler(4);

		sb.append("custom_field_text_");
		sb.append(groupId);
		sb.append("_");
		sb.append(customFieldIndex);

		return sb.toString();
	}

	public static GroupModel newGroupModel(
			long groupId, long classNameId, long classPK, String name,
			boolean site,long companyId,long sampleUserId)
		throws Exception {

		GroupModel groupModel = new GroupModelImpl();

		groupModel.setUuid(SequentialUUID.generate());
		groupModel.setGroupId(groupId);
		groupModel.setCompanyId(companyId);
		groupModel.setCreatorUserId(sampleUserId);
		groupModel.setClassNameId(classNameId);
		groupModel.setClassPK(classPK);
		groupModel.setTreePath(
			StringPool.SLASH + groupModel.getGroupId() + StringPool.SLASH);
		groupModel.setGroupKey(name);
		groupModel.setName(name);
		groupModel.setManualMembership(true);
		groupModel.setMembershipRestriction(
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION);
		groupModel.setFriendlyURL(
			StringPool.FORWARD_SLASH +
				FriendlyURLNormalizerUtil.normalize(name));
		groupModel.setSite(site);
		groupModel.setActive(true);

		return groupModel;
	}

	public static Date nextFutureDate(SimpleCounter futureDateCounter) {
		return new Date(_FUTURE_TIME + (futureDateCounter.get() * Time.SECOND));
	}

	public static GroupModel initGroupModel(long groupId,
			long classNameId, long classPK, String name,
			boolean site,long companyId,long sampleUserId) throws Exception{

		GroupModel globalGroupModel = newGroupModel(
				groupId,classNameId,classPK,name,site,companyId,sampleUserId);
		return globalGroupModel;
	}

	public static RoleModel newRoleModel(String name, int type,long roleId,
			long companyId,long sampleUserId,String sampleUserName,
			long classNameId) {
		RoleModel roleModel = new RoleModelImpl();

		roleModel.setUuid(SequentialUUID.generate());
		roleModel.setRoleId(roleId);
		roleModel.setCompanyId(companyId);
		roleModel.setUserId(sampleUserId);
		roleModel.setUserName(sampleUserName);
		roleModel.setCreateDate(new Date());
		roleModel.setModifiedDate(new Date());
		roleModel.setClassNameId(classNameId);
		roleModel.setClassPK(roleModel.getRoleId());
		roleModel.setName(name);
		roleModel.setType(type);

		return roleModel;
	}
	
	public static long getClassNameId(Class<?> clazz,
			Map<String, ClassNameModel> classNameModels) {
		
		ClassNameModel classNameModel = classNameModels.get(clazz.getName());
		return classNameModel.getClassNameId();
	}
	
	public static List<String> initUserFirstNames(Class<?> clazz) throws IOException {
		List<String> firstNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(getResourceInputStream(
					clazz, "first_names.txt")));

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			firstNames.add(line);
		}

		unsyncBufferedReader.close();

		return firstNames;
	}

	public static List<String> initUserLastNames(Class<?> clazz) throws IOException {

		List<String> lastNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(getResourceInputStream(
					clazz, "last_names.txt")));

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			lastNames.add(line);
		}

		unsyncBufferedReader.close();

		return lastNames;
	}

	public static UserModel newUserModel(
		long userId, String firstName, String lastName, String screenName,
		boolean defaultUser,long contactId,long companyId) {

		if (Validator.isNull(screenName)) {
			screenName = String.valueOf(userId);
		}

		UserModel userModel = new UserModelImpl();

		userModel.setUuid(SequentialUUID.generate());
		userModel.setUserId(userId);
		userModel.setCompanyId(companyId);
		userModel.setCreateDate(new Date());
		userModel.setModifiedDate(new Date());
		userModel.setDefaultUser(defaultUser);
		userModel.setContactId(contactId);
		userModel.setPassword("test");
		userModel.setPasswordModifiedDate(new Date());
		userModel.setReminderQueryQuestion("What is your screen name?");
		userModel.setReminderQueryAnswer(screenName);
		userModel.setEmailAddress(screenName + "@liferay.com");
		userModel.setScreenName(screenName);
		userModel.setLanguageId("en_US");
		userModel.setGreeting("Welcome " + screenName + StringPool.EXCLAMATION);
		userModel.setFirstName(firstName);
		userModel.setLastName(lastName);
		userModel.setLoginDate(new Date());
		userModel.setLastLoginDate(new Date());
		userModel.setLastFailedLoginDate(new Date());
		userModel.setLockoutDate(new Date());
		userModel.setAgreedToTermsOfUse(true);
		userModel.setEmailAddressVerified(true);

		return userModel;
	}

	public static VirtualHostModel initVirtualHostModel(String hostname,
			long virtualHostId,long companyId) {
		VirtualHostModel virtualHostModel = new VirtualHostModelImpl();

		virtualHostModel.setVirtualHostId(virtualHostId);
		virtualHostModel.setCompanyId(companyId);
		virtualHostModel.setHostname(hostname);

		return virtualHostModel;
	}

	private static final String _DEPENDENCIES_DIR =
		"com/liferay/portal/tools/sample/sql/builder/dependencies/";

	private static final long _FUTURE_TIME =
		System.currentTimeMillis() + Time.YEAR;
}