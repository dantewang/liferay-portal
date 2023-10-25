create index IX_C5A6C78F on BackgroundTask (companyId);
create index IX_A007A794 on BackgroundTask (taskExecutorClassName[$COLUMN_LENGTH:200$], groupId, completed, name[$COLUMN_LENGTH:255$]);
create index IX_98CC0AAB on BackgroundTask (taskExecutorClassName[$COLUMN_LENGTH:200$], groupId, name[$COLUMN_LENGTH:255$]);
create index IX_F2B147E4 on BackgroundTask (taskExecutorClassName[$COLUMN_LENGTH:200$], groupId, status);
create index IX_A40BB1BC on BackgroundTask (taskExecutorClassName[$COLUMN_LENGTH:200$], status);