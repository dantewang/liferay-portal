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
import com.liferay.message.boards.model.MBThreadFlagModel;
import com.liferay.message.boards.model.MBThreadModel;
import com.liferay.message.boards.model.impl.MBCategoryModelImpl;
import com.liferay.message.boards.model.impl.MBDiscussionModelImpl;
import com.liferay.message.boards.model.impl.MBMailingListModelImpl;
import com.liferay.message.boards.model.impl.MBMessageModelImpl;
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

	public MessageBoardDataFactory(UserDataFactory userDataFactory) {
		_userDataFactory = userDataFactory;
	}

	public List<MBCategoryModel> newMBCategoryModels(long groupId) {
		int maxMBCategoryCount = PropsValues.MAX_MB_CATEGORY_COUNT;

		List<MBCategoryModel> mbCategoryModels = new ArrayList<>(
			maxMBCategoryCount);

		for (int i = 1; i <= maxMBCategoryCount; i++) {
			mbCategoryModels.add(newMBCategoryModel(groupId, i));
		}

		return mbCategoryModels;
	}

	public MBDiscussionModel newMBDiscussionModel(
		long groupId, long classNameId, long classPK, long threadId) {

		MBDiscussionModel mbDiscussionModel = new MBDiscussionModelImpl();

		mbDiscussionModel.setUuid(SequentialUUID.generate());
		mbDiscussionModel.setDiscussionId(counter.get());
		mbDiscussionModel.setGroupId(groupId);
		mbDiscussionModel.setCompanyId(companyId);
		mbDiscussionModel.setUserId(sampleUserId);
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

		UserModel sampleUserModel = _userDataFactory.getSampleUserModel();

		mbMailingListModel.setUuid(SequentialUUID.generate());
		mbMailingListModel.setMailingListId(counter.get());

		mbMailingListModel.setGroupId(mbCategoryModel.getGroupId());
		mbMailingListModel.setCompanyId(companyId);
		mbMailingListModel.setUserId(sampleUserId);
		mbMailingListModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbMailingListModel.setCreateDate(new Date());
		mbMailingListModel.setModifiedDate(new Date());
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

		return _newMBMessageModel(
			mbThreadModel.getGroupId(), classNameId, classPK,
			MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			mbThreadModel.getThreadId(), messageId,
			mbThreadModel.getRootMessageId(), parentMessageId, subject, body);
	}

	public List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel) {

		int maxMBMessageCount = PropsValues.MAX_MB_MESSAGE_COUNT;

		List<MBMessageModel> mbMessageModels = new ArrayList<>(
			maxMBMessageCount);

		mbMessageModels.add(
			_newMBMessageModel(
				mbThreadModel.getGroupId(), 0, 0, mbThreadModel.getCategoryId(),
				mbThreadModel.getThreadId(), mbThreadModel.getRootMessageId(),
				mbThreadModel.getRootMessageId(),
				MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, "Test Message 1",
				"This is test message 1."));

		for (int i = 2; i <= maxMBMessageCount; i++) {
			mbMessageModels.add(
				_newMBMessageModel(
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

	public MBThreadFlagModel newMBThreadFlagModel(MBThreadModel mbThreadModel) {
		MBThreadFlagModel mbThreadFlagModel = new MBThreadFlagModelImpl();

		mbThreadFlagModel.setUuid(SequentialUUID.generate());
		mbThreadFlagModel.setThreadFlagId(counter.get());

		mbThreadFlagModel.setGroupId(mbThreadModel.getGroupId());
		mbThreadFlagModel.setCompanyId(companyId);
		mbThreadFlagModel.setUserId(sampleUserId);
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

		return _newMBThreadModel(
			threadId, groupId, MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			rootMessageId, messageCount);
	}

	public List<MBThreadModel> newMBThreadModels(
		MBCategoryModel mbCategoryModel) {

		int maxMBThreadCount = PropsValues.MAX_MB_THREAD_COUNT;

		int maxMBMessageCount = PropsValues.MAX_MB_MESSAGE_COUNT;

		List<MBThreadModel> mbThreadModels = new ArrayList<>(maxMBThreadCount);

		for (int i = 0; i < maxMBThreadCount; i++) {
			mbThreadModels.add(
				_newMBThreadModel(
					counter.get(), mbCategoryModel.getGroupId(),
					mbCategoryModel.getCategoryId(), counter.get(),
					maxMBMessageCount));
		}

		return mbThreadModels;
	}

	protected MBCategoryModel newMBCategoryModel(long groupId, int index) {
		MBCategoryModel mbCategoryModel = new MBCategoryModelImpl();

		int maxMBThreadCount = PropsValues.MAX_MB_THREAD_COUNT;
		int maxMBMessageCount = PropsValues.MAX_MB_MESSAGE_COUNT;

		mbCategoryModel.setUuid(SequentialUUID.generate());
		mbCategoryModel.setCategoryId(counter.get());

		mbCategoryModel.setGroupId(groupId);
		mbCategoryModel.setCompanyId(companyId);
		mbCategoryModel.setUserId(sampleUserId);
		mbCategoryModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbCategoryModel.setCreateDate(new Date());
		mbCategoryModel.setModifiedDate(new Date());
		mbCategoryModel.setParentCategoryId(
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
		mbCategoryModel.setName(
			DataFactoryConstants.MB_CATEGORY_NAME_PREFIX + index);
		mbCategoryModel.setDisplayStyle(
			MBCategoryConstants.DEFAULT_DISPLAY_STYLE);
		mbCategoryModel.setThreadCount(maxMBThreadCount);
		mbCategoryModel.setMessageCount(maxMBThreadCount * maxMBMessageCount);
		mbCategoryModel.setLastPostDate(new Date());
		mbCategoryModel.setLastPublishDate(new Date());
		mbCategoryModel.setStatusDate(new Date());

		return mbCategoryModel;
	}

	private MBMessageModel _newMBMessageModel(
		long groupId, long classNameId, long classPK, long categoryId,
		long threadId, long messageId, long rootMessageId, long parentMessageId,
		String subject, String body) {

		MBMessageModel mBMessageModel = new MBMessageModelImpl();

		mBMessageModel.setUuid(SequentialUUID.generate());
		mBMessageModel.setMessageId(messageId);
		mBMessageModel.setGroupId(groupId);
		mBMessageModel.setCompanyId(companyId);
		mBMessageModel.setUserId(sampleUserId);
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

	private MBThreadModel _newMBThreadModel(
		long threadId, long groupId, long categoryId, long rootMessageId,
		int messageCount) {

		MBThreadModel mbThreadModel = new MBThreadModelImpl();

		mbThreadModel.setUuid(SequentialUUID.generate());
		mbThreadModel.setThreadId(threadId);
		mbThreadModel.setGroupId(groupId);
		mbThreadModel.setCompanyId(companyId);
		mbThreadModel.setUserId(sampleUserId);
		mbThreadModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbThreadModel.setCreateDate(new Date());
		mbThreadModel.setModifiedDate(new Date());
		mbThreadModel.setCategoryId(categoryId);
		mbThreadModel.setRootMessageId(rootMessageId);
		mbThreadModel.setRootMessageUserId(sampleUserId);
		mbThreadModel.setMessageCount(messageCount);
		mbThreadModel.setLastPostByUserId(sampleUserId);
		mbThreadModel.setLastPostDate(new Date());
		mbThreadModel.setLastPublishDate(new Date());
		mbThreadModel.setStatusDate(new Date());

		return mbThreadModel;
	}

	private final UserDataFactory _userDataFactory;

}