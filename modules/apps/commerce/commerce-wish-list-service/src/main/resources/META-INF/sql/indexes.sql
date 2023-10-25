create unique index IX_FB7BEB90 on CommerceWishList (groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_5CB19FBE on CommerceWishList (userId, createDate);
create index IX_A49626F4 on CommerceWishList (userId, groupId, defaultWishList);
create index IX_F69938D2 on CommerceWishList (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_B8A10AD4 on CommerceWishListItem (commerceWishListId, CPInstanceUuid[$COLUMN_LENGTH:75$], CProductId);
create index IX_13715FA3 on CommerceWishListItem (commerceWishListId, CProductId);