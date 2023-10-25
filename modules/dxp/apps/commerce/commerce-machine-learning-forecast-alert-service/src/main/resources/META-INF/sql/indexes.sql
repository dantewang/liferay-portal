create index IX_E6AD0292 on CommerceMLForecastAlertEntry (companyId, commerceAccountId, status, relativeChange);
create index IX_7A22DE2A on CommerceMLForecastAlertEntry (companyId, commerceAccountId, timestamp);
create index IX_5D29639D on CommerceMLForecastAlertEntry (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_794DAF43 on CommerceMLForecastAlertEntry (uuid_[$COLUMN_LENGTH:75$]);