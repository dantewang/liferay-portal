create index IX_581E5365 on Reports_Definition (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_A29C8EE3 on Reports_Definition (uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_F0A5B22D on Reports_Source (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_717CFFAB on Reports_Source (uuid_[$COLUMN_LENGTH:75$], groupId);