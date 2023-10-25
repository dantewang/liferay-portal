create index IX_87DAF9C3 on OA2Auths_OA2ScopeGrants (companyId);
create index IX_2F541817 on OA2Auths_OA2ScopeGrants (oAuth2ScopeGrantId);

create index IX_C81F86B3 on OAuth2Application (companyId, clientId[$COLUMN_LENGTH:75$]);
create index IX_F9243B75 on OAuth2Application (companyId, clientProfile);
create unique index IX_67BC29B0 on OAuth2Application (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_6C79AC27 on OAuth2Application (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_361558F9 on OAuth2Application (uuid_[$COLUMN_LENGTH:75$]);

create index IX_282ECE83 on OAuth2ApplicationScopeAliases (companyId);
create index IX_2F9EBCBB on OAuth2ApplicationScopeAliases (oAuth2ApplicationId);

create index IX_BCA9D3FE on OAuth2Authorization (companyId, accessTokenContentHash);
create index IX_673EE35 on OAuth2Authorization (companyId, refreshTokenContentHash);
create index IX_7F3F0504 on OAuth2Authorization (userId, oAuth2ApplicationId, rememberDeviceContent[$COLUMN_LENGTH:75$]);

create index IX_E93B6077 on OAuth2ScopeGrant (oA2AScopeAliasesId, applicationName[$COLUMN_LENGTH:255$], bundleSymbolicName[$COLUMN_LENGTH:255$], companyId, scope[$COLUMN_LENGTH:240$]);