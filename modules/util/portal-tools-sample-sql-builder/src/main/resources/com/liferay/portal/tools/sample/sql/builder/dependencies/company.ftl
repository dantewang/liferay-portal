<#assign companyModel = userDataFactory.newCompanyModel() />

${dataFactory.toInsertSQL(companyModel)}

${dataFactory.toInsertSQL(userDataFactory.newAccountModel())}

${dataFactory.toInsertSQL(userDataFactory.newVirtualHostModel())}

${csvFileWriter.write("company", companyModel.companyId + "\n")}