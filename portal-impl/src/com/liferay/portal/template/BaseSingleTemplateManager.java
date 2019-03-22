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

package com.liferay.portal.template;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.TemplateResourceLoader;
import com.liferay.portal.kernel.template.URLTemplateResource;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * @author Leonardo Barros
 */
public abstract class BaseSingleTemplateManager extends BaseTemplateManager {

	@Override
	public Template getTemplate(
		List<TemplateResource> templateResources, boolean restricted) {

		return getTemplate(templateResources, null, restricted);
	}

	@Override
	public Template getTemplate(
		List<TemplateResource> templateResources,
		TemplateResource errorTemplateResource, boolean restricted) {

		throw new UnsupportedOperationException(
			"Template type does not support multi templates");
	}

	@Override
	public Template getTemplate(
		TemplateResource templateResource, boolean restricted) {

		return getTemplate(templateResource, null, restricted);
	}

	@Override
	public Template getTemplate(
		TemplateResource templateResource,
		TemplateResource errorTemplateResource, boolean restricted) {

		cacheTemplateResource(templateResource, errorTemplateResource);

		return doGetTemplate(
			templateResource, errorTemplateResource, restricted,
			getHelperUtilities(restricted));
	}

	protected void cacheTemplateResource(
		TemplateResource templateResource,
		TemplateResource errorTemplateResource) {

		if (!isCacheEnabled()) {
			return;
		}

		if (!(templateResource instanceof CacheTemplateResource) &&
			!(templateResource instanceof StringTemplateResource)) {

			templateResource = new CacheTemplateResource(templateResource);
		}

		String portalCacheName = TemplateResourceLoader.class.getName();

		portalCacheName = portalCacheName.concat(
			StringPool.PERIOD
		).concat(
			getName()
		);

		PortalCache<String, Serializable> portalCache = getPortalCache(
			templateResource, portalCacheName);

		Object object = portalCache.get(templateResource.getTemplateId());

		if ((object == null) || !templateResource.equals(object)) {
			portalCache.put(templateResource.getTemplateId(), templateResource);
		}

		if (errorTemplateResource == null) {
			return;
		}

		if (!(errorTemplateResource instanceof CacheTemplateResource) &&
			!(errorTemplateResource instanceof StringTemplateResource)) {

			errorTemplateResource = new CacheTemplateResource(
				errorTemplateResource);
		}

		portalCache = getPortalCache(errorTemplateResource, portalCacheName);

		object = portalCache.get(errorTemplateResource.getTemplateId());

		if ((object == null) || !errorTemplateResource.equals(object)) {
			portalCache.put(
				errorTemplateResource.getTemplateId(), errorTemplateResource);
		}
	}

	protected abstract Template doGetTemplate(
		TemplateResource templateResource,
		TemplateResource errorTemplateResource, boolean restricted,
		Map<String, Object> helperUtilities);

	/**
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 *             #doGetTemplate(TemplateResource, TemplateResource, boolean, Map)}
	 */
	@Deprecated
	protected Template doGetTemplate(
		TemplateResource templateResource,
		TemplateResource errorTemplateResource, boolean restricted,
		Map<String, Object> helperUtilities, boolean privileged) {

		return doGetTemplate(
			templateResource, errorTemplateResource, restricted,
			helperUtilities);
	}

	protected PortalCache<String, Serializable> getPortalCache(
		TemplateResource templateResource, String portalCacheName) {

		if (!(templateResource instanceof CacheTemplateResource)) {
			return (PortalCache<String, Serializable>)
				multiVMPool.getPortalCache(portalCacheName);
		}

		CacheTemplateResource cacheTemplateResource =
			(CacheTemplateResource)templateResource;

		TemplateResource innerTemplateResource =
			cacheTemplateResource.getInnerTemplateResource();

		if (innerTemplateResource instanceof URLTemplateResource) {
			return (PortalCache<String, Serializable>)
				singleVMPool.getPortalCache(portalCacheName);
		}

		return (PortalCache<String, Serializable>)multiVMPool.getPortalCache(
			portalCacheName);
	}

	protected abstract boolean isCacheEnabled();

	protected MultiVMPool multiVMPool;
	protected SingleVMPool singleVMPool;

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	protected class DoGetSingleTemplatePrivilegedAction
		extends DoGetAbstractTemplatePrivilegedAction {

		public DoGetSingleTemplatePrivilegedAction(
			TemplateResource templateResource,
			TemplateResource errorTemplateResource, boolean restricted,
			Map<String, Object> helperUtilities) {

			super(errorTemplateResource, restricted, helperUtilities);

			_templateResource = templateResource;
		}

		@Override
		public Template run() {
			return doGetTemplate(
				_templateResource, errorTemplateResource, restricted,
				helperUtilities);
		}

		private final TemplateResource _templateResource;

	}

}