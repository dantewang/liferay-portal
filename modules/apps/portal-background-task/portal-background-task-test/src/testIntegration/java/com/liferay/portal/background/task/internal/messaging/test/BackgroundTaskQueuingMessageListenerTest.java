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
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

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
	public static void setUpClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			BackgroundTaskQueuingMessageListenerTest.class);

		_bundleContext = bundle.getBundleContext();

		_backgroundTaskExecutorServiceRegistration =
			_bundleContext.registerService(
				BackgroundTaskExecutor.class.getName(),
				_testBackgroundTaskExecutor,
				new HashMapDictionary<String, String>() {
					{
						put(
							"background.task.executor.class.name",
							TestBackgroundTaskExecutor.class.getName());
					}
				});

		_createQueuedBackgroundTask();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		if (_backgroundTaskExecutorServiceRegistration != null) {
			_backgroundTaskExecutorServiceRegistration.unregister();
		}

		if (_company != null) {
			_companyLocalService.deleteCompany(_company);
		}
	}

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testFetchFirstBackgroundTaskByCompanyIdFromQueued()
		throws Exception {

		_testBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY);

		_assertFetchFirstBackgroundTaskFromQueued(
			BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY,
			TestBackgroundTaskExecutor.class.getName() + StringPool.POUND +
				_user.getCompanyId());
	}

	@Test
	public void testFetchFirstBackgroundTaskByGroupIdFromQueued()
		throws Exception {

		_testBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_GROUP);

		_assertFetchFirstBackgroundTaskFromQueued(
			BackgroundTaskConstants.ISOLATION_LEVEL_GROUP,
			TestBackgroundTaskExecutor.class.getName() + StringPool.POUND +
				_group.getGroupId());
	}

	@Test
	public void testFetchFirstBackgroundTaskByLevelClassFromQueued()
		throws Exception {

		_testBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_CLASS);

		_assertFetchFirstBackgroundTaskFromQueued(
			BackgroundTaskConstants.ISOLATION_LEVEL_CLASS,
			TestBackgroundTaskExecutor.class.getName());
	}

	@Test
	public void testFetchFirstBackgroundTaskByLevelCustomQueued()
		throws Exception {

		_testBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_CUSTOM);

		_assertFetchFirstBackgroundTaskFromQueued(
			BackgroundTaskConstants.ISOLATION_LEVEL_CUSTOM,
			TestBackgroundTaskExecutor.class.getName() + StringPool.POUND +
				BackgroundTaskConstants.ISOLATION_LEVEL_CUSTOM);
	}

	@Test
	public void testFetchFirstBackgroundTaskByNameFromQueued()
		throws Exception {

		_testBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_TASK_NAME);

		_assertFetchFirstBackgroundTaskFromQueued(
			BackgroundTaskConstants.ISOLATION_LEVEL_TASK_NAME,
			TestBackgroundTaskExecutor.class.getName() + StringPool.POUND +
				_EXPECT_NAME);
	}

	@Test
	public void testFetchFirstBackgroundTaskByNotIsolatedFromQueued()
		throws Exception {

		_testBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_NOT_ISOLATED);

		_assertFetchFirstBackgroundTaskFromQueued(
			BackgroundTaskConstants.ISOLATION_LEVEL_NOT_ISOLATED, null);
	}

	private static void _createQueuedBackgroundTask() throws Exception {
		_company = CompanyTestUtil.addCompany();

		User user = _company.getDefaultUser();

		_testBackgroundTaskExecutor.setIsolationLevel(
			BackgroundTaskConstants.ISOLATION_LEVEL_CLASS);

		String taskExecutorClassName =
			TestBackgroundTaskExecutor.class.getName();

		String owner = "Owner";

		_lockManager.lock(
			BackgroundTaskExecutor.class.getName(), taskExecutorClassName,
			"test");

		int count = 3;

		try {
			for (int i = 0; i < count; i++) {
				_backgroundTaskLocalService.addBackgroundTask(
					user.getUserId(), _company.getGroupId(), _TEST_NAME,
					taskExecutorClassName,
					HashMapBuilder.<String, Serializable>put(
						"key", i
					).build(),
					null);
			}

			List<BackgroundTask> backgroundTaskList =
				_backgroundTaskLocalService.getBackgroundTasks(
					taskExecutorClassName,
					BackgroundTaskConstants.STATUS_QUEUED);

			Assert.assertEquals(
				backgroundTaskList.toString(), count,
				backgroundTaskList.size());

			BackgroundTask backgroundTask =
				_backgroundTaskLocalService.fetchFirstBackgroundTask(
					taskExecutorClassName,
					BackgroundTaskConstants.STATUS_QUEUED);

			Assert.assertEquals(_TEST_NAME, backgroundTask.getName());
		}
		finally {
			_lockManager.unlock(
				BackgroundTaskExecutor.class.getName(), taskExecutorClassName,
				owner);
		}
	}

	private void _assertFetchFirstBackgroundTaskFromQueued(
			int isolationLevel, String lockKey)
		throws Exception {

		String taskExecutorClassName =
			TestBackgroundTaskExecutor.class.getName();

		Lock lock = null;

		if (lockKey != null) {
			lock = _lockManager.lock(
				BackgroundTaskExecutor.class.getName(), lockKey, "test");
		}

		try {
			BackgroundTask originalBackgroundTask =
				_backgroundTaskLocalService.addBackgroundTask(
					_user.getUserId(), _group.getGroupId(), _EXPECT_NAME,
					taskExecutorClassName,
					HashMapBuilder.<String, Serializable>put(
						BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS,
						false
					).build(),
					null);

			BackgroundTask actualBackgroundTask = null;

			if (isolationLevel ==
					BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY) {

				actualBackgroundTask =
					_backgroundTaskLocalService.
						fetchFirstBackgroundTaskByCompanyId(
							GetterUtil.getLong(
								StringUtil.extractLast(
									lockKey, CharPool.POUND)),
							taskExecutorClassName,
							BackgroundTaskConstants.STATUS_QUEUED, null);
			}
			else if (isolationLevel ==
						BackgroundTaskConstants.ISOLATION_LEVEL_GROUP) {

				actualBackgroundTask =
					_backgroundTaskLocalService.
						fetchFirstBackgroundTaskByGroupId(
							GetterUtil.getLong(
								StringUtil.extractLast(
									lockKey, CharPool.POUND)),
							taskExecutorClassName,
							BackgroundTaskConstants.STATUS_QUEUED, null);
			}
			else if (isolationLevel ==
						BackgroundTaskConstants.ISOLATION_LEVEL_NOT_ISOLATED) {

				Assert.assertFalse(
					"The current TestBackgroundTaskExecutor is Serial",
					_testBackgroundTaskExecutor.isSerial());

				actualBackgroundTask =
					_backgroundTaskLocalService.fetchBackgroundTask(
						originalBackgroundTask.getBackgroundTaskId());

				Assert.assertTrue(
					"Background task status is QUEUED(4)",
					(actualBackgroundTask.getStatus() !=
						BackgroundTaskConstants.STATUS_QUEUED) &&
					(actualBackgroundTask.getStatus() ==
						BackgroundTaskConstants.STATUS_SUCCESSFUL));

				return;
			}
			else if (isolationLevel ==
						BackgroundTaskConstants.ISOLATION_LEVEL_TASK_NAME) {

				actualBackgroundTask =
					_backgroundTaskLocalService.fetchFirstBackgroundTask(
						StringUtil.extractLast(lockKey, CharPool.POUND),
						taskExecutorClassName,
						BackgroundTaskConstants.STATUS_QUEUED, null);
			}
			else {
				BackgroundTask firstBackgroundTask =
					_backgroundTaskLocalService.fetchFirstBackgroundTask(
						taskExecutorClassName,
						BackgroundTaskConstants.STATUS_QUEUED);

				Assert.assertEquals(_TEST_NAME, firstBackgroundTask.getName());

				com.liferay.portal.kernel.backgroundtask.BackgroundTask
					actualLastBackgroundTask =
						_backgroundTaskManager.fetchFirstBackgroundTask(
							taskExecutorClassName,
							BackgroundTaskConstants.STATUS_QUEUED,
							new BackgroundTaskCreateDateComparator(false));

				Assert.assertEquals(
					_EXPECT_NAME, actualLastBackgroundTask.getName());

				return;
			}

			Assert.assertTrue(
				"Background task status is not QUEUED(4)",
				actualBackgroundTask.getStatus() ==
					BackgroundTaskConstants.STATUS_QUEUED);
			Assert.assertEquals(_EXPECT_NAME, actualBackgroundTask.getName());
		}
		finally {
			if (lock != null) {
				_lockManager.unlock(
					BackgroundTaskExecutor.class.getName(), lockKey, "test");
			}
		}
	}

	private static final String _EXPECT_NAME = "_EXPECT_NAME";

	private static final String _TEST_NAME = "_TEST_NAME";

	private static ServiceRegistration<?>
		_backgroundTaskExecutorServiceRegistration;

	@Inject
	private static BackgroundTaskLocalService _backgroundTaskLocalService;

	private static BundleContext _bundleContext;
	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject
	private static LockManager _lockManager;

	private static final TestBackgroundTaskExecutor
		_testBackgroundTaskExecutor = new TestBackgroundTaskExecutor();

	@Inject
	private BackgroundTaskManager _backgroundTaskManager;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

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

}