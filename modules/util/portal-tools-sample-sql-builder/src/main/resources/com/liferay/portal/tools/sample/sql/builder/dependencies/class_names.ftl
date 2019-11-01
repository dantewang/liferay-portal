<#list initContext.classNameModelValues as classNameModel>
	${resourcePermissionDataFactory.toInsertSQL(classNameModel)}
</#list>