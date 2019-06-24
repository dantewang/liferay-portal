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

package com.liferay.portal.template.soy.internal;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.template.TemplateResource;

import java.io.Serializable;

import java.util.List;

/**
 * @author Bruno Basto
 */
public class SoyTofuCacheHandler {

	public SoyTofuCacheHandler(
		PortalCache<Serializable, SoyTofuCacheBag> portalCache) {

		_portalCache = portalCache;
	}

	public SoyTofuCacheBag add(
		List<TemplateResource> templateResources, SoyFileSet soyFileSet,
		SoyTofu soyTofu) {

		SoyTofuCacheBag soyTofuCacheBag = new SoyTofuCacheBag(
			soyFileSet, soyTofu);

		if (templateResources.size() == _allSoyTemplateResources.size()) {
			if (templateResources.equals(_allSoyTemplateResources)) {
				_allSoyTofuCacheBag = soyTofuCacheBag;
			}
		}

		_portalCache.put((Serializable)templateResources, soyTofuCacheBag);

		return soyTofuCacheBag;
	}

	public SoyTofuCacheBag get(List<TemplateResource> templateResources) {
		if (templateResources.size() == _allSoyTemplateResources.size()) {
			if (templateResources.equals(_allSoyTemplateResources)) {
				return _allSoyTofuCacheBag;
			}
		}

		return _portalCache.get((Serializable)templateResources);
	}

	public void removeIfAny(List<TemplateResource> templateResources) {
		_allSoyTofuCacheBag = null;

		for (TemplateResource templateResource : templateResources) {
			for (Serializable key : _portalCache.getKeys()) {
				List<TemplateResource> templateResourcesList =
					(List<TemplateResource>)key;

				if (templateResourcesList.contains(templateResource)) {
					_portalCache.remove(key);
				}
			}
		}
	}

	protected void setAllSoyTemplateResources(
		List<TemplateResource> templateResources) {

		_allSoyTemplateResources = templateResources;
	}

	private List<TemplateResource> _allSoyTemplateResources;
	private volatile SoyTofuCacheBag _allSoyTofuCacheBag;
	private final PortalCache<Serializable, SoyTofuCacheBag> _portalCache;

}