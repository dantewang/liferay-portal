<#assign companyModel = userDataFactory.companyModel />

insert into Company values (${companyModel.mvccVersion}, ${companyModel.companyId}, ${companyModel.accountId}, '${companyModel.webId}', '${companyModel.key}', '${companyModel.mx}', '${companyModel.homeURL}', ${companyModel.logoId}, ${companyModel.system?string}, ${companyModel.maxUsers}, ${companyModel.active?string});

<#assign accountModel = userDataFactory.accountModel />

insert into Account_ values (${accountModel.mvccVersion}, ${accountModel.accountId}, ${accountModel.companyId}, ${accountModel.userId}, '${accountModel.userName}', '${initContext.getDateString(accountModel.createDate)}', '${initContext.getDateString(accountModel.modifiedDate)}', '${accountModel.parentAccountId}', '${accountModel.name}', '${accountModel.legalName}', '${accountModel.legalId}', '${accountModel.legalType}', '${accountModel.sicCode}', '${accountModel.tickerSymbol}', '${accountModel.industry}', '${accountModel.type}', '${accountModel.size}');

<#assign virtualHostModel = userDataFactory.virtualHostModel />

insert into VirtualHost values (${virtualHostModel.mvccVersion}, ${virtualHostModel.virtualHostId}, ${virtualHostModel.companyId}, ${virtualHostModel.layoutSetId}, '${virtualHostModel.hostname}');

${companyCSVWriter.write(companyModel.companyId + "\n")}