<#assign counterModels = counterDataFactory.newCounterModels() />

<#list counterModels as counterModel>
	<#if '${counterModel.name}' == 'com.liferay.counter.kernel.model.Counter'>
		update Counter set currentId = ${counterModel.currentId} where name = '${counterModel.name}';
	<#else>
		${resourcePermissionDataFactory.toInsertSQL(counterModel)}
	</#if>
</#list>