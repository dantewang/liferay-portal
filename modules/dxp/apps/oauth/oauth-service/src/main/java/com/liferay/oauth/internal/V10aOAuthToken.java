/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.oauth.internal;

import com.liferay.oauth.util.OAuthToken;
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.PwdGenerator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Hai Yu
 */
@Component(immediate = true, service = OAuthToken.class)
public class V10aOAuthToken implements OAuthToken {

	@Override
	public String randomizeToken(String token) {
		return DigesterUtil.digestHex(
			Digester.MD5, token, PwdGenerator.getPassword());
	}

}