<#assign ddmStructureModel = journalDataFactory.defaultJournalDDMStructureModel />

<@insertDDMStructure
	_ddmStructureModel = ddmStructureModel
	_ddmStructureLayoutModel = journalDataFactory.defaultJournalDDMStructureLayoutModel
	_ddmStructureVersionModel = journalDataFactory.defaultJournalDDMStructureVersionModel
/>

<#assign ddmTemplateModel = journalDataFactory.defaultJournalDDMTemplateModel />

insert into DDMTemplate values ('${ddmTemplateModel.uuid}', ${ddmTemplateModel.templateId}, ${ddmTemplateModel.groupId}, ${ddmTemplateModel.companyId}, ${ddmTemplateModel.userId}, '${ddmTemplateModel.userName}', ${ddmTemplateModel.versionUserId}, '${ddmTemplateModel.versionUserName}', '${dataFactory.getDateString(ddmTemplateModel.createDate)}', '${dataFactory.getDateString(ddmTemplateModel.modifiedDate)}', ${ddmTemplateModel.classNameId}, ${ddmTemplateModel.classPK}, ${ddmTemplateModel.resourceClassNameId}, '${ddmTemplateModel.templateKey}', '${ddmTemplateModel.version}', '${ddmTemplateModel.name}', '${ddmTemplateModel.description}', '${ddmTemplateModel.type}', '${ddmTemplateModel.mode}', '${ddmTemplateModel.language}', '${ddmTemplateModel.script}', ${ddmTemplateModel.cacheable?string}, ${ddmTemplateModel.smallImage?string}, ${ddmTemplateModel.smallImageId}, '${ddmTemplateModel.smallImageURL}', '${dataFactory.getDateString(ddmTemplateModel.lastPublishDate)}');

<@insertResourcePermissions
	_entry = ddmTemplateModel
/>

<#assign
	journalArticlePageCounts = counterDataFactory.getSequence(initContext.maxJournalArticlePageCount)

	resourcePermissionModels = resourcePermissionDataFactory.newResourcePermissionModels("com.liferay.journal", groupId)
/>

<#list resourcePermissionModels as resourcePermissionModel>
	<@insertResourcePermission
		_resourcePermissionModel = resourcePermissionModel
	/>
</#list>

<#list journalArticlePageCounts as journalArticlePageCount>
	<#assign
		portletIdPrefix = "com_liferay_journal_content_web_portlet_JournalContentPortlet_INSTANCE_TEST_" + journalArticlePageCount + "_"

		layoutModel = layoutDataFactory.newLayoutModel(groupId, groupId + "_journal_article_" + journalArticlePageCount, "", journalDataFactory.getJournalArticleLayoutColumn(portletIdPrefix))
	/>

	${layoutCSVWriter.write(layoutModel.friendlyURL + "\n")}

	<@insertLayout
		_layoutModel = layoutModel
	/>

	<#assign portletPreferencesModels = portletPreferenceDataFactory.newJournalPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		<@insertPortletPreferences
			_portletPreferencesModel = portletPreferencesModel
		/>
	</#list>

	<#assign journalArticleCounts = counterDataFactory.getSequence(initContext.maxJournalArticleCount) />

	<#list journalArticleCounts as journalArticleCount>
		<#assign journalArticleResourceModel = journalDataFactory.newJournalArticleResourceModel(groupId) />

		insert into JournalArticleResource values ('${journalArticleResourceModel.uuid}', ${journalArticleResourceModel.resourcePrimKey}, ${journalArticleResourceModel.groupId}, ${journalArticleResourceModel.companyId}, '${journalArticleResourceModel.articleId}');

		<#assign versionCounts = counterDataFactory.getSequence(initContext.maxJournalArticleVersionCount) />

		<#list versionCounts as versionCount>
			<#assign journalArticleModel = journalDataFactory.newJournalArticleModel(journalArticleResourceModel, journalArticleCount, versionCount) />

			insert into JournalArticle values ('${journalArticleModel.uuid}', ${journalArticleModel.id}, ${journalArticleModel.resourcePrimKey}, ${journalArticleModel.groupId}, ${journalArticleModel.companyId}, ${journalArticleModel.userId}, '${journalArticleModel.userName}', '${dataFactory.getDateString(journalArticleModel.createDate)}', '${dataFactory.getDateString(journalArticleModel.modifiedDate)}', ${journalArticleModel.folderId}, ${journalArticleModel.classNameId}, ${journalArticleModel.classPK}, '', '${journalArticleModel.articleId}', ${journalArticleModel.version}, '${journalArticleModel.urlTitle}', '${journalArticleModel.content}', '${journalArticleModel.DDMStructureKey}', '${journalArticleModel.DDMTemplateKey}', '${journalArticleModel.defaultLanguageId}', '${journalArticleModel.layoutUuid}', '${dataFactory.getDateString(journalArticleModel.displayDate)}', '${dataFactory.getDateString(journalArticleModel.expirationDate)}', '${dataFactory.getDateString(journalArticleModel.reviewDate)}', ${journalArticleModel.indexable?string}, ${journalArticleModel.smallImage?string}, ${journalArticleModel.smallImageId}, '${journalArticleModel.smallImageURL}', '${dataFactory.getDateString(journalArticleModel.lastPublishDate)}', ${journalArticleModel.status}, ${journalArticleModel.statusByUserId}, '${journalArticleModel.statusByUserName}', '${dataFactory.getDateString(journalArticleModel.statusDate)}');

			<#assign journalArticleLocalizationModel = journalDataFactory.newJournalArticleLocalizationModel(journalArticleModel, journalArticleCount, versionCount) />

			insert into JournalArticleLocalization values (${journalArticleLocalizationModel.articleLocalizationId}, ${journalArticleLocalizationModel.companyId}, ${journalArticleLocalizationModel.articlePK}, '${journalArticleLocalizationModel.title}', '${journalArticleLocalizationModel.description}', '${journalArticleLocalizationModel.languageId}');

			<#assign ddmTemplateLinkModel = journalDataFactory.newDDMTemplateLinkModel(journalArticleModel, ddmTemplateModel.templateId) />

			insert into DDMTemplateLink values (${ddmTemplateLinkModel.templateLinkId}, ${ddmTemplateLinkModel.companyId}, ${ddmTemplateLinkModel.classNameId}, ${ddmTemplateLinkModel.classPK}, ${ddmTemplateLinkModel.templateId});

			<@insertDDMStorageLink
				_ddmStorageLinkModel = journalDataFactory.newDDMStorageLinkModel(journalArticleModel, ddmStructureModel.structureId)
			/>

			<@insertSocialActivity
				_entry = journalArticleModel
			/>

			<#if versionCount = initContext.maxJournalArticleVersionCount>
				<@insertAssetEntry
					_entry = journalDataFactory.newObjectValuePair(journalArticleModel, journalArticleLocalizationModel)
					_categoryAndTag = true
				/>
			</#if>
		</#list>

		<@insertResourcePermissions
			_entry = journalArticleResourceModel
		/>

		<@insertMBDiscussion
			_classNameId = journalDataFactory.journalArticleClassNameId
			_classPK = journalArticleResourceModel.resourcePrimKey
			_groupId = groupId
			_maxCommentCount = 0
			_mbRootMessageId = counterDataFactory.getCounterNext()
			_mbThreadId = counterDataFactory.getCounterNext()
		/>

		<#assign portletPreferencesModel = portletPreferenceDataFactory.newPortletPreferencesModel(layoutModel.plid, portletIdPrefix + journalArticleCount, journalArticleResourceModel) />

		<@insertPortletPreferences
			_portletPreferencesModel = portletPreferencesModel
		/>

		<#assign journalContentSearchModel = journalDataFactory.newJournalContentSearchModel(journalArticleModel, layoutModel.plid) />

		insert into JournalContentSearch values (${journalContentSearchModel.contentSearchId}, ${journalContentSearchModel.groupId}, ${journalContentSearchModel.companyId}, ${journalContentSearchModel.privateLayout?string}, ${journalContentSearchModel.layoutId}, '${journalContentSearchModel.portletId}', '${journalContentSearchModel.articleId}');
	</#list>
</#list>