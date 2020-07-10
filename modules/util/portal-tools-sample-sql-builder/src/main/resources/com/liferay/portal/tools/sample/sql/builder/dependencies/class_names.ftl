<#list dataFactory.classNameModels as classNameModel>
	${resourcePermissionDataFactory.toInsertSQL(classNameModel)}
</#list>