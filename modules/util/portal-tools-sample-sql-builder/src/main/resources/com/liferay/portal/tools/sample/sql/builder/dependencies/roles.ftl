<#list userDataFactory.roleModels as roleModel>
	${insertSQLBuilder.toInsertSQL(roleModel)}
</#list>