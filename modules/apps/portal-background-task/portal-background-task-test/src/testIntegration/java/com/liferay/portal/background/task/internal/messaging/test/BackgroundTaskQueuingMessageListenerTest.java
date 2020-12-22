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
import com.liferay.background.task.kernel.util.comparator.BackgroundTaskCreateDateComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.background.task.service.BackgroundTaskLocalServiceWrapper;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.lock.Lock;
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
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
	public static void setUpClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			BackgroundTaskQueuingMessageListenerTest.class);

		_bundleContext = bundle.getBundleContext();

		_backgroundTaskExecutorServiceRegistrations.add(
			_bundleContext.registerService(
				BackgroundTaskExecutor.class.getName(),
				_testSerialBackgroundTaskExecutor,
				new HashMapDictionary<String, String>() {
					{
						put(
							"background.task.executor.class.name",
							TestSerialBackgroundTaskExecutor.class.getName());
					}
				}));

		_backgroundTaskExecutorServiceRegistrations.add(
			_bundleContext.registerService(
				BackgroundTaskExecutor.class.getName(),
				_testNoSerialBackgroundTaskExecutor,
				new HashMapDictionary<String, String>() {
					{
						put(
							"background.task.executor.class.name",
							TestNoSerialBackgroundTaskExecutor.class.getName());
					}
				}));

		_backgroundTaskLocalServiceWrapperServiceRegistration =
			_bundleContext.registerService(
				ServiceWrapper.class.getName(),
				new TestBackgroundTaskLocalServiceWrapper(), null);

		_createQueuedBackgroundTask();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		for (ServiceRegistration<?> backgroundTaskExecutorServiceRegistration :
				_backgroundTaskExecutorServiceRegistrations) {

			backgroundTaskExecutorServiceRegistration.unregister();
		}

		_backgroundTaskExecutorServiceRegistrations.clear();

		if (_backgroundTaskLocalServiceWrapperServiceRegistration != null) {
			_backgroundTaskLocalServiceWrapperServiceRegistration.unregister();
		}

		if (_company != null) {
			_companyLocalService.deleteCompany(_company);
		}
	}

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		_group = GroupTestUtil.addGroup();

		_actualBackgroundTask = null;
	}

	@Test
	public void testResumedBackgroundTaskByCompanyIdFromQueued()
		throws Exception {

		_testSerialBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY);

		_assertResumedBackgroundTaskFromQueued(
			BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY,
			TestSerialBackgroundTaskExecutor.class.getName() +
				StringPool.POUND + _user.getCompanyId());
	}

	@Test
	public void testResumedBackgroundTaskByGroupIdFromQueued()
		throws Exception {

		_testSerialBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_GROUP);

		_assertResumedBackgroundTaskFromQueued(
			BackgroundTaskConstants.ISOLATION_LEVEL_GROUP,
			TestSerialBackgroundTaskExecutor.class.getName() +
				StringPool.POUND + _group.getGroupId());
	}

	@Test
	public void testResumedBackgroundTaskByLevelClassFromQueued()
		throws Exception {

		_testSerialBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_CLASS);

		_assertResumedBackgroundTaskFromQueued(
			BackgroundTaskConstants.ISOLATION_LEVEL_CLASS,
			TestSerialBackgroundTaskExecutor.class.getName());
	}

	@Test
	public void testResumedBackgroundTaskByLevelCustomFromQueued()
		throws Exception {

		_testSerialBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_CUSTOM);

		_assertResumedBackgroundTaskFromQueued(
			BackgroundTaskConstants.ISOLATION_LEVEL_CUSTOM,
			TestSerialBackgroundTaskExecutor.class.getName() +
				StringPool.POUND +
					BackgroundTaskConstants.ISOLATION_LEVEL_CUSTOM);
	}

	@Test
	public void testResumedBackgroundTaskByNameFromQueued() throws Exception {
		_testSerialBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_TASK_NAME);

		_assertResumedBackgroundTaskFromQueued(
			BackgroundTaskConstants.ISOLATION_LEVEL_TASK_NAME,
			TestSerialBackgroundTaskExecutor.class.getName() +
				StringPool.POUND + _EXPECT_NAME);
	}

	@Test
	public void testResumedBackgroundTaskByNotIsolatedFromQueued()
		throws Exception {

		_assertResumedBackgroundTaskFromQueued(
			BackgroundTaskConstants.ISOLATION_LEVEL_NOT_ISOLATED, null);
	}

	private static void _createQueuedBackgroundTask() throws Exception {
		_testSerialBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_CLASS);

		_company = CompanyTestUtil.addCompany();

		User defaultUser = _company.getDefaultUser();

		String taskExecutorClassName =
			TestSerialBackgroundTaskExecutor.class.getName();

		_lockManager.lock(
			BackgroundTaskExecutor.class.getName(), taskExecutorClassName,
			_OWNER);

		int count = 3;

		try {
			for (int i = 0; i < count; i++) {
				_backgroundTaskLocalService.addBackgroundTask(
					defaultUser.getUserId(), _company.getGroupId(), _TEST_NAME,
					taskExecutorClassName,
					HashMapBuilder.<String, Serializable>put(
						"key", i
					).build(),
					null);
			}
		}
		finally {
			_lockManager.unlock(
				BackgroundTaskExecutor.class.getName(), taskExecutorClassName,
				_OWNER);
		}
	}

	private void _assertResumedBackgroundTaskFromQueued(
			int isolationLevel, String lockKey)
		throws Exception {

		String taskExecutorClassName =
			TestSerialBackgroundTaskExecutor.class.getName();

		Lock lock = null;

		if (lockKey != null) {
			lock = _lockManager.lock(
				BackgroundTaskExecutor.class.getName(), lockKey, _OWNER);
		}
		else {
			taskExecutorClassName =
				TestNoSerialBackgroundTaskExecutor.class.getName();
		}

		BackgroundTask expectedBackgroundTask = null;

		try {
			expectedBackgroundTask =
				_backgroundTaskLocalService.addBackgroundTask(
					_user.getUserId(), _group.getGroupId(), _EXPECT_NAME,
					taskExecutorClassName, null, null);
		}
		finally {
			if (lock != null) {
				_lockManager.unlock(
					BackgroundTaskExecutor.class.getName(), lockKey, _OWNER);
			}
		}

		long expectedBackgroundTaskId =
			expectedBackgroundTask.getBackgroundTaskId();

		Message message = new Message();

		if (isolationLevel !=
				BackgroundTaskConstants.ISOLATION_LEVEL_NOT_ISOLATED) {

			message.put("status", BackgroundTaskConstants.STATUS_QUEUED);
			message.put("isolationLevel", isolationLevel);
			message.put("lockKey", lockKey);
		}
		else {
			message.put("status", BackgroundTaskConstants.STATUS_SUCCESSFUL);
		}

		message.put(
			BackgroundTaskConstants.BACKGROUND_TASK_ID,
			expectedBackgroundTaskId);
		message.put("taskExecutorClassName", taskExecutorClassName);

		_messageBus.sendMessage(
			DestinationNames.BACKGROUND_TASK_STATUS, message);

		if (isolationLevel ==
				BackgroundTaskConstants.ISOLATION_LEVEL_NOT_ISOLATED) {

			Assert.assertNull(
				"Non-serial BackgroundTaskExecutor resumed one queued " +
					"BackgroundTask",
				_actualBackgroundTask);

			return;
		}
		else if ((isolationLevel !=
					BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY) &&
				 (isolationLevel !=
					 BackgroundTaskConstants.ISOLATION_LEVEL_GROUP) &&
				 (isolationLevel !=
					 BackgroundTaskConstants.ISOLATION_LEVEL_TASK_NAME)) {

			Assert.assertEquals(_TEST_NAME, _actualBackgroundTask.getName());

			com.liferay.portal.kernel.backgroundtask.BackgroundTask
				actualLastBackgroundTask =
					_backgroundTaskManager.fetchFirstBackgroundTask(
						taskExecutorClassName,
						BackgroundTaskConstants.STATUS_QUEUED,
						new BackgroundTaskCreateDateComparator(false));

			Assert.assertTrue(
				actualLastBackgroundTask.getBackgroundTaskId() ==
					expectedBackgroundTaskId);
			Assert.assertEquals(
				_EXPECT_NAME, actualLastBackgroundTask.getName());

			return;
		}

		Assert.assertTrue(
			"BackgroundTask status is not QUEUED(4)",
			_actualBackgroundTask.getStatus() ==
				BackgroundTaskConstants.STATUS_QUEUED);
		Assert.assertTrue(
			_actualBackgroundTask.getBackgroundTaskId() ==
				expectedBackgroundTaskId);
		Assert.assertEquals(_EXPECT_NAME, _actualBackgroundTask.getName());
	}

	private static final String _EXPECT_NAME = "_EXPECT_NAME";

	private static final String _OWNER = "OWNER";

	private static final String _TEST_NAME = "_TEST_NAME";

	private static BackgroundTask _actualBackgroundTask;
	private static final List<ServiceRegistration<?>>
		_backgroundTaskExecutorServiceRegistrations =
			new CopyOnWriteArrayList<>();

	@Inject
	private static BackgroundTaskLocalService _backgroundTaskLocalService;

	private static ServiceRegistration<?>
		_backgroundTaskLocalServiceWrapperServiceRegistration;
	private static BundleContext _bundleContext;
	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject
	private static LockManager _lockManager;

	private static final TestNoSerialBackgroundTaskExecutor
		_testNoSerialBackgroundTaskExecutor =
			new TestNoSerialBackgroundTaskExecutor();
	private static final TestSerialBackgroundTaskExecutor
		_testSerialBackgroundTaskExecutor =
			new TestSerialBackgroundTaskExecutor();

	@Inject
	private BackgroundTaskManager _backgroundTaskManager;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MessageBus _messageBus;

	@DeleteAfterTestRun
	private User _user;

	private static class TestBackgroundTaskLocalServiceWrapper
		extends BackgroundTaskLocalServiceWrapper {

		public TestBackgroundTaskLocalServiceWrapper() {
			super(null);
		}

		@Override
		public void resumeBackgroundTask(long backgroundTaskId) {
			BackgroundTask backgroundTask =
				_backgroundTaskLocalService.fetchBackgroundTask(
					backgroundTaskId);

			String name = backgroundTask.getName();

			if (name.equals(_EXPECT_NAME) || name.equals(_TEST_NAME)) {
				_actualBackgroundTask = backgroundTask;

				return;
			}

			_backgroundTaskLocalService.resumeBackgroundTask(backgroundTaskId);
		}

	}

	private static class TestNoSerialBackgroundTaskExecutor
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

	}

	private static class TestSerialBackgroundTaskExecutor
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

}