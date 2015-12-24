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

<%@ include file="/portlet/init.jsp" %>

<%
String productMenuState = SessionClicks.get(request, "com.liferay.control.menu.web_productMenuState", "closed");
%>

<li class="<%= Validator.equals(productMenuState, "open") ? "active" : StringPool.BLANK %>">
	<liferay-portlet:renderURL portletName="<%= ProductNavigationProductMenuPortletKeys.PRODUCT_NAVIGATION_PRODUCT_MENU %>" var="portletURL" windowState="exclusive">
		<portlet:param name="mvcPath" value="/portlet/view.jsp" />
	</liferay-portlet:renderURL>

	<a class="control-menu-icon sidenav-toggler" data-content="body" data-panelurl="<%= portletURL.toString() %>" data-qa-id="productMenu" data-target="#sidenavSliderId,#wrapper" data-title="<%= HtmlUtil.escape(LanguageUtil.get(request, "menu")) %>" data-toggle="sidenav" data-type="fixed-push" data-type-mobile="fixed" href="#sidenavSliderId" id="sidenavToggleId">
		<div class="toast-animation">
			<div class="pm"></div>

			<div class="cn"></div>
		</div>
	</a>
</li>

<%
String controlMenuPortletId = PortletProviderUtil.getPortletId(PortalControlMenuApplicationType.ControlMenu.CLASS_NAME, PortletProvider.Action.VIEW);
%>

<c:if test="<%= Validator.isNotNull(controlMenuPortletId) %>">
	<aui:script>
		Liferay.on(
			'<%= controlMenuPortletId %>:portletRefreshed',
			function(event) {
				Liferay.Portlet.refresh('#p_p_id_<%= ProductNavigationProductMenuPortletKeys.PRODUCT_NAVIGATION_PRODUCT_MENU %>_', {});
			}
		);
	</aui:script>
</c:if>

<aui:script use="liferay-store">
	AUI.$('#sidenavToggleId').sideNavigation();

	var sidenavSlider = AUI.$('#sidenavSliderId');

	sidenavSlider.off('closed.lexicon.sidenav');
	sidenavSlider.off('open.lexicon.sidenav');

	sidenavSlider.on(
		'closed.lexicon.sidenav',
		function(event) {
			Liferay.Store('com.liferay.control.menu.web_productMenuState', 'closed');
		}
	);

	sidenavSlider.on(
		'open.lexicon.sidenav',
		function(event) {
		   	var productMenuSidebar = A.one('#productMenuSidebar');

	 		if (productMenuSidebar && !productMenuSidebar.hasClass('has-content')) {
	 			<portlet:namespace />getProductMenuSidebar();
	 		}

			Liferay.Store('com.liferay.control.menu.web_productMenuState', 'open');
		}
	);
</aui:script>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />getProductMenuSidebar',
		function() {
			var A = AUI();

			var sidenavToggle = A.one('#sidenavToggleId');

			if (sidenavToggle) {
				var uri = sidenavToggle.attr('data-panelurl');

				A.io.request(
					uri,
					{
						after: {
							success: function(event, id, obj) {
								var response = this.get('responseData');

								var productMenuSidebar = A.one('#productMenuSidebar');

								productMenuSidebar.plug(A.Plugin.ParseContent);

								productMenuSidebar.setContent(response);
								productMenuSidebar.addClass('has-content');
							}
						}
					}
				);
			}
		},
		['aui-io-request', 'aui-parse-content', 'event-outside']
	);
</aui:script>