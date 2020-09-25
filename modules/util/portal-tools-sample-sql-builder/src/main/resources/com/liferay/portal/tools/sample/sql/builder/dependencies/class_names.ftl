<#list classNameDataFactory.classNameModels as classNameModel>
	${resourcePermissionDataFactory.toInsertSQL(classNameModel)}
</#list>