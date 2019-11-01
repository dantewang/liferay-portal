<#list dataFactory.classNameModelValues as classNameModel>
	${dataFactory.toInsertSQL(classNameModel)}
</#list>