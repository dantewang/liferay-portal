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

package com.liferay.oauth.web.internal.util;

import com.liferay.oauth.constants.OAuthConstants;
import com.liferay.petra.string.StringPool;

/**
 * @author Ivica Cardic
 * @author Raymond Augé
 * @author Igor Beslic
 */
public class OAuthUtil {

	public static String getAccessTokenURI() {
		if (_accessTokenURI == null) {
			_accessTokenURI = _getOAuthURI("access_token");
		}

		return _accessTokenURI;
	}

	public static String getAuthorizeURI() {
		if (_authorizeURI == null) {
			_authorizeURI = _getOAuthURI("authorize");
		}

		return _authorizeURI;
	}

	public static String getRequestTokenURI() {
		if (_requestTokenURI == null) {
			_requestTokenURI = _getOAuthURI("request_token");
		}

		return _requestTokenURI;
	}

	private static String _getOAuthURI(String uriSuffix) {
		String oauthPublicPath = null;

		for (String publicPath : OAuthConstants.PUBLIC_PATHS) {
			if (publicPath.endsWith(uriSuffix)) {
				oauthPublicPath = publicPath;

				break;
			}
		}

		if (oauthPublicPath != null) {
			oauthPublicPath = "/c" + oauthPublicPath;
		}
		else {
			oauthPublicPath = StringPool.BLANK;
		}

		return oauthPublicPath;
	}

	private static String _accessTokenURI;
	private static String _authorizeURI;
	private static String _requestTokenURI;

}