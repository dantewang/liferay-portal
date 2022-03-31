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
import com.liferay.portal.kernel.cluster.Clusterable;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
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
		if (_ploEntryLocalService.getPLOEntriesCount() == 0) {
			return;
		}

		_overrideMaps = new ConcurrentHashMap<>();
	}

	@Clusterable
	protected void clear(long companyId, String languageId) {
		if (_overrideMaps == null) {
			synchronized (this) {
				_overrideMaps = new ConcurrentHashMap<>();
			}
		}

		_overrideMaps.remove(_encodeKey(companyId, languageId));
	}

	private String _encodeKey(long companyId, String languageId) {
		return StringBundler.concat(companyId, StringPool.POUND, languageId);
	}

	private Map<String, String> _getOverrideMap(long companyId, Locale locale) {
		if (_overrideMaps == null) {
			return Collections.emptyMap();
		}

		String languageId = LanguageUtil.getLanguageId(locale);

		return _overrideMaps.computeIfAbsent(
			_encodeKey(companyId, languageId),
			key -> {
				HashMap<String, String> hashMap = new HashMap<>();

				for (PLOEntry ploEntry :
						_ploEntryLocalService.getPLOEntries(
							companyId, languageId)) {

					hashMap.put(ploEntry.getKey(), ploEntry.getValue());
				}

				return hashMap;
			});
	}

	private Map<String, HashMap<String, String>> _overrideMaps;

	@Reference
	private PLOEntryLocalService _ploEntryLocalService;

}