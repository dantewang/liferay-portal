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

import com.liferay.message.boards.kernel.model.MBCategoryConstants;
import com.liferay.message.boards.kernel.model.MBCategoryModel;
import com.liferay.message.boards.kernel.model.MBDiscussionModel;
import com.liferay.message.boards.kernel.model.MBMailingListModel;
import com.liferay.message.boards.kernel.model.MBMessageConstants;
import com.liferay.message.boards.kernel.model.MBMessageModel;
import com.liferay.message.boards.kernel.model.MBStatsUserModel;
import com.liferay.message.boards.kernel.model.MBThreadFlagModel;
import com.liferay.message.boards.kernel.model.MBThreadModel;
import com.liferay.portlet.messageboards.model.impl.MBCategoryModelImpl;
import com.liferay.portlet.messageboards.model.impl.MBDiscussionModelImpl;
import com.liferay.portlet.messageboards.model.impl.MBMailingListModelImpl;
import com.liferay.portlet.messageboards.model.impl.MBMessageModelImpl;
import com.liferay.portlet.messageboards.model.impl.MBStatsUserModelImpl;
import com.liferay.portlet.messageboards.model.impl.MBThreadFlagModelImpl;
import com.liferay.portlet.messageboards.model.impl.MBThreadModelImpl;
import com.liferay.util.SimpleCounter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class MessageBoardDataFactory {

	public MessageBoardDataFactory(InitContext initContext) {
		_initContext = initContext;
	}

	public List<MBCategoryModel> newMBCategoryModels(long groupId) {
		int maxMBCategoryCount = _initContext.getMaxMBCategoryCount();

		List<MBCategoryModel> mbCategoryModels = new ArrayList<>(
			maxMBCategoryCount);

		SimpleCounter counter = _initContext.getCounter();

		for (int i = 1; i <= maxMBCategoryCount; i++) {
			mbCategoryModels.add(
				newMBCategoryModel(
					groupId, i, counter.get(),
					_initContext.getCompanyId(), _initContext.getSampleUserId(),
					DataFactoryConstants.SAMPLE_USER_NAME,
					_initContext.getMaxMBThreadCount(),
					_initContext.getMaxMBMessageCount()));
		}

		return mbCategoryModels;
	}

	public MBDiscussionModel newMBDiscussionModel(
		long groupId, long classNameId, long classPK, long threadId) {

		MBDiscussionModel mbDiscussionModel = new MBDiscussionModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		mbDiscussionModel.setUuid(SequentialUUID.generate());
		mbDiscussionModel.setDiscussionId(counter.get());
		mbDiscussionModel.setGroupId(groupId);
		mbDiscussionModel.setCompanyId(_initContext.getCompanyId());
		mbDiscussionModel.setUserId(_initContext.getSampleUserId());
		mbDiscussionModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbDiscussionModel.setCreateDate(new Date());
		mbDiscussionModel.setModifiedDate(new Date());
		mbDiscussionModel.setClassNameId(classNameId);
		mbDiscussionModel.setClassPK(classPK);
		mbDiscussionModel.setThreadId(threadId);
		mbDiscussionModel.setLastPublishDate(new Date());

		return mbDiscussionModel;
	}

	public MBMailingListModel newMBMailingListModel(
		MBCategoryModel mbCategoryModel) {

		MBMailingListModel mbMailingListModel = new MBMailingListModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		mbMailingListModel.setUuid(SequentialUUID.generate());
		mbMailingListModel.setMailingListId(counter.get());
		mbMailingListModel.setGroupId(mbCategoryModel.getGroupId());
		mbMailingListModel.setCompanyId(_initContext.getCompanyId());
		mbMailingListModel.setUserId(_initContext.getSampleUserId());
		mbMailingListModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbMailingListModel.setCreateDate(new Date());
		mbMailingListModel.setModifiedDate(new Date());
		mbMailingListModel.setCategoryId(mbCategoryModel.getCategoryId());
		mbMailingListModel.setInProtocol("pop3");
		mbMailingListModel.setInServerPort(110);
		mbMailingListModel.setInUserName(
			_initContext.getSampleUserModel().getEmailAddress());
		mbMailingListModel.setInPassword(
			_initContext.getSampleUserModel().getPassword());
		mbMailingListModel.setInReadInterval(5);
		mbMailingListModel.setOutServerPort(25);

		return mbMailingListModel;
	}

	public MBMessageModel newMBMessageModel(
		MBThreadModel mbThreadModel, long classNameId, long classPK,
		int index) {

		long messageId = 0;
		long parentMessageId = 0;
		String subject = null;
		String body = null;
	
		SimpleCounter counter = _initContext.getCounter();

		if (index == 0) {
			messageId = mbThreadModel.getRootMessageId();
			parentMessageId = MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID;
			subject = String.valueOf(classPK);
			body = String.valueOf(classPK);
		}
		else {
			messageId = counter.get();
			parentMessageId = mbThreadModel.getRootMessageId();
			subject = "N/A";
			body = "This is test comment " + index + ".";
		}

		return newMBMessageModel(
			mbThreadModel.getGroupId(), classNameId, classPK,
			MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			mbThreadModel.getThreadId(), messageId,
			mbThreadModel.getRootMessageId(), parentMessageId, subject, body);
	}

	public List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel) {

		int maxMBMessageCount = _initContext.getMaxMBMessageCount();

		List<MBMessageModel> mbMessageModels = new ArrayList<>(
			maxMBMessageCount);

		SimpleCounter counter = _initContext.getCounter();

		mbMessageModels.add(
			newMBMessageModel(
				mbThreadModel.getGroupId(), 0, 0, mbThreadModel.getCategoryId(),
				mbThreadModel.getThreadId(), mbThreadModel.getRootMessageId(),
				mbThreadModel.getRootMessageId(),
				MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, "Test Message 1",
				"This is test message 1."));

		for (int i = 2; i <= maxMBMessageCount; i++) {
			mbMessageModels.add(
				newMBMessageModel(
					mbThreadModel.getGroupId(), 0, 0,
					mbThreadModel.getCategoryId(), mbThreadModel.getThreadId(),
					counter.get(), mbThreadModel.getRootMessageId(),
					mbThreadModel.getRootMessageId(), "Test Message " + i,
					"This is test message " + i + "."));
		}

		return mbMessageModels;
	}

	public List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel, long classNameId, long classPK,
		int maxMessageCount) {

		List<MBMessageModel> mbMessageModels = new ArrayList<>(maxMessageCount);

		for (int i = 1; i <= maxMessageCount; i++) {
			mbMessageModels.add(
				newMBMessageModel(mbThreadModel, classNameId, classPK, i));
		}

		return mbMessageModels;
	}

	public MBStatsUserModel newMBStatsUserModel(long groupId) {
		int maxMBThreadCount = _initContext.getMaxMBThreadCount();
		int maxMBCategoryCount = _initContext.getMaxMBCategoryCount();
		int maxMBMessageCount = _initContext.getMaxMBMessageCount();

		MBStatsUserModel mbStatsUserModel = new MBStatsUserModelImpl();
		
		SimpleCounter counter = _initContext.getCounter();

		mbStatsUserModel.setStatsUserId(counter.get());
		mbStatsUserModel.setGroupId(groupId);
		mbStatsUserModel.setUserId(_initContext.getSampleUserId());
		mbStatsUserModel.setMessageCount(
			maxMBCategoryCount * maxMBThreadCount * maxMBMessageCount);
		mbStatsUserModel.setLastPostDate(new Date());

		return mbStatsUserModel;
	}

	public MBThreadFlagModel newMBThreadFlagModel(MBThreadModel mbThreadModel)
	{
		MBThreadFlagModel mbThreadFlagModel = new MBThreadFlagModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		mbThreadFlagModel.setUuid(SequentialUUID.generate());
		mbThreadFlagModel.setThreadFlagId(counter.get());
		mbThreadFlagModel.setGroupId(mbThreadModel.getGroupId());
		mbThreadFlagModel.setCompanyId(_initContext.getCompanyId());
		mbThreadFlagModel.setUserId(_initContext.getSampleUserId());
		mbThreadFlagModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbThreadFlagModel.setCreateDate(new Date());
		mbThreadFlagModel.setModifiedDate(new Date());
		mbThreadFlagModel.setThreadId(mbThreadModel.getThreadId());
		mbThreadFlagModel.setLastPublishDate(new Date());

		return mbThreadFlagModel;
	}

	public MBThreadModel newMBThreadModel(
		long threadId, long groupId, long rootMessageId, int messageCount) {

		if (messageCount == 0) {
			messageCount = 1;
		}

		return newMBThreadModel(
			threadId, groupId, MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			rootMessageId, messageCount);
	}

	public List<MBThreadModel> newMBThreadModels(
		MBCategoryModel mbCategoryModel) {

		List<MBThreadModel> mbThreadModels = new ArrayList<>(
			_initContext.getMaxMBThreadCount());

		SimpleCounter counter = _initContext.getCounter();

		for (int i = 0; i < _initContext.getMaxMBThreadCount(); i++) {
			mbThreadModels.add(
				newMBThreadModel(
					counter.get(),mbCategoryModel.getGroupId(),
					mbCategoryModel.getCategoryId(),counter.get(),
					_initContext.getMaxMBMessageCount()));
		}

		return mbThreadModels;
	}

	protected MBCategoryModel newMBCategoryModel(
		long groupId, int index, long categoryId, long companyId, long userId,
		String userName, int threadCount, int messageCount) {

		MBCategoryModel mbCategoryModel = new MBCategoryModelImpl();

		mbCategoryModel.setUuid(SequentialUUID.generate());
		mbCategoryModel.setCategoryId(categoryId);
		mbCategoryModel.setGroupId(groupId);
		mbCategoryModel.setCompanyId(companyId);
		mbCategoryModel.setUserId(userId);
		mbCategoryModel.setUserName(userName);
		mbCategoryModel.setCreateDate(new Date());
		mbCategoryModel.setModifiedDate(new Date());
		mbCategoryModel.setParentCategoryId(
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
		mbCategoryModel.setName("Test Category " + index);
		mbCategoryModel.setDisplayStyle(
			MBCategoryConstants.DEFAULT_DISPLAY_STYLE);
		mbCategoryModel.setThreadCount(threadCount);
		mbCategoryModel.setMessageCount(threadCount * messageCount);
		mbCategoryModel.setLastPostDate(new Date());
		mbCategoryModel.setLastPublishDate(new Date());
		mbCategoryModel.setStatusDate(new Date());

		return mbCategoryModel;
	}

	protected MBMessageModel newMBMessageModel(
		long groupId, long classNameId, long classPK, long categoryId,
		long threadId, long messageId, long rootMessageId, long parentMessageId,
		String subject, String body) {

		MBMessageModel mBMessageModel = new MBMessageModelImpl();

		mBMessageModel.setUuid(SequentialUUID.generate());
		mBMessageModel.setMessageId(messageId);
		mBMessageModel.setGroupId(groupId);
		mBMessageModel.setCompanyId(_initContext.getCompanyId());
		mBMessageModel.setUserId(_initContext.getSampleUserId());
		mBMessageModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mBMessageModel.setCreateDate(new Date());
		mBMessageModel.setModifiedDate(new Date());
		mBMessageModel.setClassNameId(classNameId);
		mBMessageModel.setClassPK(classPK);
		mBMessageModel.setCategoryId(categoryId);
		mBMessageModel.setThreadId(threadId);
		mBMessageModel.setRootMessageId(rootMessageId);
		mBMessageModel.setParentMessageId(parentMessageId);
		mBMessageModel.setSubject(subject);
		mBMessageModel.setBody(body);
		mBMessageModel.setFormat(MBMessageConstants.DEFAULT_FORMAT);
		mBMessageModel.setLastPublishDate(new Date());
		mBMessageModel.setStatusDate(new Date());

		return mBMessageModel;
	}

	protected MBThreadModel newMBThreadModel(
		long threadId, long groupId, long categoryId, long rootMessageId,
		int messageCount) {

		MBThreadModel mbThreadModel = new MBThreadModelImpl();

		mbThreadModel.setUuid(SequentialUUID.generate());
		mbThreadModel.setThreadId(threadId);
		mbThreadModel.setGroupId(groupId);
		mbThreadModel.setCompanyId(_initContext.getCompanyId());
		mbThreadModel.setUserId(_initContext.getSampleUserId());
		mbThreadModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbThreadModel.setCreateDate(new Date());
		mbThreadModel.setModifiedDate(new Date());
		mbThreadModel.setCategoryId(categoryId);
		mbThreadModel.setRootMessageId(rootMessageId);
		mbThreadModel.setRootMessageUserId(_initContext.getSampleUserId());
		mbThreadModel.setMessageCount(messageCount);
		mbThreadModel.setLastPostByUserId(_initContext.getSampleUserId());
		mbThreadModel.setLastPostDate(new Date());
		mbThreadModel.setLastPublishDate(new Date());
		mbThreadModel.setStatusDate(new Date());

		return mbThreadModel;
	}

	private InitContext _initContext;
}