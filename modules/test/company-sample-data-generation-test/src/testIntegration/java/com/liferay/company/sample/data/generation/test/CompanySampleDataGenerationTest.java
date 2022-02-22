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

package com.liferay.company.sample.data.generation.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.product.constants.CommerceCatalogConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.increment.BufferedIncrementThreadLocal;
import com.liferay.portal.kernel.messaging.proxy.ProxyModeThreadLocal;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsUtil;

import java.io.BufferedWriter;

import java.net.InetSocketAddress;

import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Hai Yu
 */
@DataGuard(scope = DataGuard.Scope.CLASS)
@RunWith(Arquillian.class)
public class CompanySampleDataGenerationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_originalAtomicReference = ReflectionTestUtil.getFieldValue(
			_portal, "_portalServerInetSocketAddress");

		ReflectionTestUtil.setFieldValue(
			_portal, "_portalServerInetSocketAddress",
			new AtomicReference<InetSocketAddress>());

		Runtime runtime = Runtime.getRuntime();

		_executorService = Executors.newFixedThreadPool(
			runtime.availableProcessors());
	}

	@After
	public void tearDown() {
		_executorService.shutdownNow();

		ReflectionTestUtil.setFieldValue(
			_portal, "_portalServerInetSocketAddress",
			_originalAtomicReference);
	}

	@Test
	public void testGenerateSampleData() throws Exception {
		int originalCompaniesCount = _companyLocalService.getCompaniesCount();

		List<Future<Void>> futures = new ArrayList<>();

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			for (int i = 1; i <= _COMPANY_COUNT; i++) {
				int companyIndex = i;

				futures.add(
					_executorService.submit(
						() -> {
							BufferedIncrementThreadLocal.setWithSafeCloseable(
								true);
							ProxyModeThreadLocal.setWithSafeCloseable(true);

							_addCompany(companyIndex);

							return null;
						}));
			}

			for (Future<Void> future : futures) {
				future.get();
			}
		}

		Assert.assertEquals(
			"Company count should be " +
				(_COMPANY_COUNT + originalCompaniesCount),
			_COMPANY_COUNT + originalCompaniesCount,
			_companyLocalService.getCompaniesCount());

		_exportCSVs();
	}

	private void _addCompany(int companyIndex) throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer(
				String.valueOf(companyIndex))) {

			String webId = _generateCompanyWebId(companyIndex);

			// Add company

			Company company = _companyLocalService.addCompany(
				null, webId, webId, webId, false, 0, true);

			PortalInstances.initCompany(
				ServletContextPool.get(StringPool.BLANK), webId);

			// Add user

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setWithSafeCloseable(
						company.getCompanyId())) {

				int originalCompanyUsersCount =
					_userLocalService.getCompanyUsersCount(
						company.getCompanyId());

				_addUsers(
					companyIndex, company.getCompanyId(), company.getGroupId(),
					webId);

				Assert.assertEquals(
					StringBundler.concat(
						"User count for ", webId, " should be ",
						_USER_PER_COMPANY_COUNT + originalCompanyUsersCount),
					_USER_PER_COMPANY_COUNT + originalCompanyUsersCount,
					_userLocalService.getCompanyUsersCount(
						company.getCompanyId()));
			}
		}
	}

	private void _addUsers(
			int companyIndex, long companyId, long groupId, String webId)
		throws Exception {

		String middleName = StringPool.BLANK;
		long prefixId = 0;
		long suffixId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;
		long[] organizationIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = false;

		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.ADMINISTRATOR);

		long userStartIndex = (companyIndex * _USER_PER_COMPANY_COUNT) + 1;
		long userEndIndex = (companyIndex + 1) * _USER_PER_COMPANY_COUNT;

		for (long i = userStartIndex; i <= userEndIndex; i++) {
			String screenName = _generateUserScreenName(i);

			String firstName = screenName;
			String lastName = screenName;

			String emailAddress = screenName + StringPool.AT + webId;

			User user = _userLocalService.addUser(
				0, companyId, false, "test", "test", false, screenName,
				emailAddress, LocaleUtil.US, firstName, middleName, lastName,
				prefixId, suffixId, male, birthdayMonth, birthdayDay,
				birthdayYear, jobTitle, new long[] {groupId}, organizationIds,
				new long[] {role.getRoleId()}, userGroupIds, sendEmail,
				_getServiceContext(companyId));

			user.setLoginDate(new Date());
			user.setLastLoginDate(new Date());
			user.setLockoutDate(new Date());
			user.setAgreedToTermsOfUse(true);
			user.setEmailAddressVerified(true);
			user.setPasswordModified(true);
			user.setPasswordReset(false);
			user.setReminderQueryQuestion("What is your screen name?");
			user.setReminderQueryAnswer(screenName);

			_userLocalService.updateUser(user);

			List<String> userScreenNames = _csvMap.computeIfAbsent(
				webId, key -> new ArrayList<>());

			userScreenNames.add(screenName);
		}
	}

	private void _exportByCompany(Company company) throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setWithSafeCloseable(
					company.getCompanyId())) {

			_exportCompanyTableData(company);

			_exportCommerceCurrencyTableData(company.getCompanyId());

			_exportDDMStructureVersionTableData(company.getCompanyId());

			_exportDDMTemplateTableData(company.getCompanyId());

			_exportDefaultUserId(company.getCompanyId());

			_exportGroupTableData(company.getCompanyId());

			_exportRoleTableData(company.getCompanyId());
		}
	}

	private void _exportClassNameTableData() throws Exception {
		List<ClassName> classNames = _classNameLocalService.getClassNames(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
				_outputDirPath.resolve(_CLASS_NAME_TABLE_CSV))) {

			for (ClassName className : classNames) {
				bufferedWriter.append(
					String.valueOf(className.getClassNameId()));
				bufferedWriter.append(StringPool.COMMA);
				bufferedWriter.append(className.getValue());
				bufferedWriter.newLine();
			}
		}
	}

	private void _exportCommerceCurrencyTableData(long companyId)
		throws Exception {

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.getCommerceCurrency(
				companyId,
				CommerceCatalogConstants.MASTER_COMMERCE_DEFAULT_CURRENCY);

		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
				_outputDirPath.resolve(_COMMERCE_CURRENCY_TABLE_CSV),
				_openOptions)) {

			bufferedWriter.append(String.valueOf(companyId));
			bufferedWriter.append(StringPool.COMMA);
			bufferedWriter.append(
				String.valueOf(commerceCurrency.getCommerceCurrencyId()));
			bufferedWriter.newLine();
		}
	}

	private void _exportCompanyTableData(Company company) throws Exception {
		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
				_outputDirPath.resolve(_COMPANY_TABLE_CSV), _openOptions)) {

			bufferedWriter.append(String.valueOf(company.getCompanyId()));
			bufferedWriter.append(StringPool.COMMA);
			bufferedWriter.append(company.getWebId());
			bufferedWriter.newLine();
		}
	}

	private void _exportCounterTableData() throws Exception {
		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select name, currentId from Counter")) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
						_outputDirPath.resolve(_COUNTER_TABLE_CSV))) {

					while (resultSet.next()) {
						String name = resultSet.getString("name");
						long currentId = resultSet.getLong("currentId");

						bufferedWriter.append(name);
						bufferedWriter.append(StringPool.COMMA);
						bufferedWriter.append(String.valueOf(currentId));
						bufferedWriter.newLine();
					}
				}
			}
		}
	}

	private void _exportCSVs() throws Exception {
		String outputDir = PropsUtil.get("sample.data.output.dir");

		if (Validator.isNull(outputDir)) {
			return;
		}

		_outputDirPath = Paths.get(
			PropsUtil.get(PropsKeys.LIFERAY_HOME), outputDir);

		if (Files.exists(_outputDirPath)) {
			FileUtil.deltree(_outputDirPath.toFile());
		}

		Files.createDirectories(_outputDirPath);

		try (LoggingTimer loggingTimer = new LoggingTimer(
				_outputDirPath.toString())) {

			_exportByCompany(
				_companyLocalService.getCompanyByWebId(_DEFAULT_COMPANY_WEBID));

			List<String> keys = new ArrayList<>(_csvMap.keySet());

			Collections.sort(keys);

			for (String key : keys) {
				_exportByCompany(_companyLocalService.getCompanyByWebId(key));

				_exportHost(key);

				_exportUserData(key);
			}

			_exportClassNameTableData();

			_exportCounterTableData();
		}
	}

	private void _exportDDMStructureVersionTableData(long companyId)
		throws Exception {

		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
				_outputDirPath.resolve(_DDM_STRUCTURE_VERSION_TABLE_CSV),
				_openOptions)) {

			for (DDMStructure ddmStructure :
					_dDMStructureLocalService.getStructures()) {

				String structureKey = ddmStructure.getStructureKey();

				if (structureKey.equals(_JOURNAL_STRUCTURE_KEY)) {
					DDMStructureVersion ddmStructureVersion =
						_dDMStructureVersionLocalService.getStructureVersion(
							ddmStructure.getStructureId(),
							DDMStructureConstants.VERSION_DEFAULT);

					bufferedWriter.append(String.valueOf(companyId));
					bufferedWriter.append(StringPool.COMMA);
					bufferedWriter.append(
						String.valueOf(ddmStructureVersion.getPrimaryKey()));
					bufferedWriter.newLine();
				}
			}
		}
	}

	private void _exportDDMTemplateTableData(long companyId) throws Exception {
		Group globalGroup = _groupLocalService.getGroup(
			companyId, String.valueOf(companyId));

		List<DDMTemplate> ddmTemplates = _dDMTemplateLocalService.getTemplates(
			globalGroup.getGroupId(),
			_classNameLocalService.getClassNameId(DDMStructure.class));

		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
				_outputDirPath.resolve(_DDM_TEMPLATE_TABLE_CSV),
				_openOptions)) {

			for (DDMTemplate ddmTemplate : ddmTemplates) {
				bufferedWriter.append(
					String.valueOf(ddmTemplate.getCompanyId()));
				bufferedWriter.append(StringPool.COMMA);
				bufferedWriter.append(
					String.valueOf(ddmTemplate.getTemplateId()));
				bufferedWriter.newLine();
			}
		}
	}

	private void _exportDefaultUserId(long companyId) throws Exception {
		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
				_outputDirPath.resolve(_DEFAULT_USER_ID_CSV), _openOptions)) {

			bufferedWriter.append(String.valueOf(companyId));
			bufferedWriter.append(StringPool.COMMA);
			bufferedWriter.append(
				String.valueOf(_userLocalService.getDefaultUserId(companyId)));
			bufferedWriter.newLine();
		}
	}

	private void _exportGroupTableData(long companyId) throws Exception {
		Set<String> expectedGroupKeyNames = new HashSet<>(
			Arrays.asList(GroupConstants.GUEST, String.valueOf(companyId)));

		List<Group> groups = _groupLocalService.getCompanyGroups(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
				_outputDirPath.resolve(_GROUP_TABLE_CSV), _openOptions)) {

			for (Group group : groups) {
				String groupKey = group.getGroupKey();

				if (expectedGroupKeyNames.contains(groupKey)) {
					bufferedWriter.append(String.valueOf(companyId));
					bufferedWriter.append(StringPool.COMMA);
					bufferedWriter.append(String.valueOf(group.getGroupId()));
					bufferedWriter.append(StringPool.COMMA);

					if (groupKey.equals(String.valueOf(companyId))) {
						bufferedWriter.append(GroupConstants.GLOBAL);
					}
					else {
						bufferedWriter.append(groupKey);
					}

					bufferedWriter.newLine();
				}
			}
		}
	}

	private void _exportHost(String hostName) throws Exception {
		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
				_outputDirPath.resolve(_HOST_CSV), _openOptions)) {

			bufferedWriter.append(_PORTAL_SERVER_IP_ADDRESS);
			bufferedWriter.append(StringPool.SPACE);
			bufferedWriter.append(hostName);
			bufferedWriter.newLine();
		}
	}

	private void _exportRoleTableData(long companyId) throws Exception {
		Set<String> unexpectedRoleNames = new HashSet<>(
			Arrays.asList(
				RoleConstants.ANALYTICS_ADMINISTRATOR,
				RoleConstants.PUBLICATIONS_USER,
				DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER,
				DepotRolesConstants.ASSET_LIBRARY_MEMBER));

		List<Role> roles = _roleLocalService.getRoles(
			companyId,
			new int[] {
				RoleConstants.TYPE_REGULAR, RoleConstants.TYPE_SITE,
				RoleConstants.TYPE_ORGANIZATION, RoleConstants.TYPE_DEPOT
			});

		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
				_outputDirPath.resolve(_ROLE_TABLE_CSV), _openOptions)) {

			for (Role role : roles) {
				if (!unexpectedRoleNames.contains(role.getName())) {
					bufferedWriter.append(String.valueOf(companyId));
					bufferedWriter.append(StringPool.COMMA);
					bufferedWriter.append(String.valueOf(role.getRoleId()));
					bufferedWriter.append(StringPool.COMMA);
					bufferedWriter.append(role.getName());
					bufferedWriter.newLine();
				}
			}
		}
	}

	private void _exportUserData(String hostName) throws Exception {
		List<String> screenNames = _csvMap.get(hostName);

		Collections.sort(screenNames);

		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
				_outputDirPath.resolve(_USER_CSV), _openOptions)) {

			for (String screenName : screenNames) {
				bufferedWriter.append(hostName);
				bufferedWriter.append(StringPool.COMMA);
				bufferedWriter.append(screenName);
				bufferedWriter.newLine();
			}
		}
	}

	private String _generateCompanyWebId(int companyIndex) {
		return "liferay" + companyIndex + ".com";
	}

	private String _generateUserScreenName(long userIndex) {
		return "test" + userIndex;
	}

	private ServiceContext _getServiceContext(long companyId) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setCompanyId(companyId);

		return serviceContext;
	}

	private static final String _CLASS_NAME_TABLE_CSV = "classNameTable.csv";

	private static final String _COMMERCE_CURRENCY_TABLE_CSV =
		"commerceCurrencyTable.csv";

	private static final int _COMPANY_COUNT = GetterUtil.get(
		PropsUtil.get("sample.data.company.count"), 2);

	private static final String _COMPANY_TABLE_CSV = "companyTable.csv";

	private static final String _COUNTER_TABLE_CSV = "counterTable.csv";

	private static final String _DDM_STRUCTURE_VERSION_TABLE_CSV =
		"ddmStructureVersionTable.csv";

	private static final String _DDM_TEMPLATE_TABLE_CSV =
		"ddmTemplateTable.csv";

	private static final String _DEFAULT_COMPANY_WEBID = "liferay.com";

	private static final String _DEFAULT_USER_ID_CSV = "defaultUserId.csv";

	private static final String _GROUP_TABLE_CSV = "groupTable.csv";

	private static final String _HOST_CSV = "host.csv";

	private static final String _JOURNAL_STRUCTURE_KEY = "BASIC-WEB-CONTENT";

	private static final String _PORTAL_SERVER_IP_ADDRESS = GetterUtil.get(
		PropsUtil.get("sample.data.portal.server.ip.address"), "127.0.0.1");

	private static final String _ROLE_TABLE_CSV = "roleTable.csv";

	private static final String _USER_CSV = "user.csv";

	private static final int _USER_PER_COMPANY_COUNT = GetterUtil.get(
		PropsUtil.get("sample.data.user.per.company.count"), 2);

	private static final OpenOption[] _openOptions = {
		StandardOpenOption.CREATE, StandardOpenOption.APPEND
	};

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	private final Map<String, List<String>> _csvMap = new ConcurrentHashMap<>();

	@Inject
	private DDMStructureLocalService _dDMStructureLocalService;

	@Inject
	private DDMStructureVersionLocalService _dDMStructureVersionLocalService;

	@Inject
	private DDMTemplateLocalService _dDMTemplateLocalService;

	private ExecutorService _executorService;

	@Inject
	private GroupLocalService _groupLocalService;

	private AtomicReference<InetSocketAddress> _originalAtomicReference;
	private Path _outputDirPath;

	@Inject
	private Portal _portal;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}