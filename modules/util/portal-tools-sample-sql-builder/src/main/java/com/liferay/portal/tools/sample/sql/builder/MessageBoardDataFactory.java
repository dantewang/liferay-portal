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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class MessageBoardDataFactory {

	public static List<MBCategoryModel> newMBCategoryModels(long groupId) {
		int maxMBCategoryCount = InitContextUtil.getMaxMBCategoryCount();

		List<MBCategoryModel> mbCategoryModels = new ArrayList<>(
			maxMBCategoryCount);

		for (int i = 1; i <= maxMBCategoryCount; i++) {
			mbCategoryModels.add(
				newMBCategoryModel(
					groupId, i, InitContextUtil.getCounter().get(),
					InitContextUtil.getCompanyId(),
					InitContextUtil.getSampleUserId(),
					DataFactoryConstants.SAMPLE_USER_NAME,
					InitContextUtil.getMaxMBThreadCount(),
					InitContextUtil.getMaxMBMessageCount()));
		}

		return mbCategoryModels;
	}

	public static MBDiscussionModel newMBDiscussionModel(
		long groupId, long classNameId, long classPK, long threadId) {

		MBDiscussionModel mbDiscussionModel = new MBDiscussionModelImpl();

		mbDiscussionModel.setUuid(SequentialUUID.generate());
		mbDiscussionModel.setDiscussionId(InitContextUtil.getCounter().get());
		mbDiscussionModel.setGroupId(groupId);
		mbDiscussionModel.setCompanyId(InitContextUtil.getCompanyId());
		mbDiscussionModel.setUserId(InitContextUtil.getSampleUserId());
		mbDiscussionModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbDiscussionModel.setCreateDate(new Date());
		mbDiscussionModel.setModifiedDate(new Date());
		mbDiscussionModel.setClassNameId(classNameId);
		mbDiscussionModel.setClassPK(classPK);
		mbDiscussionModel.setThreadId(threadId);
		mbDiscussionModel.setLastPublishDate(new Date());

		return mbDiscussionModel;
	}

	public static MBMailingListModel newMBMailingListModel(
		MBCategoryModel mbCategoryModel) {

		MBMailingListModel mbMailingListModel = new MBMailingListModelImpl();

		mbMailingListModel.setUuid(SequentialUUID.generate());
		mbMailingListModel.setMailingListId(InitContextUtil.getCounter().get());
		mbMailingListModel.setGroupId(mbCategoryModel.getGroupId());
		mbMailingListModel.setCompanyId(InitContextUtil.getCompanyId());
		mbMailingListModel.setUserId(InitContextUtil.getSampleUserId());
		mbMailingListModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbMailingListModel.setCreateDate(new Date());
		mbMailingListModel.setModifiedDate(new Date());
		mbMailingListModel.setCategoryId(mbCategoryModel.getCategoryId());
		mbMailingListModel.setInProtocol("pop3");
		mbMailingListModel.setInServerPort(110);
		mbMailingListModel.setInUserName(
			InitContextUtil.getSampleUserModel().getEmailAddress());
		mbMailingListModel.setInPassword(
			InitContextUtil.getSampleUserModel().getPassword());
		mbMailingListModel.setInReadInterval(5);
		mbMailingListModel.setOutServerPort(25);

		return mbMailingListModel;
	}

	public static MBMessageModel newMBMessageModel(
		MBThreadModel mbThreadModel, long classNameId, long classPK,
		int index) {

		long messageId = 0;
		long parentMessageId = 0;
		String subject = null;
		String body = null;

		if (index == 0) {
			messageId = mbThreadModel.getRootMessageId();
			parentMessageId = MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID;
			subject = String.valueOf(classPK);
			body = String.valueOf(classPK);
		}
		else {
			messageId = InitContextUtil.getCounter().get();
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

	public static List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel) {

		int maxMBMessageCount = InitContextUtil.getMaxMBMessageCount();

		List<MBMessageModel> mbMessageModels = new ArrayList<>(
			maxMBMessageCount);

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
					InitContextUtil.getCounter().get(),
					mbThreadModel.getRootMessageId(),
					mbThreadModel.getRootMessageId(), "Test Message " + i,
					"This is test message " + i + "."));
		}

		return mbMessageModels;
	}

	public static List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel, long classNameId, long classPK,
		int maxMessageCount) {

		List<MBMessageModel> mbMessageModels = new ArrayList<>(maxMessageCount);

		for (int i = 1; i <= maxMessageCount; i++) {
			mbMessageModels.add(
				newMBMessageModel(mbThreadModel, classNameId, classPK, i));
		}

		return mbMessageModels;
	}

	public static MBStatsUserModel newMBStatsUserModel(long groupId) {
		int maxMBThreadCount = InitContextUtil.getMaxMBThreadCount();
		int maxMBCategoryCount = InitContextUtil.getMaxMBCategoryCount();
		int maxMBMessageCount = InitContextUtil.getMaxMBMessageCount();

		MBStatsUserModel mbStatsUserModel = new MBStatsUserModelImpl();

		mbStatsUserModel.setStatsUserId(InitContextUtil.getCounter().get());
		mbStatsUserModel.setGroupId(groupId);
		mbStatsUserModel.setUserId(InitContextUtil.getSampleUserId());
		mbStatsUserModel.setMessageCount(
			maxMBCategoryCount * maxMBThreadCount * maxMBMessageCount);
		mbStatsUserModel.setLastPostDate(new Date());

		return mbStatsUserModel;
	}

	public static MBThreadFlagModel newMBThreadFlagModel(
			MBThreadModel mbThreadModel)
	{
		MBThreadFlagModel mbThreadFlagModel = new MBThreadFlagModelImpl();

		mbThreadFlagModel.setUuid(SequentialUUID.generate());
		mbThreadFlagModel.setThreadFlagId(InitContextUtil.getCounter().get());
		mbThreadFlagModel.setGroupId(mbThreadModel.getGroupId());
		mbThreadFlagModel.setCompanyId(InitContextUtil.getCompanyId());
		mbThreadFlagModel.setUserId(InitContextUtil.getSampleUserId());
		mbThreadFlagModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbThreadFlagModel.setCreateDate(new Date());
		mbThreadFlagModel.setModifiedDate(new Date());
		mbThreadFlagModel.setThreadId(mbThreadModel.getThreadId());
		mbThreadFlagModel.setLastPublishDate(new Date());

		return mbThreadFlagModel;
	}

	public static MBThreadModel newMBThreadModel(
		long threadId, long groupId, long rootMessageId, int messageCount) {

		if (messageCount == 0) {
			messageCount = 1;
		}

		return newMBThreadModel(
			threadId, groupId, MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			rootMessageId, messageCount);
	}

	public static List<MBThreadModel> newMBThreadModels(
		MBCategoryModel mbCategoryModel) {

		List<MBThreadModel> mbThreadModels = new ArrayList<>(
			InitContextUtil.getMaxMBThreadCount());

		for (int i = 0; i < InitContextUtil.getMaxMBThreadCount(); i++) {
			mbThreadModels.add(
				newMBThreadModel(
					InitContextUtil.getCounter().get(),
					mbCategoryModel.getGroupId(),
					mbCategoryModel.getCategoryId(),
					InitContextUtil.getCounter().get(),
					InitContextUtil.getMaxMBMessageCount()));
		}

		return mbThreadModels;
	}

	protected static MBMessageModel newMBMessageModel(
		long groupId, long classNameId, long classPK, long categoryId,
		long threadId, long messageId, long rootMessageId, long parentMessageId,
		String subject, String body) {

		MBMessageModel mBMessageModel = new MBMessageModelImpl();

		mBMessageModel.setUuid(SequentialUUID.generate());
		mBMessageModel.setMessageId(messageId);
		mBMessageModel.setGroupId(groupId);
		mBMessageModel.setCompanyId(InitContextUtil.getCompanyId());
		mBMessageModel.setUserId(InitContextUtil.getSampleUserId());
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

	protected static MBThreadModel newMBThreadModel(
		long threadId, long groupId, long categoryId, long rootMessageId,
		int messageCount) {

		MBThreadModel mbThreadModel = new MBThreadModelImpl();

		mbThreadModel.setUuid(SequentialUUID.generate());
		mbThreadModel.setThreadId(threadId);
		mbThreadModel.setGroupId(groupId);
		mbThreadModel.setCompanyId(InitContextUtil.getCompanyId());
		mbThreadModel.setUserId(InitContextUtil.getSampleUserId());
		mbThreadModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbThreadModel.setCreateDate(new Date());
		mbThreadModel.setModifiedDate(new Date());
		mbThreadModel.setCategoryId(categoryId);
		mbThreadModel.setRootMessageId(rootMessageId);
		mbThreadModel.setRootMessageUserId(InitContextUtil.getSampleUserId());
		mbThreadModel.setMessageCount(messageCount);
		mbThreadModel.setLastPostByUserId(InitContextUtil.getSampleUserId());
		mbThreadModel.setLastPostDate(new Date());
		mbThreadModel.setLastPublishDate(new Date());
		mbThreadModel.setStatusDate(new Date());

		return mbThreadModel;
	}

	public static MBCategoryModel newMBCategoryModel(
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

}