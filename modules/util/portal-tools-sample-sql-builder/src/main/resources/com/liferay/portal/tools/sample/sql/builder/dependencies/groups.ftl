<#assign
	groupClassNameId = dataFactory.getClassNameId("com.liferay.portal.kernel.model.Group")
	guestGroupModel = dataFactory.newGuestGroupModel(groupClassNameId)
	userClassNameId = dataFactory.getClassNameId("com.liferay.portal.kernel.model.User")
/>

<#include "default_user.ftl">

<@insertLayout _layoutModel=dataFactory.newLayoutModel(guestGroupModel.groupId, "welcome", "com_liferay_login_web_portlet_LoginPortlet,", "com_liferay_hello_world_web_portlet_HelloWorldPortlet,") />

<@insertGroup _groupModel=dataFactory.newGlobalGroupModel(dataFactory.getClassNameId("com.liferay.portal.kernel.model.Company")) />

<@insertGroup _groupModel=guestGroupModel />

<@insertGroup _groupModel=dataFactory.newUserPersonalSiteGroupModel(dataFactory.getClassNameId("com.liferay.portal.kernel.model.UserPersonalSite")) />

<#list dataFactory.newGroupModels(groupClassNameId) as groupModel>
	<#assign groupId = groupModel.groupId />

	<#include "asset_publisher.ftl">

	<#include "blogs.ftl">

	<#include "ddl.ftl">

	<#include "journal_article.ftl">

	<#include "fragment.ftl">

	<#include "mb.ftl">

	<#include "users.ftl">

	<#include "wiki.ftl">

	<@insertDLFolder
		_ddmStructureId=dataFactory.defaultDLDDMStructureId
		_dlFolderDepth=1
		_groupId=groupId
		_parentDLFolderId=0
	/>

	<#assign publicLayoutModels = dataFactory.newPublicLayoutModels(groupId) />

	<#list publicLayoutModels as publicLayoutModel>
		<@insertLayout _layoutModel=publicLayoutModel />
	</#list>

	<@insertGroup _groupModel=groupModel />

	${csvFileWriter.write("repository", groupId + ", " + groupModel.name + "\n")}
</#list>