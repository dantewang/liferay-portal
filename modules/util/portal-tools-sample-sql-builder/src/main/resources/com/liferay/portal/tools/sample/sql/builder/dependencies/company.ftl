<#assign
	companyModel = dataFactory.newDefaultCompanyModel()
	virtualHostModel = dataFactory.newVirtualHostModel()
/>

${dataFactory.toInsertSQL(companyModel)}

${dataFactory.toInsertSQL(dataFactory.newAccountModel())}

${dataFactory.toInsertSQL(virtualHostModel)}

${csvFileWriter.write("company", companyModel.companyId + "\n")}

<#include "roles.ftl">

<#include "groups.ftl">

<#list dataFactory.getSequence(dataFactory.maxVirtualInstanceCount) as virtualInstanceCount>
	<#assign
		companyModel = dataFactory.newCompanyModel(virtualInstanceCount)
		virtualHostModel = dataFactory.newVirtualHostModel(companyModel)
	/>

	${dataFactory.toInsertSQL(companyModel)}

	${dataFactory.toInsertSQL(dataFactory.newAccountModel(companyModel))}

	${dataFactory.toInsertSQL(virtualHostModel)}

	${csvFileWriter.write("company", companyModel.companyId + "," + virtualHostModel.hostname+ "\n")}

	<#include "roles.ftl">

	<#include "groups.ftl">
</#list>