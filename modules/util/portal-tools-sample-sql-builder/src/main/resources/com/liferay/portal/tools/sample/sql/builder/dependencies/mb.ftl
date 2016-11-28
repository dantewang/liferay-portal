<#assign mbCategoryModels = messageBoardDataFactory.newMBCategoryModels(groupId) />

<#list mbCategoryModels as mbCategoryModel>
	insert into MBCategory values ('${mbCategoryModel.uuid}', ${mbCategoryModel.categoryId}, ${mbCategoryModel.groupId}, ${mbCategoryModel.companyId}, ${mbCategoryModel.userId}, '${mbCategoryModel.userName}', '${initContext.getDateString(mbCategoryModel.createDate)}', '${initContext.getDateString(mbCategoryModel.modifiedDate)}', ${mbCategoryModel.parentCategoryId}, '${mbCategoryModel.name}', '${mbCategoryModel.description}', '${mbCategoryModel.displayStyle}', ${mbCategoryModel.threadCount}, ${mbCategoryModel.messageCount}, '${initContext.getDateString(mbCategoryModel.lastPostDate)}', '${initContext.getDateString(mbCategoryModel.lastPublishDate)}', ${mbCategoryModel.status}, ${mbCategoryModel.statusByUserId}, '${mbCategoryModel.statusByUserName}', '${initContext.getDateString(mbCategoryModel.statusDate)}');

	<@insertResourcePermissions
		_entry = mbCategoryModel
	/>

	<#assign mbMailingListModel = messageBoardDataFactory.newMBMailingListModel(mbCategoryModel) />

	insert into MBMailingList values ('${mbMailingListModel.uuid}', ${mbMailingListModel.mailingListId}, ${mbMailingListModel.groupId}, ${mbMailingListModel.companyId}, ${mbMailingListModel.userId}, '${mbMailingListModel.userName}', '${initContext.getDateString(mbMailingListModel.createDate)}', '${initContext.getDateString(mbMailingListModel.modifiedDate)}', ${mbMailingListModel.categoryId}, '${mbMailingListModel.emailAddress}', '${mbMailingListModel.inProtocol}', '${mbMailingListModel.inServerName}', ${mbMailingListModel.inServerPort}, ${mbMailingListModel.inUseSSL?string}, '${mbMailingListModel.inUserName}', '${mbMailingListModel.inPassword}', ${mbMailingListModel.inReadInterval}, '${mbMailingListModel.outEmailAddress}', ${mbMailingListModel.outCustom?string}, '${mbMailingListModel.outServerName}', ${mbMailingListModel.outServerPort}, ${mbMailingListModel.outUseSSL?string}, '${mbMailingListModel.outUserName}', '${mbMailingListModel.outPassword}', ${mbMailingListModel.allowAnonymous?string}, ${mbMailingListModel.active?string});

	<#assign mbThreadModels = messageBoardDataFactory.newMBThreadModels(mbCategoryModel) />

	<#list mbThreadModels as mbThreadModel>
		insert into MBThread values ('${mbThreadModel.uuid}', ${mbThreadModel.threadId}, ${mbThreadModel.groupId}, ${mbThreadModel.companyId}, ${mbThreadModel.userId}, '${mbThreadModel.userName}', '${initContext.getDateString(mbThreadModel.createDate)}', '${initContext.getDateString(mbThreadModel.modifiedDate)}', ${mbThreadModel.categoryId}, ${mbThreadModel.rootMessageId}, ${mbThreadModel.rootMessageUserId}, ${mbThreadModel.messageCount}, ${mbThreadModel.viewCount}, ${mbThreadModel.lastPostByUserId}, '${initContext.getDateString(mbThreadModel.lastPostDate)}', ${mbThreadModel.priority}, ${mbThreadModel.question?string}, '${initContext.getDateString(mbThreadModel.lastPublishDate)}', ${mbThreadModel.status}, ${mbThreadModel.statusByUserId}, '${mbThreadModel.statusByUserName}', '${initContext.getDateString(mbThreadModel.statusDate)}');

		<@insertSubscription
			_entry = mbThreadModel
		/>

		<@insertAssetEntry
			_entry = mbThreadModel
		/>

		<#assign mbThreadFlagModel = messageBoardDataFactory.newMBThreadFlagModel(mbThreadModel) />

		insert into MBThreadFlag values ('${mbThreadFlagModel.uuid}', ${mbThreadFlagModel.threadFlagId}, ${mbThreadFlagModel.groupId}, ${mbThreadFlagModel.companyId}, ${mbThreadFlagModel.userId}, '${mbThreadFlagModel.userName}', '${initContext.getDateString(mbThreadFlagModel.createDate)}', '${initContext.getDateString(mbThreadFlagModel.modifiedDate)}', ${mbThreadFlagModel.threadId}, '${initContext.getDateString(mbThreadFlagModel.lastPublishDate)}');

		<#assign mbMessageModels = messageBoardDataFactory.newMBMessageModels(mbThreadModel) />

		<#list mbMessageModels as mbMessageModel>
			<@insertMBMessage
				_mbMessageModel = mbMessageModel
			/>

			<@insertSocialActivity
				_entry = mbMessageModel
			/>
		</#list>

		${messageBoardCSVWriter.write(mbCategoryModel.categoryId + "," + mbThreadModel.threadId + "," + mbThreadModel.rootMessageId + "\n")}
	</#list>
</#list>