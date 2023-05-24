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

/*******************************************************************************
 * Copyright (c) 2014 Raymond Augé and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 ******************************************************************************/

package com.liferay.portal.osgi.web.http.servlet.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Dante Wang
 * @author Raymond Augé
 */
public class StringPlus {

	@SuppressWarnings("unchecked")
	public static String[] from(Object object) {
		if (object instanceof String) {
			return new String[] {(String)object};
		}
		else if (object instanceof String[]) {
			return (String[])object;
		}
		else if (object instanceof Collection) {
			Collection<?> collection = (Collection<?>)object;

			Iterator<?> iterator = collection.iterator();

			if (!collection.isEmpty() && (iterator.next() instanceof String)) {
				List<String> list = new ArrayList<>((Collection<String>)object);

				return list.toArray(new String[0]);
			}
		}

		return new String[0];
	}

}