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

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.journal.model.JournalArticle;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.UserPersonalSite;
import com.liferay.portal.model.impl.ClassNameModelImpl;
import com.liferay.wiki.model.WikiPage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class ClassNameDataFactory extends BaseDataFactory {

	public static ClassNameDataFactory getInstance() {
		return _classNameDataFactory;
	}

	public long[] getAssetClassNameIds() {
		long[] assetClassNameIds = new long[3];

		ClassNameModel blogEntrysClassNameModel = _classNameModels.get(
			BlogsEntry.class.getName());
		ClassNameModel journalArticleClassNameModel = _classNameModels.get(
			JournalArticle.class.getName());
		ClassNameModel wikiPageClassNameModel = _classNameModels.get(
			WikiPage.class.getName());

		assetClassNameIds[0] = blogEntrysClassNameModel.getClassNameId();
		assetClassNameIds[1] = journalArticleClassNameModel.getClassNameId();
		assetClassNameIds[2] = wikiPageClassNameModel.getClassNameId();

		return assetClassNameIds;
	}

	public String getClassName(BaseModel<?> baseModel) {
		long classNameId;

		if (baseModel instanceof DDMStructureModel) {
			DDMStructureModel ddmStructureModel = (DDMStructureModel)baseModel;

			classNameId = ddmStructureModel.getClassNameId();
		}
		else {
			DDMTemplateModel ddmTemplateModel = (DDMTemplateModel)baseModel;

			classNameId = ddmTemplateModel.getResourceClassNameId();
		}

		for (ClassNameModel classNameModel : _classNameModels.values()) {
			if (classNameModel.getClassNameId() == classNameId) {
				return classNameModel.getValue();
			}
		}

		throw new RuntimeException(
			"Unable to find class name for id " + classNameId);
	}

	public long getClassNameId(String className) {
		ClassNameModel classNameModel = _classNameModels.get(
			_classNames.get(className));

		return classNameModel.getClassNameId();
	}

	public Collection<ClassNameModel> getClassNameModels() {
		return _classNameModels.values();
	}

	private ClassNameDataFactory() {
		List<String> models = ModelHintsUtil.getModels();

		models.add(Layout.class.getName());
		models.add(UserPersonalSite.class.getName());

		models.add(_getMBDiscussionCombinedClassName(BlogsEntry.class));
		models.add(_getMBDiscussionCombinedClassName(WikiPage.class));

		for (String model : models) {
			ClassNameModel classNameModel = new ClassNameModelImpl();

			classNameModel.setClassNameId(counter.get());
			classNameModel.setValue(model);

			_classNameModels.put(model, classNameModel);

			if (!model.contains(StringPool.UNDERLINE)) {
				int index = model.lastIndexOf(StringPool.PERIOD) + 1;

				_classNames.put(model.substring(index), model);
			}
			else {
				String[] combineClassNames = model.split(StringPool.UNDERLINE);
				StringBundler sb = new StringBundler(4);

				for (String combineClassName : combineClassNames) {
					int index =
						combineClassName.lastIndexOf(StringPool.PERIOD) + 1;

					sb.append(combineClassName.substring(index));

					sb.append(StringPool.UNDERLINE);
				}

				sb.setIndex(3);

				_classNames.put(sb.toString(), model);
			}
		}
	}

	private String _getMBDiscussionCombinedClassName(Class<?> clazz) {
		return StringBundler.concat(
			MBDiscussion.class.getName(), StringPool.UNDERLINE,
			clazz.getName());
	}

	private static ClassNameDataFactory _classNameDataFactory =
		new ClassNameDataFactory();

	private final Map<String, ClassNameModel> _classNameModels =
		new HashMap<>();
	private final Map<String, String> _classNames = new HashMap<>();

}