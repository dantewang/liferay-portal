create index IX_5C068998 on Calendar (ctCollectionId, groupId, calendarResourceId, defaultCalendar);
create unique index IX_CDC1CFCC on Calendar (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_A7E3F712 on Calendar (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_30C671B0 on CalendarBooking (ctCollectionId, calendarId, parentCalendarBookingId);
create index IX_FE85AD12 on CalendarBooking (ctCollectionId, calendarId, status);
create unique index IX_9090D8F0 on CalendarBooking (ctCollectionId, calendarId, vEventUid[$COLUMN_LENGTH:255$]);
create index IX_98BCEE5A on CalendarBooking (ctCollectionId, calendarResourceId);
create index IX_FF78B8A3 on CalendarBooking (ctCollectionId, parentCalendarBookingId, status);
create index IX_5F56208C on CalendarBooking (ctCollectionId, recurringCalendarBookingId);
create index IX_A140184F on CalendarBooking (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_B13F1C49 on CalendarBooking (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_7FA5F060 on CalendarNotificationTemplate (ctCollectionId, calendarId, notificationTemplateType[$COLUMN_LENGTH:75$], notificationType[$COLUMN_LENGTH:75$]);
create index IX_ADC34037 on CalendarNotificationTemplate (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_ACBB1E31 on CalendarNotificationTemplate (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_DFE31A3B on CalendarResource (ctCollectionId, active_, code_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_CD46CB85 on CalendarResource (ctCollectionId, classNameId, classPK);
create index IX_3DE4AF5 on CalendarResource (ctCollectionId, groupId, active_);
create index IX_ECCBFE5C on CalendarResource (ctCollectionId, groupId, code_[$COLUMN_LENGTH:75$]);
create unique index IX_EF11AE7A on CalendarResource (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_B4B6E240 on CalendarResource (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);