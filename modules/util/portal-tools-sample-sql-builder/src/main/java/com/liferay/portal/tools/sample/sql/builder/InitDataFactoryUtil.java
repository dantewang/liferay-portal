package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.impl.AccountModelImpl;
import com.liferay.portal.model.impl.ClassNameModelImpl;
import com.liferay.portal.model.impl.CompanyModelImpl;
import com.liferay.util.SimpleCounter;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class InitDataFactoryUtil {

	public static String getResource(Class<?> clazz, String resourceName)
		throws Exception {

		List<String> lines = new ArrayList<>();

		StringUtil.readLines(
			getResourceInputStream(clazz, resourceName), lines);

		return StringUtil.merge(lines, StringPool.SPACE);
	}

	public static InputStream getResourceInputStream(
		Class<?> clazz, String resourceName) {

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			_DEPENDENCIES_DIR + resourceName);
	}

	public static Map<String, ClassNameModel> initClassNameModels(
		SimpleCounter simpleCounter) {

		Map<String, ClassNameModel> classNameModels = new HashMap<>();
		List<String> models = ModelHintsUtil.getModels();
		SimpleCounter counter = simpleCounter;

		for (String model : models) {
			ClassNameModel classNameModel = new ClassNameModelImpl();

			long classNameId = counter.get();

			classNameModel.setClassNameId(classNameId);

			classNameModel.setValue(model);

			classNameModels.put(model, classNameModel);
		}

		return classNameModels;
	}
	
	public static AccountModel initAccountModel(
		long companyId, long accountId) {

		AccountModel accountModel = new AccountModelImpl();

		accountModel.setAccountId(accountId);
		accountModel.setCompanyId(companyId);
		accountModel.setCreateDate(new Date());
		accountModel.setModifiedDate(new Date());
		accountModel.setName("Liferay");
		accountModel.setLegalName("Liferay, Inc.");

		return accountModel;
	}

	public static CompanyModel initCompanyModel(
		long companyId, long accountId) {

		CompanyModel companyModel = new CompanyModelImpl();

		companyModel.setCompanyId(companyId);
		companyModel.setAccountId(accountId);
		companyModel.setWebId("liferay.com");
		companyModel.setMx("liferay.com");
		companyModel.setActive(true);

		return companyModel;
	}

	private static final String _DEPENDENCIES_DIR =
		"com/liferay/portal/tools/sample/sql/builder/dependencies/";

}