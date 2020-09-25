<#list classNameDataFactory.classNameModels as classNameModel>
	${dataFactory.toInsertSQL(classNameModel)}
</#list>