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

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBCategoryModel;
import com.liferay.message.boards.model.MBDiscussionModel;
import com.liferay.message.boards.model.MBMailingListModel;
import com.liferay.message.boards.model.MBMessageModel;
import com.liferay.message.boards.model.MBStatsUserModel;
import com.liferay.message.boards.model.MBThreadFlagModel;
import com.liferay.message.boards.model.MBThreadModel;
import com.liferay.message.boards.model.impl.MBCategoryModelImpl;
import com.liferay.message.boards.model.impl.MBDiscussionModelImpl;
import com.liferay.message.boards.model.impl.MBMailingListModelImpl;
import com.liferay.message.boards.model.impl.MBMessageModelImpl;
import com.liferay.message.boards.model.impl.MBStatsUserModelImpl;
import com.liferay.message.boards.model.impl.MBThreadFlagModelImpl;
import com.liferay.message.boards.model.impl.MBThreadModelImpl;
import com.liferay.portal.kernel.model.UserModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class MessageBoardDataFactory extends BaseDataFactory {

	public static MessageBoardDataFactory getInstance() {
		return _messageBoardDataFactory;
	}

	public List<MBCategoryModel> newMBCategoryModels(long groupId) {
		List<MBCategoryModel> mbCategoryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_MB_CATEGORY_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_MB_CATEGORY_COUNT; i++) {
			mbCategoryModels.add(_newMBCategoryModel(groupId, i));
		}

		return mbCategoryModels;
	}

	public MBDiscussionModel newMBDiscussionModel(
		long groupId, long classNameId, long classPK, long threadId) {

		MBDiscussionModel mbDiscussionModel = new MBDiscussionModelImpl();

		// UUID

		mbDiscussionModel.setUuid(SequentialUUID.generate());

		// PK fields

		mbDiscussionModel.setDiscussionId(counter.get());

		// Group instance

		mbDiscussionModel.setGroupId(groupId);

		// Audit fields

		mbDiscussionModel.setCompanyId(COMPANY_ID);
		mbDiscussionModel.setUserId(SAMPLE_USER_ID);
		mbDiscussionModel.setUserName(SAMPLE_USER_NAME);
		mbDiscussionModel.setCreateDate(new Date());
		mbDiscussionModel.setModifiedDate(new Date());

		// Other fields

		mbDiscussionModel.setClassNameId(classNameId);
		mbDiscussionModel.setClassPK(classPK);
		mbDiscussionModel.setThreadId(threadId);
		mbDiscussionModel.setLastPublishDate(new Date());

		return mbDiscussionModel;
	}

	public MBMailingListModel newMBMailingListModel(
		MBCategoryModel mbCategoryModel, UserModel sampleUserModel) {

		MBMailingListModel mbMailingListModel = new MBMailingListModelImpl();

		// UUID

		mbMailingListModel.setUuid(SequentialUUID.generate());

		// PK fields

		mbMailingListModel.setMailingListId(counter.get());

		// Group instance

		mbMailingListModel.setGroupId(mbCategoryModel.getGroupId());

		// Audit fields

		mbMailingListModel.setCompanyId(COMPANY_ID);
		mbMailingListModel.setUserId(SAMPLE_USER_ID);
		mbMailingListModel.setUserName(SAMPLE_USER_NAME);
		mbMailingListModel.setCreateDate(new Date());
		mbMailingListModel.setModifiedDate(new Date());

		// Other fields

		mbMailingListModel.setCategoryId(mbCategoryModel.getCategoryId());
		mbMailingListModel.setInProtocol("pop3");
		mbMailingListModel.setInServerPort(110);
		mbMailingListModel.setInUserName(sampleUserModel.getEmailAddress());
		mbMailingListModel.setInPassword(sampleUserModel.getPassword());
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
		String urlSubject = null;

		if (index == 0) {
			messageId = mbThreadModel.getRootMessageId();
			parentMessageId = MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID;
			subject = String.valueOf(classPK);
			body = String.valueOf(classPK);
			urlSubject = String.valueOf(mbThreadModel.getRootMessageId());
		}
		else {
			messageId = counter.get();
			parentMessageId = mbThreadModel.getRootMessageId();
			subject = "N/A";
			body = "This is test comment " + index + ".";
			urlSubject = "test-comment-" + index;
		}

		return _newMBMessageModel(
			mbThreadModel.getGroupId(), classNameId, classPK,
			MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			mbThreadModel.getThreadId(), messageId,
			mbThreadModel.getRootMessageId(), parentMessageId, subject,
			urlSubject, body);
	}

	public List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel) {

		List<MBMessageModel> mbMessageModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_MB_MESSAGE_COUNT);

		mbMessageModels.add(
			_newMBMessageModel(
				mbThreadModel.getGroupId(), 0, 0, mbThreadModel.getCategoryId(),
				mbThreadModel.getThreadId(), mbThreadModel.getRootMessageId(),
				mbThreadModel.getRootMessageId(),
				MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, "Test Message 1",
				"test-message-1", "This is test message 1."));

		for (int i = 2; i <= BenchmarksPropsValues.MAX_MB_MESSAGE_COUNT; i++) {
			mbMessageModels.add(
				_newMBMessageModel(
					mbThreadModel.getGroupId(), 0, 0,
					mbThreadModel.getCategoryId(), mbThreadModel.getThreadId(),
					counter.get(), mbThreadModel.getRootMessageId(),
					mbThreadModel.getRootMessageId(), "Test Message " + i,
					"test-message-" + i, "This is test message " + i + "."));
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
		MBStatsUserModel mbStatsUserModel = new MBStatsUserModelImpl();

		// PK fields

		mbStatsUserModel.setStatsUserId(counter.get());

		// Group instance

		mbStatsUserModel.setGroupId(groupId);

		// Audit fields

		mbStatsUserModel.setUserId(SAMPLE_USER_ID);

		// Other fields

		mbStatsUserModel.setMessageCount(
			BenchmarksPropsValues.MAX_MB_CATEGORY_COUNT *
				BenchmarksPropsValues.MAX_MB_THREAD_COUNT *
					BenchmarksPropsValues.MAX_MB_MESSAGE_COUNT);
		mbStatsUserModel.setLastPostDate(new Date());

		return mbStatsUserModel;
	}

	public MBThreadFlagModel newMBThreadFlagModel(MBThreadModel mbThreadModel) {
		MBThreadFlagModel mbThreadFlagModel = new MBThreadFlagModelImpl();

		// UUID

		mbThreadFlagModel.setUuid(SequentialUUID.generate());

		// PK fields

		mbThreadFlagModel.setThreadFlagId(counter.get());

		// Group instance

		mbThreadFlagModel.setGroupId(mbThreadModel.getGroupId());

		// Audit fields

		mbThreadFlagModel.setCompanyId(COMPANY_ID);
		mbThreadFlagModel.setUserId(SAMPLE_USER_ID);
		mbThreadFlagModel.setUserName(SAMPLE_USER_NAME);
		mbThreadFlagModel.setCreateDate(new Date());
		mbThreadFlagModel.setModifiedDate(new Date());

		// Other fields

		mbThreadFlagModel.setThreadId(mbThreadModel.getThreadId());
		mbThreadFlagModel.setLastPublishDate(new Date());

		return mbThreadFlagModel;
	}

	public MBThreadModel newMBThreadModel(
		long threadId, long groupId, long rootMessageId) {

		return _newMBThreadModel(
			threadId, groupId, MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			rootMessageId);
	}

	public List<MBThreadModel> newMBThreadModels(
		MBCategoryModel mbCategoryModel) {

		List<MBThreadModel> mbThreadModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_MB_THREAD_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_MB_THREAD_COUNT; i++) {
			mbThreadModels.add(
				_newMBThreadModel(
					counter.get(), mbCategoryModel.getGroupId(),
					mbCategoryModel.getCategoryId(), counter.get()));
		}

		return mbThreadModels;
	}

	private MessageBoardDataFactory() {
	}

	private MBCategoryModel _newMBCategoryModel(long groupId, int index) {
		MBCategoryModel mbCategoryModel = new MBCategoryModelImpl();

		// UUID

		mbCategoryModel.setUuid(SequentialUUID.generate());

		// PK fields

		mbCategoryModel.setCategoryId(counter.get());

		// Group instance

		mbCategoryModel.setGroupId(groupId);

		// Audit fields

		mbCategoryModel.setCompanyId(COMPANY_ID);
		mbCategoryModel.setUserId(SAMPLE_USER_ID);
		mbCategoryModel.setUserName(SAMPLE_USER_NAME);
		mbCategoryModel.setCreateDate(new Date());
		mbCategoryModel.setModifiedDate(new Date());

		// Other fields

		mbCategoryModel.setParentCategoryId(
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
		mbCategoryModel.setName("Test Category " + index);
		mbCategoryModel.setDisplayStyle(
			MBCategoryConstants.DEFAULT_DISPLAY_STYLE);
		mbCategoryModel.setLastPublishDate(new Date());
		mbCategoryModel.setStatusDate(new Date());

		return mbCategoryModel;
	}

	private MBMessageModel _newMBMessageModel(
		long groupId, long classNameId, long classPK, long categoryId,
		long threadId, long messageId, long rootMessageId, long parentMessageId,
		String subject, String urlSubject, String body) {

		MBMessageModel mBMessageModel = new MBMessageModelImpl();

		// UUID

		mBMessageModel.setUuid(SequentialUUID.generate());

		// PK fields

		mBMessageModel.setMessageId(messageId);

		// Group instance

		mBMessageModel.setGroupId(groupId);

		// Audit fields

		mBMessageModel.setCompanyId(COMPANY_ID);
		mBMessageModel.setUserId(SAMPLE_USER_ID);
		mBMessageModel.setUserName(SAMPLE_USER_NAME);
		mBMessageModel.setCreateDate(new Date());
		mBMessageModel.setModifiedDate(new Date());

		// Other fields

		mBMessageModel.setClassNameId(classNameId);
		mBMessageModel.setClassPK(classPK);
		mBMessageModel.setCategoryId(categoryId);
		mBMessageModel.setThreadId(threadId);
		mBMessageModel.setRootMessageId(rootMessageId);
		mBMessageModel.setParentMessageId(parentMessageId);
		mBMessageModel.setSubject(subject);
		mBMessageModel.setUrlSubject(urlSubject + "-" + messageId);
		mBMessageModel.setBody(body);
		mBMessageModel.setFormat(MBMessageConstants.DEFAULT_FORMAT);
		mBMessageModel.setLastPublishDate(new Date());
		mBMessageModel.setStatusDate(new Date());

		return mBMessageModel;
	}

	private MBThreadModel _newMBThreadModel(
		long threadId, long groupId, long categoryId, long rootMessageId) {

		MBThreadModel mbThreadModel = new MBThreadModelImpl();

		// UUID

		mbThreadModel.setUuid(SequentialUUID.generate());

		// PK fields

		mbThreadModel.setThreadId(threadId);

		// Group instance

		mbThreadModel.setGroupId(groupId);

		// Audit fields

		mbThreadModel.setCompanyId(COMPANY_ID);
		mbThreadModel.setUserId(SAMPLE_USER_ID);
		mbThreadModel.setUserName(SAMPLE_USER_NAME);
		mbThreadModel.setCreateDate(new Date());
		mbThreadModel.setModifiedDate(new Date());

		// Other fields

		mbThreadModel.setCategoryId(categoryId);
		mbThreadModel.setRootMessageId(rootMessageId);
		mbThreadModel.setRootMessageUserId(SAMPLE_USER_ID);
		mbThreadModel.setLastPostByUserId(SAMPLE_USER_ID);
		mbThreadModel.setLastPostDate(new Date());
		mbThreadModel.setLastPublishDate(new Date());
		mbThreadModel.setStatusDate(new Date());

		return mbThreadModel;
	}

	private static MessageBoardDataFactory _messageBoardDataFactory =
		new MessageBoardDataFactory();

}