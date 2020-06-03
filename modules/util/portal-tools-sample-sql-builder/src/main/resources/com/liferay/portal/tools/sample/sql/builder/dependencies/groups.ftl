<#assign guestGroupModel = dataFactory.newGuestGroupModel() />

<#include "default_user.ftl">

<@insertGroup _groupModel=dataFactory.commerceCatalogGroupModel />

<@insertGroup _groupModel=dataFactory.commerceChannelGroupModel />

<@insertGroup _groupModel=dataFactory.newGlobalGroupModel() />

<@insertGroup _groupModel=guestGroupModel />

<@insertGroup _groupModel=dataFactory.newUserPersonalSiteGroupModel() />

<#list dataFactory.newGroupModels() as groupModel>
	<#assign groupId = groupModel.groupId />

	<#include "asset_publisher.ftl">

	<#include "blogs.ftl">

	<#include "ddl.ftl">

	<#include "journal_article.ftl">

	<#include "fragment.ftl">

	<#include "mb.ftl">

	<#include "users.ftl">

	<#include "wiki.ftl">

	<@insertHomePageDLFolder
		_ddmStructureId=dataFactory.defaultDLDDMStructureId
		_dlFolderDepth=1
		_groupId=groupId
		_parentDLFolderId=0
	/>

	<@insertDLFolder
		_ddmStructureId=dataFactory.defaultDLDDMStructureId
		_dlFolderDepth=1
		_groupId=groupId
		_parentDLFolderId=0
	/>

	<@insertContentLayout _layoutModels=dataFactory.newHomePageLayoutModels(groupId) />

	<#assign publicLayoutModels = dataFactory.newPublicLayoutModels(groupId) />

	<#list publicLayoutModels as publicLayoutModel>
		<@insertLayout _layoutModel=publicLayoutModel />
	</#list>

	<@insertGroup _groupModel=groupModel />

	${dataFactory.getCSVWriter("repository").write(groupId + ", " + groupModel.name + "\n")}
</#list>

<@insertContentLayout _layoutModels=dataFactory.newHomePageLayoutModels(guestGroupModel.groupId) />