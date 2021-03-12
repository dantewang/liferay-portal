<#assign resourcePermissionModels = dataFactory.newResourcePermissionModels("com.liferay.journal", groupId) />

<#list resourcePermissionModels as resourcePermissionModel>
	${dataFactory.toInsertSQL(resourcePermissionModel)}
</#list>

<#if dataFactory.maxContentLayoutCount != 0>
	<#assign
		journalArticlePageCount = 0
		journalArticleCounts = dataFactory.getSequence(dataFactory.maxJournalArticleCount)
		versionCounts = dataFactory.getSequence(dataFactory.maxJournalArticleVersionCount)
	/>

	<#include "widget_journal_article.ftl">

	<#assign fragmentCollectionModel = dataFactory.newFragmentCollectionModel(groupId) />

	${dataFactory.toInsertSQL(fragmentCollectionModel)}

	<#assign fragmentEntryModel = dataFactory.newFragmentEntryModel(groupId, fragmentCollectionModel) />

	${dataFactory.toInsertSQL(fragmentEntryModel)}

	<#assign contentLayoutModels = dataFactory.newContentLayoutModels(groupId) />

	<#list contentLayoutModels as contentLayoutModel>
		<@insertContentLayout
			_fragmentEntryModel=fragmentEntryModel
			_journalArticleModel=journalArticleModel
			_layoutModel=contentLayoutModel
		/>

		${csvFileWriter.write("fragment", contentLayoutModel.friendlyURL + "\n")}
	</#list>
</#if>

<#assign
	journalArticlePageCounts = dataFactory.getSequence(dataFactory.maxJournalArticlePageCount)
	journalArticleCounts = dataFactory.getSequence(dataFactory.maxJournalArticleCount)
	versionCounts = dataFactory.getSequence(dataFactory.maxJournalArticleVersionCount)
/>

<#list journalArticlePageCounts as journalArticlePageCount>
	<#include "widget_journal_article.ftl">
</#list>