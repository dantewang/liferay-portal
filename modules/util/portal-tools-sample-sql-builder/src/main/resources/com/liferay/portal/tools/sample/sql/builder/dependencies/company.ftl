<#assign companyModel = userDataFactory.newCompanyModel() />

${resourcePermissionDataFactory.toInsertSQL(companyModel)}

${resourcePermissionDataFactory.toInsertSQL(userDataFactory.newAccountModel())}

${resourcePermissionDataFactory.toInsertSQL(userDataFactory.newVirtualHostModel())}

${csvFileWriter.write("company", companyModel.companyId + "\n")}