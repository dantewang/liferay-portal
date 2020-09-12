<#list classNameDataFactory.classNameModels as classNameModel>
	${insertSQLBuilder.toInsertSQL(classNameModel)}
</#list>