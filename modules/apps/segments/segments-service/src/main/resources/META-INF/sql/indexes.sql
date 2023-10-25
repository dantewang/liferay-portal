create unique index IX_AC88F035 on SegmentsEntry (ctCollectionId, groupId, segmentsEntryKey[$COLUMN_LENGTH:75$]);
create index IX_2AD151CB on SegmentsEntry (ctCollectionId, groupId, type_[$COLUMN_LENGTH:75$], active_, source[$COLUMN_LENGTH:75$]);
create unique index IX_6AE1E800 on SegmentsEntry (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_9DCDED05 on SegmentsEntry (ctCollectionId, source[$COLUMN_LENGTH:75$]);
create index IX_5AA49066 on SegmentsEntry (ctCollectionId, type_[$COLUMN_LENGTH:75$], active_);
create index IX_7D5ED246 on SegmentsEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_EF87630A on SegmentsEntryRel (ctCollectionId, classNameId, classPK, groupId);
create unique index IX_278B71BD on SegmentsEntryRel (ctCollectionId, classNameId, classPK, segmentsEntryId);
create index IX_70170DB2 on SegmentsEntryRel (ctCollectionId, segmentsEntryId);

create unique index IX_245993CC on SegmentsEntryRole (ctCollectionId, segmentsEntryId, roleId);

create unique index IX_A1462F9F on SegmentsExperience (ctCollectionId, groupId, plid, priority);
create index IX_43F38F5F on SegmentsExperience (ctCollectionId, groupId, plid, segmentsEntryId, active_);
create unique index IX_2E07319E on SegmentsExperience (ctCollectionId, groupId, plid, segmentsExperienceKey[$COLUMN_LENGTH:75$]);
create unique index IX_D81326B8 on SegmentsExperience (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_FE1B5C2F on SegmentsExperience (ctCollectionId, segmentsEntryId);
create index IX_633B42FE on SegmentsExperience (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_4E742D on SegmentsExperiment (ctCollectionId, groupId, plid, segmentsExperienceId);
create unique index IX_D6D5069 on SegmentsExperiment (ctCollectionId, groupId, segmentsExperimentKey[$COLUMN_LENGTH:75$]);
create unique index IX_372C448B on SegmentsExperiment (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_83B415B1 on SegmentsExperiment (ctCollectionId, segmentsExperimentKey[$COLUMN_LENGTH:75$]);
create index IX_60843811 on SegmentsExperiment (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_9FF4F025 on SegmentsExperimentRel (ctCollectionId, segmentsExperimentId, segmentsExperienceId);