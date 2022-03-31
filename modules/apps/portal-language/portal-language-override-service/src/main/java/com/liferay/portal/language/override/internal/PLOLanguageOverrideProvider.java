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

package com.liferay.portal.language.override.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterLink;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.language.LanguageOverrideProvider;
import com.liferay.portal.language.override.internal.provider.PLOOriginalTranslationThreadLocal;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	service = {
		IdentifiableOSGiService.class, LanguageOverrideProvider.class,
		PLOLanguageOverrideProvider.class
	}
)
public class PLOLanguageOverrideProvider
	implements IdentifiableOSGiService, LanguageOverrideProvider {

	@Override
	public String get(String key, Locale locale) {
		if (PLOOriginalTranslationThreadLocal.isUseOriginalTranslation()) {
			return null;
		}

		Map<String, String> overrideMap = _getOverrideMap(
			CompanyThreadLocal.getCompanyId(), locale);

		return overrideMap.get(key);
	}

	@Override
	public String getOSGiServiceIdentifier() {
		return PLOLanguageOverrideProvider.class.getName();
	}

	@Override
	public Set<String> keySet(Locale locale) {
		if (PLOOriginalTranslationThreadLocal.isUseOriginalTranslation()) {
			return Collections.emptySet();
		}

		Map<String, String> overrideMap = _getOverrideMap(
			CompanyThreadLocal.getCompanyId(), locale);

		return overrideMap.keySet();
	}

	@Activate
	protected void activate() {
		_overrideMaps = new ConcurrentHashMap<>();
	}

	protected void clear(long companyId, String languageId) {
		String key = _encodeKey(companyId, languageId);

		_clear(key);

		if (!_clusterLink.isEnabled()) {
			return;
		}

		try {
			MethodHandler methodHandler = new MethodHandler(
				_clearMethodKey, key);

			ClusterRequest clusterRequest =
				ClusterRequest.createMulticastRequest(methodHandler, true);

			clusterRequest.setFireAndForget(true);

			_clusterExecutor.execute(clusterRequest);
		}
		catch (Throwable throwable) {
			_log.error("Unable to execute on cluster", throwable);
		}
	}

	private void _clear(String key) {
		_overrideMaps.remove(key);
	}

	private String _encodeKey(long companyId, String languageId) {
		return StringBundler.concat(companyId, StringPool.POUND, languageId);
	}

	private Map<String, String> _getOverrideMap(long companyId, Locale locale) {
		String languageId = LanguageUtil.getLanguageId(locale);

		String key = _encodeKey(companyId, languageId);

		HashMap<String, String> overrideMap = _overrideMaps.get(key);

		if (overrideMap == null) {
			overrideMap = new HashMap<>();

			for (PLOEntry ploEntry :
					_ploEntryLocalService.getPLOEntries(
						companyId, languageId)) {

				overrideMap.put(ploEntry.getKey(), ploEntry.getValue());
			}

			_overrideMaps.put(key, overrideMap);
		}

		return overrideMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PLOLanguageOverrideProvider.class.getName());

	private static final MethodKey _clearMethodKey = new MethodKey(
		PLOLanguageOverrideProvider.class, "_clear", String.class);

	@Reference
	private ClusterExecutor _clusterExecutor;

	@Reference
	private ClusterLink _clusterLink;

	private Map<String, HashMap<String, String>> _overrideMaps;

	@Reference
	private PLOEntryLocalService _ploEntryLocalService;

}