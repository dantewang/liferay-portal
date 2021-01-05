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

package com.liferay.portal.background.task.internal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.background.task.service.BackgroundTaskLocalServiceWrapper;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Hai Yu
 */
@RunWith(Arquillian.class)
public class BackgroundTaskQueuingMessageListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(
			BackgroundTaskQueuingMessageListenerTest.class);

		_bundleContext = bundle.getBundleContext();

		_serviceRegistrations.add(
			_bundleContext.registerService(
				BackgroundTaskExecutor.class.getName(),
				_testBackgroundTaskExecutor,
				new HashMapDictionary<String, String>() {
					{
						put(
							"background.task.executor.class.name",
							TestBackgroundTaskExecutor.class.getName());
					}
				}));

		_serviceRegistrations.add(
			_bundleContext.registerService(
				ServiceWrapper.class.getName(),
				_testBackgroundTaskLocalServiceWrapper, null));
	}

	@AfterClass
	public static void tearDownClass() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		_group = GroupTestUtil.addGroup();

		_groups.add(_group);

		_testBackgroundTaskLocalServiceWrapper._resumedBackgroundTaskId = -1;
	}

	@Test
	public void testResumeQueuedBackgroundTaskByClassName() throws Exception {

		// ISOLATION_LEVEL_CLASS

		_testBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_CLASS);

		String lockKey1 = TestBackgroundTaskExecutor.class.getName();

		BackgroundTask backgroundTask1 = _createQueuedBackgroundTask(
			lockKey1, _user.getUserId(), _group.getGroupId(), _TASK_NAME + 1);

		// ISOLATION_LEVEL_CUSTOM

		_testBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_CUSTOM);

		String lockKey2 =
			TestBackgroundTaskExecutor.class.getName() + StringPool.POUND +
				BackgroundTaskConstants.ISOLATION_LEVEL_CUSTOM;

		BackgroundTask backgroundTask2 = _createQueuedBackgroundTask(
			lockKey2, _user.getUserId(), _group.getGroupId(), _TASK_NAME + 2);

		// Unrecognized isolation level

		_testBackgroundTaskExecutor.setIsolationLevel(10);

		String lockKey3 =
			TestBackgroundTaskExecutor.class.getName() + StringPool.POUND +
				"10";

		BackgroundTask backgroundTask3 = _createQueuedBackgroundTask(
			lockKey3, _user.getUserId(), _group.getGroupId(), _TASK_NAME + 3);

		// Tasks of ISOLATION_LEVEL_CLASS, ISOLATION_LEVEL_CUSTOM and
		// unrecognized isolation level are in the same queue by executor class
		// name

		List<BackgroundTask> backgroundTasks =
			_backgroundTaskLocalService.getBackgroundTasks(
				TestBackgroundTaskExecutor.class.getName(),
				BackgroundTaskConstants.STATUS_QUEUED);

		Assert.assertEquals(
			backgroundTasks.toString(), 3, backgroundTasks.size());

		_sendStatusMessage(10, lockKey3, backgroundTask3.getBackgroundTaskId());

		Assert.assertEquals(
			backgroundTask1.getBackgroundTaskId(),
			_testBackgroundTaskLocalServiceWrapper._resumedBackgroundTaskId);

		_sendStatusMessage(
			BackgroundTaskConstants.ISOLATION_LEVEL_CUSTOM, lockKey2,
			backgroundTask2.getBackgroundTaskId());

		Assert.assertEquals(
			backgroundTask1.getBackgroundTaskId(),
			_testBackgroundTaskLocalServiceWrapper._resumedBackgroundTaskId);

		_sendStatusMessage(
			BackgroundTaskConstants.ISOLATION_LEVEL_CLASS, lockKey1,
			backgroundTask1.getBackgroundTaskId());

		Assert.assertEquals(
			backgroundTask1.getBackgroundTaskId(),
			_testBackgroundTaskLocalServiceWrapper._resumedBackgroundTaskId);
	}

	@Test
	public void testResumeQueuedBackgroundTaskByCompany() throws Exception {
		_testBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY);

		Company company = CompanyTestUtil.addCompany();

		User user = company.getDefaultUser();

		Group group = company.getGroup();

		try {
			_createQueuedBackgroundTask(
				TestBackgroundTaskExecutor.class.getName() + StringPool.POUND +
					company.getCompanyId(),
				user.getUserId(), group.getGroupId(), _TASK_NAME);

			_testResumeQueuedBackgroundTask(
				TestBackgroundTaskExecutor.class.getName() + StringPool.POUND +
					_user.getCompanyId());
		}
		finally {
			_companyLocalService.deleteCompany(company);
		}
	}

	@Test
	public void testResumeQueuedBackgroundTaskByGroup() throws Exception {
		_testBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_GROUP);

		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		_createQueuedBackgroundTask(
			TestBackgroundTaskExecutor.class.getName() + StringPool.POUND +
				group.getGroupId(),
			_user.getUserId(), group.getGroupId(), _TASK_NAME);

		_testResumeQueuedBackgroundTask(
			TestBackgroundTaskExecutor.class.getName() + StringPool.POUND +
				_group.getGroupId());
	}

	@Test
	public void testResumeQueuedBackgroundTaskByTaskName() throws Exception {
		_testBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_TASK_NAME);

		_createQueuedBackgroundTask(
			TestBackgroundTaskExecutor.class.getName() + StringPool.POUND +
				"ANOTHER_NAME",
			_user.getUserId(), _group.getGroupId(), "ANOTHER_NAME");

		_testResumeQueuedBackgroundTask(
			TestBackgroundTaskExecutor.class.getName() + StringPool.POUND +
				_TASK_NAME);
	}

	private BackgroundTask _createQueuedBackgroundTask(
			String lockKey, long userId, long groupId, String name)
		throws Exception {

		_lockManager.lock(
			BackgroundTaskExecutor.class.getName(), lockKey, _OWNER);

		try {
			return _backgroundTaskLocalService.addBackgroundTask(
				userId, groupId, name,
				TestBackgroundTaskExecutor.class.getName(), null, null);
		}
		finally {
			_lockManager.unlock(
				BackgroundTaskExecutor.class.getName(), lockKey, _OWNER);
		}
	}

	private void _sendStatusMessage(
		int isolationLevel, String lockKey, long backgroundTaskId) {

		Message message = new Message();

		message.put("status", BackgroundTaskConstants.STATUS_QUEUED);
		message.put("isolationLevel", isolationLevel);
		message.put("lockKey", lockKey);
		message.put(
			BackgroundTaskConstants.BACKGROUND_TASK_ID, backgroundTaskId);
		message.put(
			"taskExecutorClassName",
			TestBackgroundTaskExecutor.class.getName());

		_messageBus.sendMessage(
			DestinationNames.BACKGROUND_TASK_STATUS, message);
	}

	private void _testResumeQueuedBackgroundTask(String lockKey)
		throws Exception {

		BackgroundTask backgroundTask = _createQueuedBackgroundTask(
			lockKey, _user.getUserId(), _group.getGroupId(), _TASK_NAME);

		long backgroundTaskId = backgroundTask.getBackgroundTaskId();

		List<BackgroundTask> backgroundTasks =
			_backgroundTaskLocalService.getBackgroundTasks(
				TestBackgroundTaskExecutor.class.getName(),
				BackgroundTaskConstants.STATUS_QUEUED);

		Assert.assertEquals(
			backgroundTasks.toString(), 2, backgroundTasks.size());

		_sendStatusMessage(
			_testBackgroundTaskExecutor.getIsolationLevel(), lockKey,
			backgroundTaskId);

		Assert.assertEquals(
			backgroundTaskId,
			_testBackgroundTaskLocalServiceWrapper._resumedBackgroundTaskId);
	}

	private static final String _OWNER = "OWNER";

	private static final String _TASK_NAME = "TASK_NAME";

	@Inject
	private static BackgroundTaskLocalService _backgroundTaskLocalService;

	private static BundleContext _bundleContext;

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject
	private static LockManager _lockManager;

	private static final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();
	private static final TestBackgroundTaskExecutor
		_testBackgroundTaskExecutor = new TestBackgroundTaskExecutor();
	private static final TestBackgroundTaskLocalServiceWrapper
		_testBackgroundTaskLocalServiceWrapper =
			new TestBackgroundTaskLocalServiceWrapper();

	private Group _group;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@Inject
	private MessageBus _messageBus;

	@DeleteAfterTestRun
	private User _user;

	private static class TestBackgroundTaskExecutor
		extends BaseBackgroundTaskExecutor {

		@Override
		public BackgroundTaskExecutor clone() {
			return this;
		}

		@Override
		public BackgroundTaskResult execute(
			com.liferay.portal.kernel.backgroundtask.BackgroundTask
				backgroundTask) {

			return BackgroundTaskResult.SUCCESS;
		}

		@Override
		public BackgroundTaskDisplay getBackgroundTaskDisplay(
			com.liferay.portal.kernel.backgroundtask.BackgroundTask
				backgroundTask) {

			return null;
		}

		@Override
		protected void setIsolationLevel(int isolationLevel) {
			super.setIsolationLevel(isolationLevel);
		}

	}

	private static class TestBackgroundTaskLocalServiceWrapper
		extends BackgroundTaskLocalServiceWrapper {

		public TestBackgroundTaskLocalServiceWrapper() {
			super(null);
		}

		@Override
		public void resumeBackgroundTask(long backgroundTaskId) {
			_resumedBackgroundTaskId = backgroundTaskId;
		}

		private long _resumedBackgroundTaskId = -1;

	}

}