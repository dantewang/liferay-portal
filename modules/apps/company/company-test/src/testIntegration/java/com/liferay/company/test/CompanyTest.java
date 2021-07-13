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

package com.liferay.company.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.LayoutCopyHelper;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Hai Yu
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class CompanyTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_executorService = Executors.newWorkStealingPool();

		File file = new File("portal-web/docroot");

		_mockServletContext = new MockServletContext(
			"file:" + file.getAbsolutePath(), new FileSystemResourceLoader());
	}

	@After
	public void tearDown() throws Exception {
		_executorService.shutdownNow();

		if (System.getenv("JENKINS_HOME") != null) {
			_companyLocalService.forEachCompany(
				company -> {
					String webId = company.getWebId();

					if (webId.equals(PropsValues.COMPANY_DEFAULT_WEB_ID)) {
						return;
					}

					_companyLocalService.deleteCompany(company);
				});
		}
	}

	@Test
	public void testAddCompanyAndUserData() throws Exception {
		Assert.assertTrue(_companyLocalService.getCompaniesCount() == 1);

		List<Future<Void>> futures = new ArrayList<>();

		for (int i = 0; i < _COMPANYCOUNT; i++) {
			String webId = "liferay" + i + ".com";

			futures.add(
				_executorService.submit(
					() -> {
						_addCompany(webId);

						return null;
					}));
		}

		for (Future<Void> future : futures) {
			future.get();
		}

		Assert.assertTrue(
			"Company count should be " + (_COMPANYCOUNT + 1),
			_companyLocalService.getCompaniesCount() == (_COMPANYCOUNT + 1));

		_companyLocalService.forEachCompany(
			company -> {
				String webId = company.getWebId();

				if (webId.equals(PropsValues.COMPANY_DEFAULT_WEB_ID)) {
					return;
				}

				int usersCount = _userLocalService.getCompanyUsersCount(
					company.getCompanyId());

				Assert.assertTrue(
					"users count should be " + (_USERCOUNT + 1),
					usersCount == (_USERCOUNT + 1));
			});
	}

	private void _addCompany(String webId) throws Exception {

		// Add company

		Company company = _companyLocalService.addCompany(
			null, webId, webId, webId, false, 0, true);

		PortalInstances.initCompany(_mockServletContext, webId);

		// Add group

		Group group = _addGroup(company.getCompanyId());

		// Add layout

		_addLayout(company.getCompanyId(), group.getGroupId());

		// Add user

		_addUser(company.getCompanyId(), group.getGroupId(), webId);
	}

	private Group _addGroup(long companyId) throws Exception {
		return _groupLocalService.addGroup(
			_getUserId(companyId), 0, null, 0,
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.US, "site-1"
			).build(),
			null, GroupConstants.TYPE_SITE_OPEN, true, 0, StringPool.BLANK,
			true, false, true, _getServiceContext(companyId));
	}

	private void _addLayout(long companyId, long groupId) throws Exception {
		long userId = _getUserId(companyId);

		// Add layout

		Layout layout = _layoutLocalService.addLayout(
			userId, groupId, false, 0, 0, 0,
			HashMapBuilder.put(
				LocaleUtil.US, "welcome"
			).build(),
			new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
			LayoutConstants.TYPE_CONTENT, StringPool.BLANK, false, false,
			new HashMap<>(), 0, _getServiceContext(companyId));

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout != null) {
			_layoutLocalService.updateLayout(
				groupId, false, layout.getLayoutId(),
				draftLayout.getModifiedDate());
		}

		// Add FragmentEntryLink

		String portletId = "com_liferay_login_web_portlet_LoginPortlet";
		long segmentsExperienceId = 0;

		JSONObject editableValueJSONObject =
			_fragmentEntryProcessorRegistry.getDefaultEditableValuesJSONObject(
				StringPool.BLANK, StringPool.BLANK);

		editableValueJSONObject.put(
			"instanceId", StringPool.BLANK
		).put(
			"portletId", portletId
		);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				userId, groupId, 0, 0, segmentsExperienceId,
				draftLayout.getPlid(), StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK,
				editableValueJSONObject.toString(), StringUtil.randomId(), 0,
				null, _getServiceContext(companyId));

		// Add layoutStructure

		LayoutPageTemplateStructure layoutPageTemplateStructure = null;

		String originalName = PrincipalThreadLocal.getName();

		try {
			PrincipalThreadLocal.setName(userId);

			ServiceContextThreadLocal.pushServiceContext(
				_getServiceContext(companyId));

			layoutPageTemplateStructure =
				_layoutPageTemplateStructureLocalService.
					fetchLayoutPageTemplateStructure(
						groupId, draftLayout.getPlid(), true);
		}
		finally {
			PrincipalThreadLocal.setName(originalName);

			ServiceContextThreadLocal.popServiceContext();
		}

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(0));

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(),
			layoutStructure.getMainItemId(), 0);

		JSONObject dataJSONObject = layoutStructure.toJSONObject();

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				groupId, draftLayout.getPlid(), segmentsExperienceId,
				dataJSONObject.toString());

		// Add PortletPreferences to draft layout

		Portlet portlet = _portletLocalService.fetchPortletById(
			companyId, portletId);

		long originalCompanyId = CompanyThreadLocal.getCompanyId();

		CompanyThreadLocal.setCompanyId(companyId);

		_portletPreferencesLocalService.addPortletPreferences(
			companyId, 0, PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
			draftLayout.getPlid(), portlet.getPortletId(), portlet, null);

		CompanyThreadLocal.setCompanyId(originalCompanyId);

		// Publish layout
		// No need to execute LayoutStructureUtil.deleteMarkedForDeletionItems)
		// since no remove portlet from draft layout

		draftLayout = _layoutLocalService.getLayout(draftLayout.getPlid());

		layout = _layoutLocalService.getLayout(draftLayout.getClassPK());

		layout = _layoutCopyHelper.copyLayout(draftLayout, layout);

		layout.setType(draftLayout.getType());
		layout.setStatus(WorkflowConstants.STATUS_APPROVED);

		String layoutPrototypeUuid = layout.getLayoutPrototypeUuid();

		layout.setLayoutPrototypeUuid(null);

		_layoutLocalService.updateLayout(layout);

		draftLayout = _layoutLocalService.getLayout(draftLayout.getPlid());

		UnicodeProperties typeSettingsUnicodeProperties =
			draftLayout.getTypeSettingsProperties();

		if (Validator.isNotNull(layoutPrototypeUuid)) {
			typeSettingsUnicodeProperties.setProperty(
				"layoutPrototypeUuid", layoutPrototypeUuid);
		}

		draftLayout.setStatus(WorkflowConstants.STATUS_APPROVED);

		_layoutLocalService.updateLayout(draftLayout);
	}

	private void _addUser(long companyId, long groupId, String webId)
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

		for (int i = 0; i < _USERCOUNT; i++) {
			String screenName = "test" + i;

			String firstName = screenName;
			String lastName = screenName;

			String emailAddress = screenName + StringPool.AT + webId;

			_userLocalService.addUser(
				0, companyId, false, "test", "test", false, screenName,
				emailAddress, LocaleUtil.US, firstName, middleName, lastName,
				prefixId, suffixId, male, birthdayMonth, birthdayDay,
				birthdayYear, jobTitle, new long[] {groupId}, organizationIds,
				new long[] {role.getRoleId()}, userGroupIds, sendEmail,
				_getServiceContext(companyId));
		}
	}

	private ServiceContext _getServiceContext(long companyId) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setCompanyId(companyId);

		return serviceContext;
	}

	private long _getUserId(long companyId) throws Exception {
		User adminUser = _userLocalService.getUserByScreenName(
			companyId, "test");

		return adminUser.getUserId();
	}

	private static final int _COMPANYCOUNT = GetterUtil.get(
		PropsUtil.get("company.test.count"), 2);

	private static final int _USERCOUNT = GetterUtil.get(
		PropsUtil.get("each.company.include.users.count"), 1);

	@Inject
	private CompanyLocalService _companyLocalService;

	private ExecutorService _executorService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutCopyHelper _layoutCopyHelper;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	private MockServletContext _mockServletContext;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}