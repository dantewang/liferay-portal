<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/init.jsp" %>

<%
PortalCache<String, String> testPortalCache = PortalCacheHelperUtil.getPortalCache(PortalCacheManagerNames.MULTI_VM, "test.cache");

List<String> keys = testPortalCache.getKeys();

ClusterSampleData clusterSampleData = new ClusterSampleData();

Map<String, String> maps = new HashMap<>();

for (String key : keys) {
	maps.put(key, testPortalCache.get(key));
}
%>

<p>Following data is from the server that generated this response:</p>

<ul>
	<li>
		<b>Computer Name:</b> <%= clusterSampleData.getComputerName() %>
	</li>
	<li>
		<b>Liferay Home:</b> <%= clusterSampleData.getLiferayHome() %>
	</li>
</ul>

<c:if test="<%= keys.isEmpty() %>">
	<br />

	<span style="font-size: large; color: red;">test.cache
	is empty! </span>
	<br />
</c:if>

<c:if test="<%= !keys.isEmpty() %>">
	<span style="font-size: large;">Following
		data is
		cache
	get </span>
	<br />

	<liferay-portlet:actionURL name="/cluster/cache_update" var="clusterCacheUpdateURL">
		<portlet:param name="redirect" value="<%= currentURL %>" />
	</liferay-portlet:actionURL>

	<table style="text-align: center; vertical-align: middle;">
		<tr style="border: solid 1px black;">
			<td style="width: 20%;">CACHE KEY</td>
			<td style="width: 20%;">CACHE VALUE</td>
			<td style="width: 20%;">ACTION</td>
		</tr>

		<c:forEach items="<%= maps %>" var="map" varStatus="loop">
			<tr style="border: solid 1px black;">
				<td>
					${map.key}
				</td>
				<td>
					${map.value}
				</td>
				<td>
					<aui:button-row>
						<liferay-portlet:actionURL name="/cluster/cache_remove" var="clusterCacheRemoveURL">
							<portlet:param name="curKey" value="${map.key}" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
						</liferay-portlet:actionURL>

						<aui:button href="<%= clusterCacheRemoveURL %>" value="remove" />
					</aui:button-row>
				</td>
			</tr>
		</c:forEach>
	</table>
</c:if>

<liferay-portlet:actionURL name="/cluster/cache_put" var="clusterCachePutURL"></liferay-portlet:actionURL>

<aui:form action="<%= clusterCachePutURL %>" method="post" name="fm">
	<aui:input label="Cache key" name="key" required="<%= true %>" style="border: solid 1px grey; width: 30%;" type="text" />
	<aui:input label="Cache value" name="value" required="<%= true %>" style="border: solid 1px grey; width: 30%;" type="text" />

	<aui:button-row>
		<aui:button type="submit" value="Put/Update Cache" />

		<liferay-portlet:actionURL name="/cluster/cache_removeAll" var="clusterCacheRemoveAllURL" />

		<aui:button href="<%= clusterCacheRemoveAllURL %>" value="Remove All" />
	</aui:button-row>
</aui:form>