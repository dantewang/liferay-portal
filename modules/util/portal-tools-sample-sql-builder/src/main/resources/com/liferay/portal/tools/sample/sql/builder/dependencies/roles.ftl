<#list userDataFactory.roleModels as roleModel>
	insert into Role_ values (${roleModel.mvccVersion}, '${roleModel.uuid}', ${roleModel.roleId}, ${roleModel.companyId}, ${roleModel.userId}, '${roleModel.userName}', '${initContext.getDateString(roleModel.createDate)}', '${initContext.getDateString(roleModel.modifiedDate)}', ${roleModel.classNameId}, ${roleModel.classPK}, '${roleModel.name}', '${roleModel.title}', '${roleModel.description}', ${roleModel.type}, '${roleModel.subtype}');

	<@insertResourcePermissions
		_entry = roleModel
	/>
</#list>