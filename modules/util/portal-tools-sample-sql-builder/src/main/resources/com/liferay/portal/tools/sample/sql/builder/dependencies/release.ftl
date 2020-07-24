<#assign releaseModels = releaseDataFactory.newReleaseModels() />

<#list releaseModels as releaseModel>
	${dataFactory.toInsertSQL(releaseModel)}
</#list>