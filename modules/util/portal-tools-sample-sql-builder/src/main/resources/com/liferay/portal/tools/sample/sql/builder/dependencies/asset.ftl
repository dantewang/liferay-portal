<#list assetDataFactory.assetVocabularyModels as assetVocabularyModel>
	insert into AssetVocabulary values ('${assetVocabularyModel.uuid}', ${assetVocabularyModel.vocabularyId}, ${assetVocabularyModel.groupId}, ${assetVocabularyModel.companyId}, ${assetVocabularyModel.userId}, '${assetVocabularyModel.userName}', '${initContext.getDateString(assetVocabularyModel.createDate)}', '${initContext.getDateString(assetVocabularyModel.modifiedDate)}', '${assetVocabularyModel.name}', '${assetVocabularyModel.title}', '${assetVocabularyModel.description}', '${assetVocabularyModel.settings}', '${initContext.getDateString(assetVocabularyModel.lastPublishDate)}');

	<@insertResourcePermissions
		_entry = assetVocabularyModel
	/>
</#list>

<#list assetDataFactory.assetCategoryModels as assetCategoryModel>
	insert into AssetCategory values ('${assetCategoryModel.uuid}', ${assetCategoryModel.categoryId}, ${assetCategoryModel.groupId}, ${assetCategoryModel.companyId}, ${assetCategoryModel.userId}, '${assetCategoryModel.userName}', '${initContext.getDateString(assetCategoryModel.createDate)}', '${initContext.getDateString(assetCategoryModel.modifiedDate)}', ${assetCategoryModel.parentCategoryId}, ${assetCategoryModel.leftCategoryId}, ${assetCategoryModel.rightCategoryId}, '${assetCategoryModel.name}', '${assetCategoryModel.title}', '${assetCategoryModel.description}', ${assetCategoryModel.vocabularyId}, '${initContext.getDateString(assetCategoryModel.lastPublishDate)}');

	<@insertResourcePermissions
		_entry = assetCategoryModel
	/>
</#list>

<#list assetDataFactory.assetTagModels as assetTagModel>
	insert into AssetTag values ('${assetTagModel.uuid}', ${assetTagModel.tagId}, ${assetTagModel.groupId}, ${assetTagModel.companyId}, ${assetTagModel.userId}, '${assetTagModel.userName}', '${initContext.getDateString(assetTagModel.createDate)}', '${initContext.getDateString(assetTagModel.modifiedDate)}', '${assetTagModel.name}', ${assetTagModel.assetCount}, '${initContext.getDateString(assetTagModel.lastPublishDate)}');

	<@insertResourcePermissions
		_entry = assetTagModel
	/>
</#list>

<#list assetDataFactory.assetTagStatsModels as assetTagStatsModel>
	insert into AssetTagStats values (${assetTagStatsModel.tagStatsId}, ${assetTagStatsModel.companyId}, ${assetTagStatsModel.tagId}, ${assetTagStatsModel.classNameId}, ${assetTagStatsModel.assetCount});
</#list>