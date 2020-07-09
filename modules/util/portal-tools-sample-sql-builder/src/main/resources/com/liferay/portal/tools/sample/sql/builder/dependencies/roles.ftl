<#list userDataFactory.roleModels as roleModel>
	${dataFactory.toInsertSQL(roleModel)}
</#list>