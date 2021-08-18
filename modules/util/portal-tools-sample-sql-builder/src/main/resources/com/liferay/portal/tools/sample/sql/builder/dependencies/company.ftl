<#list dataFactory.companyModels as companyModel>
	${dataFactory.setCompanyId(companyModel.companyId)}

	${dataFactory.setWebId(companyModel.webId)}

	<#list dataFactory.newPortalPreferencesModels() as portalPreferencesModel>
		${dataFactory.toInsertSQL(portalPreferencesModel)}
	</#list>

	${csvFileWriter.write("company", companyModel.companyId + "\n")}

	<#include "roles.ftl">

	<#include "groups.ftl">
</#list>