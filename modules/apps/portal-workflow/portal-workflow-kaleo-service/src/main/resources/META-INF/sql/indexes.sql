create index IX_DD20D8D6 on KaleoAction (ctCollectionId, kaleoClassName[$COLUMN_LENGTH:200$], kaleoClassPK, companyId, executionType[$COLUMN_LENGTH:20$]);
create index IX_ADA35E86 on KaleoAction (ctCollectionId, kaleoClassName[$COLUMN_LENGTH:200$], kaleoClassPK, executionType[$COLUMN_LENGTH:20$]);
create index IX_30A3912E on KaleoAction (ctCollectionId, kaleoDefinitionVersionId);

create index IX_73E788C5 on KaleoCondition (ctCollectionId, companyId);
create index IX_CA1D1A93 on KaleoCondition (ctCollectionId, kaleoDefinitionVersionId);
create index IX_27F2D1AE on KaleoCondition (ctCollectionId, kaleoNodeId);

create index IX_CF0EC5F5 on KaleoDefinition (companyId, ctCollectionId, active_, name[$COLUMN_LENGTH:200$]);
create index IX_1ED4E08E on KaleoDefinition (companyId, ctCollectionId, active_, scope[$COLUMN_LENGTH:75$]);
create index IX_4608D7FC on KaleoDefinition (companyId, ctCollectionId, name[$COLUMN_LENGTH:200$], version);
create index IX_582E7DD7 on KaleoDefinition (companyId, ctCollectionId, scope[$COLUMN_LENGTH:75$]);

create unique index IX_AFE669AE on KaleoDefinitionVersion (companyId, ctCollectionId, name[$COLUMN_LENGTH:200$], version[$COLUMN_LENGTH:75$]);

create index IX_947A3F29 on KaleoInstance (ctCollectionId, className[$COLUMN_LENGTH:200$], classPK);
create index IX_1FC77FDE on KaleoInstance (ctCollectionId, companyId, completionDate, kaleoDefinitionName[$COLUMN_LENGTH:200$], kaleoDefinitionVersion);
create index IX_1418ABEF on KaleoInstance (ctCollectionId, companyId, userId, kaleoInstanceId);
create index IX_675715C0 on KaleoInstance (ctCollectionId, completed, kaleoDefinitionId);
create index IX_550DEEF2 on KaleoInstance (ctCollectionId, completed, kaleoDefinitionVersionId);
create index IX_D0B5950D on KaleoInstance (ctCollectionId, kaleoDefinitionVersionId);

create index IX_60B05B37 on KaleoInstanceToken (ctCollectionId, companyId, parentKaleoInstanceTokenId, completionDate);
create index IX_1217EE5C on KaleoInstanceToken (ctCollectionId, kaleoDefinitionVersionId);
create index IX_2A66B558 on KaleoInstanceToken (ctCollectionId, kaleoInstanceId);

create index IX_DA074F3C on KaleoLog (ctCollectionId, companyId);
create index IX_2E86B73C on KaleoLog (ctCollectionId, kaleoDefinitionVersionId);
create index IX_9DE87078 on KaleoLog (ctCollectionId, kaleoInstanceId);
create index IX_C92001C on KaleoLog (ctCollectionId, kaleoInstanceTokenId, type_[$COLUMN_LENGTH:50$], kaleoClassName[$COLUMN_LENGTH:200$], kaleoClassPK);
create index IX_4BF6F316 on KaleoLog (ctCollectionId, kaleoTaskInstanceTokenId);

create index IX_DF46D392 on KaleoNode (ctCollectionId, kaleoDefinitionVersionId, companyId);

create index IX_52775CF5 on KaleoNotification (ctCollectionId, companyId);
create index IX_85B9131 on KaleoNotification (ctCollectionId, kaleoClassName[$COLUMN_LENGTH:200$], kaleoClassPK, executionType[$COLUMN_LENGTH:20$]);
create index IX_32C46063 on KaleoNotification (ctCollectionId, kaleoDefinitionVersionId);

create index IX_260AB952 on KaleoNotificationRecipient (ctCollectionId, companyId);
create index IX_C48B1E66 on KaleoNotificationRecipient (ctCollectionId, kaleoDefinitionVersionId);
create index IX_C3504564 on KaleoNotificationRecipient (ctCollectionId, kaleoNotificationId);

create index IX_DC396E9B on KaleoTask (ctCollectionId, companyId);
create index IX_A1140DFD on KaleoTask (ctCollectionId, kaleoDefinitionVersionId);
create index IX_C3629A04 on KaleoTask (ctCollectionId, kaleoNodeId);

create index IX_8AA6A90E on KaleoTaskAssignment (ctCollectionId, companyId);
create index IX_B413CC28 on KaleoTaskAssignment (ctCollectionId, kaleoClassName[$COLUMN_LENGTH:200$], kaleoClassPK, assigneeClassName[$COLUMN_LENGTH:200$]);
create index IX_BD23F2A on KaleoTaskAssignment (ctCollectionId, kaleoDefinitionVersionId);

create index IX_35F3735B on KaleoTaskAssignmentInstance (ctCollectionId, assigneeClassName[$COLUMN_LENGTH:200$], assigneeClassPK);
create index IX_5A877483 on KaleoTaskAssignmentInstance (ctCollectionId, assigneeClassName[$COLUMN_LENGTH:200$], kaleoTaskInstanceTokenId);
create index IX_4A9C965 on KaleoTaskAssignmentInstance (ctCollectionId, assigneeClassPK, groupId);
create index IX_349E1A79 on KaleoTaskAssignmentInstance (ctCollectionId, companyId);
create index IX_FFE26A5F on KaleoTaskAssignmentInstance (ctCollectionId, kaleoDefinitionVersionId);
create index IX_5BD719F5 on KaleoTaskAssignmentInstance (ctCollectionId, kaleoInstanceId);
create index IX_1D52A639 on KaleoTaskAssignmentInstance (ctCollectionId, kaleoTaskInstanceTokenId);

create index IX_379782B7 on KaleoTaskForm (ctCollectionId, companyId);
create index IX_6F3FBE61 on KaleoTaskForm (ctCollectionId, kaleoDefinitionVersionId);
create index IX_BF8C1720 on KaleoTaskForm (ctCollectionId, kaleoNodeId);
create index IX_A0FF0670 on KaleoTaskForm (ctCollectionId, kaleoTaskId, formUuid[$COLUMN_LENGTH:75$]);

create index IX_D4D33B22 on KaleoTaskFormInstance (ctCollectionId, companyId);
create index IX_7414C296 on KaleoTaskFormInstance (ctCollectionId, kaleoDefinitionVersionId);
create index IX_F3B157DE on KaleoTaskFormInstance (ctCollectionId, kaleoInstanceId);
create index IX_DC7E6532 on KaleoTaskFormInstance (ctCollectionId, kaleoTaskFormId);
create index IX_A1586ECE on KaleoTaskFormInstance (ctCollectionId, kaleoTaskId);
create index IX_9184FE70 on KaleoTaskFormInstance (ctCollectionId, kaleoTaskInstanceTokenId);

create index IX_941E25F3 on KaleoTaskInstanceToken (ctCollectionId, className[$COLUMN_LENGTH:200$], classPK);
create index IX_6A2C54D0 on KaleoTaskInstanceToken (ctCollectionId, companyId, userId, completed);
create index IX_E0FFBC57 on KaleoTaskInstanceToken (ctCollectionId, kaleoDefinitionVersionId);
create index IX_75F494F3 on KaleoTaskInstanceToken (ctCollectionId, kaleoInstanceId, kaleoTaskId);

create index IX_1BE33CD0 on KaleoTimer (ctCollectionId, kaleoClassName[$COLUMN_LENGTH:200$], kaleoClassPK, blocking);

create index IX_7C0D60BD on KaleoTimerInstanceToken (ctCollectionId, kaleoInstanceId);
create index IX_30A26E7E on KaleoTimerInstanceToken (ctCollectionId, kaleoInstanceTokenId, completed, blocking);
create index IX_EC2F4F8A on KaleoTimerInstanceToken (ctCollectionId, kaleoInstanceTokenId, kaleoTimerId);

create index IX_C183C8CB on KaleoTransition (ctCollectionId, companyId);
create index IX_D1E40DCD on KaleoTransition (ctCollectionId, kaleoDefinitionVersionId);
create index IX_EB2D1920 on KaleoTransition (ctCollectionId, kaleoNodeId, defaultTransition);
create index IX_C0DF80F3 on KaleoTransition (ctCollectionId, kaleoNodeId, name[$COLUMN_LENGTH:200$]);