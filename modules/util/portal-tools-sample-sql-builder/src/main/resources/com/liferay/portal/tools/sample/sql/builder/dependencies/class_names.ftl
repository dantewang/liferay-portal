<#list initContext.classNameModelValues as classNameModelValue>
	insert into ClassName_ values (${classNameModelValue.mvccVersion}, ${classNameModelValue.classNameId}, '${classNameModelValue.value}');
</#list>