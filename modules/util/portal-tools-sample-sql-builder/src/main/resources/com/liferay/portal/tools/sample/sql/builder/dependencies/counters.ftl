<#assign counterModels = dataFactory.newCounterModels() />

<#list counterModels as counterModel>
	<#if "${counterModel.name}"?contains("#")>
		${dataFactory.toInsertSQL(counterModel)}
	<#elseif '${counterModel.name}' == 'com.liferay.social.kernel.model.SocialActivity'>
		${dataFactory.toInsertSQL(counterModel)}
	<#else>
		update Counter set currentId = ${counterModel.currentId} where name = '${counterModel.name}';
	</#if>
</#list>