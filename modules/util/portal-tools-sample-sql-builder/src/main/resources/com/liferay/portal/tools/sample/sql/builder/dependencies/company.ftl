<#assign companyModel = userDataFactory.newCompanyModel() />

${insertSQLBuilder.toInsertSQL(companyModel)}

${insertSQLBuilder.toInsertSQL(userDataFactory.newAccountModel())}

${insertSQLBuilder.toInsertSQL(userDataFactory.newVirtualHostModel())}

${csvFileWriter.write("company", companyModel.companyId + "\n")}