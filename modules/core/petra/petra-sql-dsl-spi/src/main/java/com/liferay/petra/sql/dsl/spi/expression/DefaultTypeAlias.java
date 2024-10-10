/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.sql.dsl.spi.expression;

import com.liferay.petra.sql.dsl.ast.ASTNodeListener;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.TypeAlias;

import java.util.function.Consumer;

/**
 * @author Tina Tian
 */
public class DefaultTypeAlias<T>
	extends DefaultAlias<T> implements TypeAlias<T> {

	public DefaultTypeAlias(
		Expression<T> expression, String name, Class<?> javaType) {

		super(expression, name);

		_javaType = javaType;
	}

	@Override
	public Class<?> getJavaType() {
		return _javaType;
	}

	@Override
	protected void doToSQL(
		Consumer<String> consumer, ASTNodeListener astNodeListener) {

		super.doToSQL(consumer, astNodeListener);

		astNodeListener.process(this);
	}

	private final Class<?> _javaType;

}