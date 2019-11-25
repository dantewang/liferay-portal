<#assign
	accountModel = dataFactory.newAccountModel()

	companyModel = dataFactory.newCompanyModel()
/>

${dataFactory.toInsertSQL(companyModel)}

${dataFactory.toInsertSQL(accountModel)}

${dataFactory.toInsertSQL(dataFactory.virtualHostModel)}

${dataFactory.getCSVWriter("company").write(companyModel.companyId + "\n")}