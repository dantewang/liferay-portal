<#list resourcePermissionDataFactory.classNameModels as classNameModel>
	${resourcePermissionDataFactory.toInsertSQL(classNameModel)}
</#list>