<#assign companyModel = dataFactory.newCompanyModel() />

${dataFactory.toInsertSQL(companyModel)}

${dataFactory.toInsertSQL(dataFactory.newAccountModel())}

${dataFactory.toInsertSQL(dataFactory.newVirtualHostModel())}

<#list dataFactory.newKaleoDefinitionModels() as kaleoDefinitionModel>
	${dataFactory.toInsertSQL(kaleoDefinitionModel)}
</#list>

${csvFileWriter.write("company", companyModel.companyId + "\n")}