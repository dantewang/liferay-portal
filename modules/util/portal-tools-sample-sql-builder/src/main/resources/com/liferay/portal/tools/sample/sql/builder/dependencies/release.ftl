<#assign releaseModels = releaseDataFactory.newReleaseModels() />

<#list releaseModels as releaseModel>
	${resourcePermissionDataFactory.toInsertSQL(releaseModel)}
</#list>