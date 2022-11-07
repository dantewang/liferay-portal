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

package com.liferay.portal.search.internal.indexer;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.QueryConfigContributor;
import com.liferay.portal.search.spi.model.query.contributor.SearchContextContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchConfigurator;
import com.liferay.portal.search.spi.model.registrar.ModelSearchDefinition;
import com.liferay.portal.search.spi.model.registrar.ModelSearchRegistrarHelper;
import com.liferay.portal.search.spi.model.registrar.contributor.ModelSearchDefinitionContributor;
import com.liferay.portal.search.spi.model.result.contributor.ModelSummaryContributor;
import com.liferay.portal.search.spi.model.result.contributor.ModelVisibilityContributor;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author André de Oliveira
 */
@Component(immediate = true, service = ModelSearchRegistrarHelper.class)
public class ModelSearchRegistrarHelperImpl
	implements ModelSearchRegistrarHelper {

	public List<KeywordQueryContributor> getKeywordQueryContributors(
		String indexerClassName) {

		List<KeywordQueryContributor> keywordQueryContributors =
			_keywordQueryContributors.getService(indexerClassName);

		if (keywordQueryContributors == null) {
			return Collections.emptyList();
		}

		return keywordQueryContributors;
	}

	public List<ModelDocumentContributor<?>> getModelDocumentContributors(
		String indexerClassName) {

		List<ModelDocumentContributor<?>> modelDocumentContributors =
			_modelDocumentContributors.getService(indexerClassName);

		if (modelDocumentContributors == null) {
			return Collections.emptyList();
		}

		return modelDocumentContributors;
	}

	public List<QueryConfigContributor> getQueryConfigContributors(
		String indexerClassName) {

		List<QueryConfigContributor> queryConfigContributors =
			_queryConfigContributors.getService(indexerClassName);

		if (queryConfigContributors == null) {
			return Collections.emptyList();
		}

		return queryConfigContributors;
	}

	public List<SearchContextContributor> getSearchContextContributors(
		String indexerClassName) {

		List<SearchContextContributor> searchContextContributors =
			_searchContextContributors.getService(indexerClassName);

		if (searchContextContributors == null) {
			return Collections.emptyList();
		}

		return searchContextContributors;
	}

	@Override
	public ServiceRegistration<?> register(
		Class<? extends BaseModel<?>> clazz, BundleContext bundleContext,
		ModelSearchDefinitionContributor modelSearchDefinitionContributor) {

		return register(
			clazz.getName(), bundleContext, modelSearchDefinitionContributor);
	}

	@Override
	public ServiceRegistration<?> register(
		String className, BundleContext bundleContext,
		ModelSearchDefinitionContributor modelSearchDefinitionContributor) {

		ModelSearchDefinitionImpl modelSearchDefinitionImpl =
			new ModelSearchDefinitionImpl(className);

		modelSearchDefinitionContributor.contribute(modelSearchDefinitionImpl);

		return bundleContext.registerService(
			ModelSearchConfigurator.class,
			new ModelSearchConfiguratorImpl<>(
				modelSearchDefinitionImpl._modelIndexWriterContributor,
				modelSearchDefinitionImpl._modelVisibilityContributor,
				modelSearchDefinitionImpl._modelSearchSettingsImpl,
				modelSearchDefinitionImpl._modelSummaryContributor, this),
			new Hashtable<>(
				Collections.singletonMap("indexer.class.name", className)));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_keywordQueryContributors = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, KeywordQueryContributor.class, "indexer.class.name");

		_queryConfigContributors = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, QueryConfigContributor.class, "indexer.class.name");

		_searchContextContributors = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, SearchContextContributor.class,
			"indexer.class.name");

		_modelDocumentContributors = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext,
			(Class<ModelDocumentContributor<?>>)
				(Class<?>)ModelDocumentContributor.class,
			"indexer.class.name");
	}

	@Deactivate
	protected void deactivate() {
		_keywordQueryContributors.close();
		_queryConfigContributors.close();
		_searchContextContributors.close();
		_modelDocumentContributors.close();
	}

	private ServiceTrackerMap<String, List<KeywordQueryContributor>>
		_keywordQueryContributors;
	private ServiceTrackerMap<String, List<ModelDocumentContributor<?>>>
		_modelDocumentContributors;
	private ServiceTrackerMap<String, List<QueryConfigContributor>>
		_queryConfigContributors;
	private ServiceTrackerMap<String, List<SearchContextContributor>>
		_searchContextContributors;

	private class ModelSearchDefinitionImpl implements ModelSearchDefinition {

		public ModelSearchDefinitionImpl(String className) {
			_modelSearchSettingsImpl = new ModelSearchSettingsImpl(className);
		}

		@Override
		public void setDefaultSelectedFieldNames(
			String... defaultSelectedFieldNames) {

			_modelSearchSettingsImpl.setDefaultSelectedFieldNames(
				defaultSelectedFieldNames);
		}

		@Override
		public void setDefaultSelectedLocalizedFieldNames(
			String... defaultSelectedLocalizedFieldNames) {

			_modelSearchSettingsImpl.setDefaultSelectedLocalizedFieldNames(
				defaultSelectedLocalizedFieldNames);
		}

		@Override
		public void setModelIndexWriteContributor(
			ModelIndexerWriterContributor<?> modelIndexWriterContributor) {

			_modelIndexWriterContributor = modelIndexWriterContributor;
		}

		@Override
		public void setModelSummaryContributor(
			ModelSummaryContributor modelSummaryContributor) {

			_modelSummaryContributor = modelSummaryContributor;
		}

		@Override
		public void setModelVisibilityContributor(
			ModelVisibilityContributor modelVisibilityContributor) {

			_modelVisibilityContributor = modelVisibilityContributor;
		}

		@Override
		public void setSearchResultPermissionFilterSuppressed(
			boolean searchResultPermissionFilterSuppressed) {

			_modelSearchSettingsImpl.setSearchResultPermissionFilterSuppressed(
				searchResultPermissionFilterSuppressed);
		}

		@Override
		public void setSelectAllLocales(boolean selectAllLocales) {
			_modelSearchSettingsImpl.setSelectAllLocales(selectAllLocales);
		}

		@Override
		public void setStagingAware(boolean stagingAware) {
			_modelSearchSettingsImpl.setStagingAware(stagingAware);
		}

		private ModelIndexerWriterContributor<?> _modelIndexWriterContributor;
		private final ModelSearchSettingsImpl _modelSearchSettingsImpl;
		private ModelSummaryContributor _modelSummaryContributor;
		private ModelVisibilityContributor _modelVisibilityContributor;

	}

}