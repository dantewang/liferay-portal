<#assign layoutModel = layoutDataFactory.newLayoutModel(userDataFactory.guestGroupModel.groupId, "welcome", "com_liferay_login_web_portlet_LoginPortlet,", "com_liferay_hello_world_web_portlet_HelloWorldPortlet,") />

<@insertLayout _layoutModel=layoutModel />

<@insertGroup _groupModel=commerceDataFactory.commerceCatalogGroupModel />

<@insertGroup _groupModel=commerceDataFactory.commerceChannelGroupModel />

<@insertGroup _groupModel=userDataFactory.globalGroupModel />

<@insertGroup _groupModel=userDataFactory.guestGroupModel />

<@insertGroup _groupModel=userDataFactory.userPersonalSiteGroupModel />

<#list userDataFactory.groupModels as groupModel>
	<#assign groupId = groupModel.groupId />

	<#include "asset_publisher.ftl">

	<#include "blogs.ftl">

	<#include "ddl.ftl">

	<#include "journal_article.ftl">

	<#include "mb.ftl">

	<#include "users.ftl">

	<#include "wiki.ftl">

	<@insertDLFolder
		_ddmStructureId=dLDataFactory.defaultDLDDMStructureId
		_dlFolderDepth=1
		_groupId=groupId
		_parentDLFolderId=0
	/>

	<#assign publicLayoutModels = layoutDataFactory.newPublicLayoutModels(groupId) />

	<#list publicLayoutModels as publicLayoutModel>
		<@insertLayout _layoutModel=publicLayoutModel />
	</#list>

	<@insertGroup _groupModel=groupModel />

	${initContext.getCSVWriter("repository").write(groupId + ", " + groupModel.name + "\n")}
</#list>