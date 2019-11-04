<#list dataFactoryContext.classNameModelValues as classNameModel>
	${resourcePermissionDataFactory.toInsertSQL(classNameModel)}
</#list>