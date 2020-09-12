<#assign releaseModels = releaseDataFactory.newReleaseModels() />

<#list releaseModels as releaseModel>
	${insertSQLBuilder.toInsertSQL(releaseModel)}
</#list>