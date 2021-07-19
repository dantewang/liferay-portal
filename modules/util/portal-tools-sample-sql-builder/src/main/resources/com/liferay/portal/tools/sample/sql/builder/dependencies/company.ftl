<#list dataFactory.newCompanyModels() as companyModel>
	${dataFactory.setCompanyId(companyModel.companyId)}

	<#assign virtualHostModel = dataFactory.newVirtualHostModel(companyModel) />

	${dataFactory.toInsertSQL(companyModel)}

	${dataFactory.toInsertSQL(dataFactory.newAccountModel(companyModel))}

	${dataFactory.toInsertSQL(virtualHostModel)}

	${csvFileWriter.write("company", companyModel.companyId + "\n")}

	<#include "roles.ftl">

	<#include "groups.ftl">

	<#list dataFactory.newKaleoDefinitionModels() as kaleoDefinitionModel>
		${dataFactory.toInsertSQL(kaleoDefinitionModel)}

		<#assign startKaleoNodeModel = dataFactory.newStartKaleoNodeModel(kaleoDefinitionModel) />

		${dataFactory.toInsertSQL(startKaleoNodeModel)}
	</#list>
</#list>