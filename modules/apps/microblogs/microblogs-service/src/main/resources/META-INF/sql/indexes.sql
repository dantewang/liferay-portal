create index IX_1D8CE137 on MicroblogsEntry (type_, creatorClassNameId, companyId, creatorClassPK);
create index IX_14ACFA9 on MicroblogsEntry (type_, creatorClassNameId, creatorClassPK);
create index IX_6AD9275A on MicroblogsEntry (type_, parentMicroblogsEntryId);
create index IX_D8C7F82F on MicroblogsEntry (type_, userId, createDate, socialRelationType);
create index IX_6C297B45 on MicroblogsEntry (userId);