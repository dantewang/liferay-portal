<#assign wikiNodeModels = wikiDataFactory.newWikiNodeModels(groupId) />

<#list wikiNodeModels as wikiNodeModel>
	insert into WikiNode values ('${wikiNodeModel.uuid}', ${wikiNodeModel.nodeId}, ${wikiNodeModel.groupId}, ${wikiNodeModel.companyId}, ${wikiNodeModel.userId}, '${wikiNodeModel.userName}', '${initContext.getDateString(wikiNodeModel.createDate)}', '${initContext.getDateString(wikiNodeModel.modifiedDate)}', '${wikiNodeModel.name}', '${wikiNodeModel.description}', '${initContext.getDateString(wikiNodeModel.lastPostDate)}', '${initContext.getDateString(wikiNodeModel.lastPublishDate)}', ${wikiNodeModel.status}, ${wikiNodeModel.statusByUserId}, '${wikiNodeModel.statusByUserName}', '${initContext.getDateString(wikiNodeModel.statusDate)}');

	<@insertResourcePermissions
		_entry = wikiNodeModel
	/>

	<#assign wikiPageModels = wikiDataFactory.newWikiPageModels(wikiNodeModel) />

	<#list wikiPageModels as wikiPageModel>
		insert into WikiPage values ('${wikiPageModel.uuid}', ${wikiPageModel.pageId}, ${wikiPageModel.resourcePrimKey}, ${wikiPageModel.groupId}, ${wikiPageModel.companyId}, ${wikiPageModel.userId}, '${wikiPageModel.userName}', '${initContext.getDateString(wikiPageModel.createDate)}', '${initContext.getDateString(wikiPageModel.modifiedDate)}', ${wikiPageModel.nodeId}, '${wikiPageModel.title}', ${wikiPageModel.version}, ${wikiPageModel.minorEdit?string}, '${wikiPageModel.content}', '${wikiPageModel.summary}', '${wikiPageModel.format}', ${wikiPageModel.head?string}, '${wikiPageModel.parentTitle}', '${wikiPageModel.redirectTitle}', '${initContext.getDateString(wikiPageModel.lastPublishDate)}', ${wikiPageModel.status}, ${wikiPageModel.statusByUserId}, '${wikiPageModel.statusByUserName}', ${wikiPageModel.statusDate!'null'});

		<@insertResourcePermissions
			_entry = wikiPageModel
		/>

		<@insertSubscription
			_entry = wikiPageModel
		/>

		<#assign wikiPageResourceModel = wikiDataFactory.newWikiPageResourceModel(wikiPageModel) />

		insert into WikiPageResource values ('${wikiPageResourceModel.uuid}', ${wikiPageResourceModel.resourcePrimKey}, ${wikiPageResourceModel.groupId}, '${wikiPageResourceModel.companyId}', ${wikiPageResourceModel.nodeId}, '${wikiPageResourceModel.title}');

		<@insertAssetEntry
			_entry = wikiPageModel
			_categoryAndTag = true
		/>

		<#assign
			mbRootMessageId = counterDataFactory.getCounterNext()
			mbThreadId = counterDataFactory.getCounterNext()
		/>

		<@insertMBDiscussion
			_classNameId = wikiDataFactory.wikiPageClassNameId
			_classPK = wikiPageModel.resourcePrimKey
			_groupId = groupId
			_maxCommentCount = initContext.maxWikiPageCommentCount
			_mbRootMessageId = mbRootMessageId
			_mbThreadId = mbThreadId
		/>

		${wikiCSVWriter.write(wikiNodeModel.nodeId + "," + wikiNodeModel.name + "," + wikiPageModel.resourcePrimKey + "," + wikiPageModel.title + "," + mbThreadId + "," + mbRootMessageId + "\n")}
	</#list>
</#list>