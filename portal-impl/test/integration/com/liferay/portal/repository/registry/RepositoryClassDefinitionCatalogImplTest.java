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

package com.liferay.portal.repository.registry;

import com.liferay.portal.kernel.repository.DocumentRepository;
import com.liferay.portal.kernel.repository.RepositoryConfiguration;
import com.liferay.portal.kernel.repository.RepositoryConfigurationBuilder;
import com.liferay.portal.kernel.repository.registry.CapabilityRegistry;
import com.liferay.portal.kernel.repository.registry.RepositoryDefiner;
import com.liferay.portal.kernel.repository.registry.RepositoryEventRegistry;
import com.liferay.portal.kernel.repository.registry.RepositoryFactoryRegistry;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Peter Fellwock
 */
public class RepositoryClassDefinitionCatalogImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Registry registry = RegistryUtil.getRegistry();

		TestExternalRepositoryDefiner testExternalRepositoryDefiner =
			new TestExternalRepositoryDefiner();

		Map<String, Object> properties1 = new HashMap<>();

		properties1.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration1 = registry.registerService(
			RepositoryDefiner.class, testExternalRepositoryDefiner,
			properties1);

		TestRepositoryDefiner testRepositoryDefiner =
			new TestRepositoryDefiner();

		Map<String, Object> properties2 = new HashMap<>();

		properties2.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration2 = registry.registerService(
			RepositoryDefiner.class, testRepositoryDefiner, properties2);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration1.unregister();
		_serviceRegistration2.unregister();
	}

	@Test
	public void testGetExternalRepositoryClassDefinitions() {
		Iterable<RepositoryClassDefinition> repositoryClassDefinitions =
			RepositoryClassDefinitionCatalogUtil.
				getExternalRepositoryClassDefinitions();

		Assert.assertTrue(
			_REPOSITORY_DEFINER_CLASS_NAME + " not found in " +
				repositoryClassDefinitions,
			_containsExternalRepositoryDefiner(repositoryClassDefinitions));
	}

	@Test
	public void testGetExternalRepositoryClassNames() {
		Collection<String> externalRepositoryClassNames =
			RepositoryClassDefinitionCatalogUtil.
				getExternalRepositoryClassNames();

		Assert.assertTrue(
			externalRepositoryClassNames.toString(),
			externalRepositoryClassNames.contains(
				_EXTERNAL_REPOSITORY_DEFINER_CLASS_NAME));
	}

	@Test
	public void testGetRepositoryClassDefinition() {
		RepositoryClassDefinition repositoryClassDefinition =
			RepositoryClassDefinitionCatalogUtil.getRepositoryClassDefinition(
				_REPOSITORY_DEFINER_CLASS_NAME);

		Assert.assertEquals(
			_REPOSITORY_DEFINER_CLASS_NAME,
			repositoryClassDefinition.getClassName());

		RepositoryClassDefinition repositoryExternalClassDefinition =
			RepositoryClassDefinitionCatalogUtil.getRepositoryClassDefinition(
				_EXTERNAL_REPOSITORY_DEFINER_CLASS_NAME);

		Assert.assertEquals(
			_EXTERNAL_REPOSITORY_DEFINER_CLASS_NAME,
			repositoryExternalClassDefinition.getClassName());
	}

	@Test
	public void testInstanceGetExternalRepositoryClassDefinitions() {
		RepositoryClassDefinitionCatalog repositoryClassDefinitionCatalog =
			RepositoryClassDefinitionCatalogUtil.
				getRepositoryClassDefinitionCatalog();

		Iterable<RepositoryClassDefinition> repositoryClassDefinitions =
			repositoryClassDefinitionCatalog.
				getExternalRepositoryClassDefinitions();

		Assert.assertTrue(
			_REPOSITORY_DEFINER_CLASS_NAME + " not found in " +
				repositoryClassDefinitions,
			_containsExternalRepositoryDefiner(repositoryClassDefinitions));
	}

	@Test
	public void testInstanceGetExternalRepositoryClassNames() {
		RepositoryClassDefinitionCatalog repositoryClassDefinitionCatalog =
			RepositoryClassDefinitionCatalogUtil.
				getRepositoryClassDefinitionCatalog();

		Collection<String> externalRepositoryClassNames =
			repositoryClassDefinitionCatalog.getExternalRepositoryClassNames();

		Assert.assertTrue(
			externalRepositoryClassNames.toString(),
			externalRepositoryClassNames.contains(
				_EXTERNAL_REPOSITORY_DEFINER_CLASS_NAME));
	}

	@Test
	public void testInstanceGetRepositoryClassDefinition() {
		RepositoryClassDefinitionCatalog repositoryClassDefinitionCatalog =
			RepositoryClassDefinitionCatalogUtil.
				getRepositoryClassDefinitionCatalog();

		RepositoryClassDefinition repositoryClassDefinition =
			repositoryClassDefinitionCatalog.getRepositoryClassDefinition(
				_REPOSITORY_DEFINER_CLASS_NAME);

		Assert.assertEquals(
			_REPOSITORY_DEFINER_CLASS_NAME,
			repositoryClassDefinition.getClassName());

		RepositoryClassDefinition repositoryExternalClassDefinition =
			repositoryClassDefinitionCatalog.getRepositoryClassDefinition(
				_EXTERNAL_REPOSITORY_DEFINER_CLASS_NAME);

		Assert.assertEquals(
			_EXTERNAL_REPOSITORY_DEFINER_CLASS_NAME,
			repositoryExternalClassDefinition.getClassName());
	}

	private boolean _containsExternalRepositoryDefiner(
		Iterable<RepositoryClassDefinition> repositoryClassDefinitions) {

		for (RepositoryClassDefinition repositoryClassDefinition :
				repositoryClassDefinitions) {

			if (_EXTERNAL_REPOSITORY_DEFINER_CLASS_NAME.equals(
					repositoryClassDefinition.getClassName())) {

				return true;
			}
		}

		return false;
	}

	private static final String _EXTERNAL_REPOSITORY_DEFINER_CLASS_NAME =
		TestExternalRepositoryDefiner.class.getName();

	private static final String _REPOSITORY_DEFINER_CLASS_NAME =
		TestRepositoryDefiner.class.getName();

	private static ServiceRegistration<RepositoryDefiner> _serviceRegistration1;
	private static ServiceRegistration<RepositoryDefiner> _serviceRegistration2;

	private static class TestExternalRepositoryDefiner
		implements RepositoryDefiner {

		public TestExternalRepositoryDefiner() {
			RepositoryConfigurationBuilder repositoryConfigurationBuilder =
				new RepositoryConfigurationBuilder();

			_repositoryConfiguration = repositoryConfigurationBuilder.build();
		}

		@Override
		public String getClassName() {
			return TestExternalRepositoryDefiner.class.getName();
		}

		@Override
		public RepositoryConfiguration getRepositoryConfiguration() {
			return _repositoryConfiguration;
		}

		@Override
		public String getRepositoryTypeLabel(Locale locale) {
			return null;
		}

		@Override
		public boolean isExternalRepository() {
			return true;
		}

		@Override
		public void registerCapabilities(
			CapabilityRegistry<DocumentRepository> capabilityRegistry) {
		}

		@Override
		public void registerRepositoryEventListeners(
			RepositoryEventRegistry repositoryEventRegistry) {
		}

		@Override
		public void registerRepositoryFactory(
			RepositoryFactoryRegistry repositoryFactoryRegistry) {
		}

		private final RepositoryConfiguration _repositoryConfiguration;

	}

	private static class TestRepositoryDefiner implements RepositoryDefiner {

		public TestRepositoryDefiner() {
			RepositoryConfigurationBuilder repositoryConfigurationBuilder =
				new RepositoryConfigurationBuilder();

			_repositoryConfiguration = repositoryConfigurationBuilder.build();
		}

		@Override
		public String getClassName() {
			return TestRepositoryDefiner.class.getName();
		}

		@Override
		public RepositoryConfiguration getRepositoryConfiguration() {
			return _repositoryConfiguration;
		}

		@Override
		public String getRepositoryTypeLabel(Locale locale) {
			return null;
		}

		@Override
		public boolean isExternalRepository() {
			return false;
		}

		@Override
		public void registerCapabilities(
			CapabilityRegistry<DocumentRepository> capabilityRegistry) {
		}

		@Override
		public void registerRepositoryEventListeners(
			RepositoryEventRegistry repositoryEventRegistry) {
		}

		@Override
		public void registerRepositoryFactory(
			RepositoryFactoryRegistry repositoryFactoryRegistry) {
		}

		private final RepositoryConfiguration _repositoryConfiguration;

	}

}