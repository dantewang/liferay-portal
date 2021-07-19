<#assign companyModel = dataFactory.newCompanyModel() />

${dataFactory.toInsertSQL(companyModel)}

${dataFactory.toInsertSQL(dataFactory.newAccountModel())}

${dataFactory.toInsertSQL(dataFactory.newVirtualHostModel())}

<#list dataFactory.newKaleoDefinitionModels() as kaleoDefinitionModel>
	${dataFactory.toInsertSQL(kaleoDefinitionModel)}

	<#assign startKaleoNodeModel = dataFactory.newStartKaleoNodeModel(kaleoDefinitionModel) />

	${dataFactory.toInsertSQL(startKaleoNodeModel)}

	${dataFactory.toInsertSQL(dataFactory.newKaleoDefinitionVersionModel(kaleoDefinitionModel, startKaleoNodeModel))}

	<#list dataFactory.newKaleoNodeModels() as kaleoNodeModel>
		${dataFactory.toInsertSQL(kaleoNodeModel)}
	</#list>
</#list>

${csvFileWriter.write("company", companyModel.companyId + "\n")}