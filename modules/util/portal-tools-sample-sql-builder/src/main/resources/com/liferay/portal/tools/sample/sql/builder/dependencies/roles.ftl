<#list dataFactory.newRoleModels(dataFactory.getClassNameId("com.liferay.portal.kernel.model.Role")) as roleModel>
	${dataFactory.toInsertSQL(roleModel)}
</#list>