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

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.repository.RepositoryConfigurationBuilder;
import com.liferay.portal.kernel.repository.registry.RepositoryDefiner;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.language.LanguageImpl;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.lang.reflect.InvocationHandler;

import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Leon Chi
 */
public class RepositoryClassDefinitionCatalogImplTest {

	@BeforeClass
	public static void setUpClass() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		Registry registry = RegistryUtil.getRegistry();

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(new LanguageImpl());

		_repositoryClassDefinitionCatalogImpl =
			new RepositoryClassDefinitionCatalogImpl();

		_repositoryClassDefinitionCatalogImpl.afterPropertiesSet();

		_serviceRegistration1 = registry.registerService(
			RepositoryDefiner.class,
			(RepositoryDefiner)ProxyUtil.newProxyInstance(
				RepositoryDefiner.class.getClassLoader(),
				new Class<?>[] {RepositoryDefiner.class},
				_getRepositoryDefinerInvocationHandler(
					_EXTERNAL_REPOSITORY_DEFINER_CLASS_NAME, true)));

		_serviceRegistration2 = registry.registerService(
			RepositoryDefiner.class,
			(RepositoryDefiner)ProxyUtil.newProxyInstance(
				RepositoryDefiner.class.getClassLoader(),
				new Class<?>[] {RepositoryDefiner.class},
				_getRepositoryDefinerInvocationHandler(
					_REPOSITORY_DEFINER_CLASS_NAME, false)));
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration1.unregister();
		_serviceRegistration2.unregister();
	}

	@Test
	public void testGetExternalRepositoryClassDefinitions() {
		Iterable<RepositoryClassDefinition> externalRepositoryClassDefinitions =
			_repositoryClassDefinitionCatalogImpl.
				getExternalRepositoryClassDefinitions();

		Assert.assertTrue(
			_REPOSITORY_DEFINER_CLASS_NAME + " not found in " +
				externalRepositoryClassDefinitions,
			_containsExternalRepositoryDefiner(
				externalRepositoryClassDefinitions));
	}

	@Test
	public void testGetExternalRepositoryClassNames() {
		Collection<String> externalRepositoryClassNames =
			_repositoryClassDefinitionCatalogImpl.
				getExternalRepositoryClassNames();

		Assert.assertTrue(
			externalRepositoryClassNames.toString(),
			externalRepositoryClassNames.contains(
				_EXTERNAL_REPOSITORY_DEFINER_CLASS_NAME));
	}

	@Test
	public void testGetRepositoryClassDefinition() {
		RepositoryClassDefinition repositoryClassDefinition =
			_repositoryClassDefinitionCatalogImpl.getRepositoryClassDefinition(
				_REPOSITORY_DEFINER_CLASS_NAME);

		Assert.assertEquals(
			_REPOSITORY_DEFINER_CLASS_NAME,
			repositoryClassDefinition.getClassName());

		RepositoryClassDefinition repositoryExternalClassDefinition =
			_repositoryClassDefinitionCatalogImpl.getRepositoryClassDefinition(
				_EXTERNAL_REPOSITORY_DEFINER_CLASS_NAME);

		Assert.assertEquals(
			_EXTERNAL_REPOSITORY_DEFINER_CLASS_NAME,
			repositoryExternalClassDefinition.getClassName());
	}

	private static InvocationHandler _getRepositoryDefinerInvocationHandler(
		String className, boolean external) {

		return (proxy, method, args) -> {
			if ("getClassName".equals(method.getName())) {
				return className;
			}

			if ("getRepositoryConfiguration".equals(method.getName())) {
				RepositoryConfigurationBuilder repositoryConfigurationBuilder =
					new RepositoryConfigurationBuilder();

				return repositoryConfigurationBuilder.build();
			}

			if ("isExternalRepository".equals(method.getName())) {
				return external;
			}

			return null;
		};
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
		"TestExternalRepositoryDefiner";

	private static final String _REPOSITORY_DEFINER_CLASS_NAME =
		"TestRepositoryDefiner";

	private static RepositoryClassDefinitionCatalogImpl
		_repositoryClassDefinitionCatalogImpl;
	private static ServiceRegistration<RepositoryDefiner> _serviceRegistration1;
	private static ServiceRegistration<RepositoryDefiner> _serviceRegistration2;

}