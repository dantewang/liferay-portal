<#assign companyModel = dataFactory.newCompanyModel() />

${dataFactory.setCompanyId(companyModel.companyId)}

${dataFactory.toInsertSQL(companyModel)}

${dataFactory.toInsertSQL(dataFactory.newAccountModel(companyModel))}

${dataFactory.toInsertSQL(dataFactory.newVirtualHostModel())}

${csvFileWriter.write("company", companyModel.companyId + "\n")}

<#include "roles.ftl">

<#include "groups.ftl">