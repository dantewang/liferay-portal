${resourcePermissionDataFactory.toInsertSQL(userDataFactory.companyModel)}

${resourcePermissionDataFactory.toInsertSQL(userDataFactory.accountModel)}

${resourcePermissionDataFactory.toInsertSQL(userDataFactory.virtualHostModel)}

${dataFactoryContext.getCSVWriter("company").write(userDataFactory.companyModel.companyId + "\n")}