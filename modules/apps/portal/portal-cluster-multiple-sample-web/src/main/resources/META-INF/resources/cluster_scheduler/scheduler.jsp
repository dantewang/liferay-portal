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
ClusterSampleData clusterSampleData = new ClusterSampleData();

ClusterNode clusterNode = ClusterExecutorUtil.getLocalClusterNode();

int portalPort = clusterNode.getPortalPort();

String persistentJobMessage;

try {
	SchedulerEngineHelperUtil.addScriptingJob(TriggerFactoryUtil.createTrigger("test.job.name." + portalPort + ".persistent", "test.job.group." + portalPort + ".persistent", 10, TimeUnit.SECOND), StorageType.PERSISTED, "persistent job", "groovy", "System.out.println(\"Persistent job at " + portalPort + " is triggered at \" + System.currentTimeMillis());", 0);

	persistentJobMessage = "Persistent job at " + portalPort + " is added.";
}
catch (SchedulerException ex) {
	persistentJobMessage = ex.toString();
}

String memoryJobMessage;

try {
	SchedulerEngineHelperUtil.addScriptingJob(TriggerFactoryUtil.createTrigger("test.job.name." + portalPort + ".memory", "test.job.group." + portalPort + ".memory", 10, TimeUnit.SECOND), StorageType.MEMORY, "memory job", "groovy", "System.out.println(\"Memory job at " + portalPort + " is triggered at \" + System.currentTimeMillis());", 0);

	memoryJobMessage = "Memory job at " + portalPort + " is added.";
}
catch (SchedulerException ex) {
	memoryJobMessage = ex.toString();
}

String memoryClusteredJobMessage;

try {
	SchedulerEngineHelperUtil.addScriptingJob(TriggerFactoryUtil.createTrigger("test.job.name." + portalPort + ".memory.clustered", "test.job.group." + portalPort + ".memory.clusterted", 10, TimeUnit.SECOND), StorageType.MEMORY_CLUSTERED, "memory clustered job", "groovy", "System.out.println(\"Memory clustered job at " + portalPort + " is triggered at \" + System.currentTimeMillis());", 0);

	memoryClusteredJobMessage = "Memory clustered job at " + portalPort + " is added.";
}
catch (SchedulerException ex) {
	memoryClusteredJobMessage = ex.toString();
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

Persistent job at <%= portalPort %> is triggered at <%= System.currentTimeMillis() %>.
<br /><br />
<span class="blueText"><%= persistentJobMessage %></span>
<br /><br />
Memory job at <%= portalPort %> is triggered at <%= System.currentTimeMillis() %>.
<br /><br />
<span class="blueText"><%= memoryJobMessage %></span>
<br /><br />
Memory clustered job at <%= portalPort %> is triggered at <%= System.currentTimeMillis() %>.
<br /><br />
<span class="blueText"><%= memoryClusteredJobMessage %></span>