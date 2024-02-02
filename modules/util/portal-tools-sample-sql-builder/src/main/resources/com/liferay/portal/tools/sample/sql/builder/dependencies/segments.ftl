<#assign
	layoutModel = dataFactory.newLayoutModel(guestGroupModel.groupId, "segments_experience_layout", "", "")

	layoutModelInserted = false

	segmentsExperienceCount = 1
/>

<#list dataFactory.getSequence(dataFactory.maxSegmentsEntryCount) as i>
	<#assign
		segmentEntryModel = dataFactory.newSegmentsEntry(guestGroupModel.groupId, i)
	/>

	${dataFactory.toInsertSQL(segmentEntryModel)}

	<#list dataFactory.getSequence(dataFactory.maxSegmentsEntrySegmentsExperienceCount) as j>
		<#if layoutModelInserted = false>
			${dataFactory.toInsertSQL(layoutModel)}

			<#assign
				layoutModelInserted = true
			/>
		</#if>

		${dataFactory.toInsertSQL(dataFactory.newSegmentsExperience(guestGroupModel.groupId, segmentsExperienceCount, layoutModel.plid, segmentEntryModel.segmentsEntryId))}

		<#assign
			segmentsExperienceCount = segmentsExperienceCount + 1
		/>
	</#list>
</#list>