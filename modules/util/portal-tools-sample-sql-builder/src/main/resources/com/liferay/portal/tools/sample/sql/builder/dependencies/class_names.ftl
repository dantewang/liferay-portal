<#list dataFactoryContext.classNameModels as classNameModel>
	${resourcePermissionDataFactory.toInsertSQL(classNameModel)}
</#list>