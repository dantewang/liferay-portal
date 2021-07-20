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

		<#assign kaleoTaskModel = dataFactory.newKaleoTaskModel(kaleoNodeModel) />

		${dataFactory.toInsertSQL(kaleoTaskModel)}

		<#list dataFactory.newKaleoTaskAssignmentModels(kaleoTaskModel, kaleoDefinitionModel) as kaleoTaskAssignmentModel>
			${dataFactory.toInsertSQL(kaleoTaskAssignmentModel)}
		</#list>
	</#list>
</#list>

${csvFileWriter.write("company", companyModel.companyId + "\n")}