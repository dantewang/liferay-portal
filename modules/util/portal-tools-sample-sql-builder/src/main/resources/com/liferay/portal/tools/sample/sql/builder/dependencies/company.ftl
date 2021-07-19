<#assign companyModel = dataFactory.newCompanyModel() />

${dataFactory.toInsertSQL(companyModel)}

${dataFactory.toInsertSQL(dataFactory.newAccountModel())}

${dataFactory.toInsertSQL(dataFactory.newVirtualHostModel())}

<#list dataFactory.newKaleoDefinitionModels() as kaleoDefinitionModel>
	${dataFactory.toInsertSQL(kaleoDefinitionModel)}

	<#assign
			startKaleoNodeModel = dataFactory.newStartKaleoNodeModel(kaleoDefinitionModel)
			kaleoDefinitionVersionModel = dataFactory.newKaleoDefinitionVersionModel(kaleoDefinitionModel, startKaleoNodeModel)
		/>

	${dataFactory.toInsertSQL(startKaleoNodeModel)}

	${dataFactory.toInsertSQL(kaleoDefinitionVersionModel)}

	<#list dataFactory.newKaleoNodeModels(kaleoDefinitionModel, kaleoDefinitionVersionModel) as kaleoNodeModel>
		${dataFactory.toInsertSQL(kaleoNodeModel)}

		${dataFactory.toInsertSQL(dataFactory.newKaleoTaskModel(kaleoNodeModel))}
	</#list>
</#list>

${csvFileWriter.write("company", companyModel.companyId + "\n")}