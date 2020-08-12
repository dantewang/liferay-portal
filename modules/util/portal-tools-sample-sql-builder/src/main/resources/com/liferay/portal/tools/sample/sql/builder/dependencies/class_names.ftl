<#list userDataFactory.classNameModels as classNameModel>
	${insertSQLBuilder.toInsertSQL(classNameModel)}
</#list>