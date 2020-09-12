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

import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.blogs.social.BlogsActivityKeys;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.journal.constants.JournalActivityKeys;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.message.boards.model.MBMessageModel;
import com.liferay.message.boards.social.MBActivityKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portlet.documentlibrary.social.DLActivityKeys;
import com.liferay.portlet.social.model.impl.SocialActivityModelImpl;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityModel;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.social.WikiActivityKeys;

/**
 * @author Lily Chi
 */
public class SocialActivityDataFactory extends BaseDataFactory {

	public static SocialActivityDataFactory getInstance() {
		return _socialActivityDataFactory;
	}

	public SocialActivityModel newSocialActivityModel(
		BlogsEntryModel blogsEntryModel, long classNameId) {

		return _newSocialActivityModel(
			blogsEntryModel.getGroupId(), classNameId,
			blogsEntryModel.getEntryId(), BlogsActivityKeys.ADD_ENTRY,
			"{\"title\":\"" + blogsEntryModel.getTitle() + "\"}");
	}

	public SocialActivityModel newSocialActivityModel(
		DLFileEntryModel dlFileEntryModel, long classNameId) {

		return _newSocialActivityModel(
			dlFileEntryModel.getGroupId(), classNameId,
			dlFileEntryModel.getFileEntryId(), DLActivityKeys.ADD_FILE_ENTRY,
			StringPool.BLANK);
	}

	public SocialActivityModel newSocialActivityModel(
		JournalArticleModel journalArticleModel, long classNameId) {

		int type = JournalActivityKeys.UPDATE_ARTICLE;

		if (journalArticleModel.getVersion() ==
				JournalArticleConstants.VERSION_DEFAULT) {

			type = JournalActivityKeys.ADD_ARTICLE;
		}

		return _newSocialActivityModel(
			journalArticleModel.getGroupId(), classNameId,
			journalArticleModel.getResourcePrimKey(), type,
			"{\"title\":\"" + journalArticleModel.getUrlTitle() + "\"}");
	}

	public SocialActivityModel newSocialActivityModel(
		MBMessageModel mbMessageModel, long wikiPageClassNameId,
		long mbMessageClassNameId) {

		long classNameId = mbMessageModel.getClassNameId();
		long classPK = mbMessageModel.getClassPK();

		int type = 0;
		String extraData = null;

		if (classNameId == wikiPageClassNameId) {
			extraData = "{\"version\":1}";

			type = WikiActivityKeys.ADD_PAGE;
		}
		else if (classNameId == 0) {
			extraData = "{\"title\":\"" + mbMessageModel.getSubject() + "\"}";

			type = MBActivityKeys.ADD_MESSAGE;

			classNameId = mbMessageClassNameId;
			classPK = mbMessageModel.getMessageId();
		}
		else {
			StringBundler sb = new StringBundler(5);

			sb.append("{\"messageId\": \"");
			sb.append(mbMessageModel.getMessageId());
			sb.append("\", \"title\": ");
			sb.append(mbMessageModel.getSubject());
			sb.append("}");

			extraData = sb.toString();

			type = SocialActivityConstants.TYPE_ADD_COMMENT;
		}

		return _newSocialActivityModel(
			mbMessageModel.getGroupId(), classNameId, classPK, type, extraData);
	}

	private SocialActivityDataFactory() {
		_timeCounter = new SimpleCounter();
	}

	private SocialActivityModel _newSocialActivityModel(
		long groupId, long classNameId, long classPK, int type,
		String extraData) {

		SocialActivityModel socialActivityModel = new SocialActivityModelImpl();

		// PK fields

		socialActivityModel.setActivityId(socialActivityCounter.get());

		// Group instance

		socialActivityModel.setGroupId(groupId);

		// Audit fields

		socialActivityModel.setCompanyId(COMPANY_ID);
		socialActivityModel.setUserId(SAMPLE_USER_ID);
		socialActivityModel.setCreateDate(_CURRENT_TIME + _timeCounter.get());

		// Other fields

		socialActivityModel.setClassNameId(classNameId);
		socialActivityModel.setClassPK(classPK);
		socialActivityModel.setType(type);
		socialActivityModel.setExtraData(extraData);

		return socialActivityModel;
	}

	private static final long _CURRENT_TIME = System.currentTimeMillis();

	private static SocialActivityDataFactory _socialActivityDataFactory =
		new SocialActivityDataFactory();

	private final SimpleCounter _timeCounter;

}