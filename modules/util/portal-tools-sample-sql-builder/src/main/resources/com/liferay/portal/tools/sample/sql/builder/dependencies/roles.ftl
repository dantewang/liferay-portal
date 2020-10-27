<#list dataFactory.newRoleModels(dataFactory.getClassNameId("Role")) as roleModel>
	${dataFactory.toInsertSQL(roleModel)}
</#list>