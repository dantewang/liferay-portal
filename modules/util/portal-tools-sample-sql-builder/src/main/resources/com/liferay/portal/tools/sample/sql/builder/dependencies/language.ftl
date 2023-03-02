<#list dataFactory.newLayoutModels(groupId) as layoutModel>
	${csvFileWriter.write("language", virtualHostModel.hostname + "," + layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />
</#list>