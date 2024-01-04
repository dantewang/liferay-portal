/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.benchmark.test.internal;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import java.nio.ByteBuffer;

import java.util.List;
import java.util.Map;

/**
 * @author Tina Tian
 */
public class HttpUtil {

	public static HttpResponse doGet(String uri) throws Exception {
		return doGet(uri, null);
	}

	public static HttpResponse doGet(String uri, String[][] headers)
		throws Exception {

		HttpURLConnection httpURLConnection = null;

		long startTime = System.currentTimeMillis();

		try {
			httpURLConnection = _openConnection(uri);

			if (headers != null) {
				for (String[] header : headers) {
					httpURLConnection.addRequestProperty(header[0], header[1]);
				}
			}

			_setCSRFToken(httpURLConnection);

			httpURLConnection.setRequestMethod("GET");

			return _readConnection(httpURLConnection);
		}
		catch (IOException ioException) {
			_consumeErrorConnection(httpURLConnection);

			throw new IOException(
				"Wait time : " + (System.currentTimeMillis() - startTime) +
					"ms",
				ioException);
		}
		finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	public static HttpResponse doPost(
			String uri, String content, String[] contentType)
		throws Exception {

		return doPost(uri, new String[][] {contentType}, null, content);
	}

	public static HttpResponse doPost(String uri, String[][] parameters)
		throws Exception {

		return doPost(uri, null, parameters, null);
	}

	public static HttpResponse doPost(
			String uri, String[][] headers, String[][] parameters,
			String content)
		throws Exception {

		long startTime = System.currentTimeMillis();

		HttpURLConnection httpURLConnection = null;

		try {
			httpURLConnection = _openConnection(uri);

			if (headers != null) {
				for (String[] header : headers) {
					httpURLConnection.addRequestProperty(header[0], header[1]);
				}
			}

			_setCSRFToken(httpURLConnection);

			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);

			PrintWriter printWriter = new PrintWriter(
				httpURLConnection.getOutputStream());

			if (parameters != null) {
				boolean first = true;

				for (String[] parameter : parameters) {
					if (first) {
						first = false;
					}
					else {
						printWriter.print(CharPool.AMPERSAND);
					}

					printWriter.print(parameter[0]);
					printWriter.print(CharPool.EQUAL);
					printWriter.print(URLEncoder.encode(parameter[1], "UTF-8"));
				}
			}

			if (content != null) {
				printWriter.print(content);
			}

			printWriter.close();

			return _readConnection(httpURLConnection);
		}
		catch (IOException ioException) {
			_consumeErrorConnection(httpURLConnection);

			throw new IOException(
				"Wait time : " + (System.currentTimeMillis() - startTime) +
					"ms",
				ioException);
		}
		finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	public static class HttpResponse {

		public HttpResponse(
			int statusCode, String statusLine,
			Map<String, List<String>> headers, String text, long duration) {

			_statusCode = statusCode;
			_statusLine = statusLine;
			_text = text;
			_duration = duration;

			_headersMap = headers;
		}

		public long getDuration() {
			return _duration;
		}

		public String getHeader(String name) {
			List<String> headers = _headersMap.get(name);

			if (ListUtil.isEmpty(headers)) {
				return StringPool.BLANK;
			}

			StringBundler sb = new StringBundler(headers.size() * 2);

			for (String header : headers) {
				sb.append(header);
				sb.append(StringPool.COMMA);
			}

			sb.setIndex(sb.index() - 1);

			return sb.toString();
		}

		public Map<String, List<String>> getHeaders() {
			return _headersMap;
		}

		public InputStream getInputStream() {
			return _inputStream;
		}

		public String getRedirect() throws Exception {
			String location = getHeader("Location");

			if (location.contains("%")) {
				location = URLDecoder.decode(location, "UTF-8");
			}

			return location;
		}

		public int getStatusCode() {
			return _statusCode;
		}

		public String getStatusLine() {
			return _statusLine;
		}

		public String getText() {
			return _text;
		}

		@Override
		public String toString() {
			StringBundler sb = new StringBundler();

			sb.append(_statusCode);
			sb.append(CharPool.SPACE);
			sb.append(_statusLine);
			sb.append(StringPool.NEW_LINE);

			for (Map.Entry<String, List<String>> entry :
					_headersMap.entrySet()) {

				String key = entry.getKey();

				if (key != null) {
					sb.append(key);
					sb.append(StringPool.COLON);
				}

				for (String value : entry.getValue()) {
					sb.append(value);
					sb.append(StringPool.COMMA);
				}

				sb.setStringAt(StringPool.NEW_LINE, sb.index() - 1);
			}

			sb.append(_text);

			return sb.toString();
		}

		private final long _duration;
		private final Map<String, List<String>> _headersMap;
		private InputStream _inputStream;
		private final int _statusCode;
		private final String _statusLine;
		private final String _text;

	}

	private static ByteBuffer _collect(InputStream inputStream)
		throws Exception {

		byte[] byteBuffer = _byteBuffer.get();

		int offset = 0;

		int length = -1;

		int left = byteBuffer.length;

		while ((length = inputStream.read(byteBuffer, offset, left)) != -1) {
			left -= length;
			offset += length;

			if (left == 0) {
				int newLength = byteBuffer.length * 6 / 5;

				byte[] newBuffer = new byte[newLength];

				System.arraycopy(
					byteBuffer, 0, newBuffer, 0, byteBuffer.length);

				left = newLength - byteBuffer.length;

				byteBuffer = newBuffer;

				_byteBuffer.set(byteBuffer);
			}
		}

		inputStream.close();

		return ByteBuffer.wrap(byteBuffer, 0, offset);
	}

	private static void _consumeErrorConnection(
			HttpURLConnection httpURLConnection)
		throws IOException {

		if (httpURLConnection != null) {
			try (InputStream inputStream = httpURLConnection.getErrorStream()) {
				if (inputStream != null) {
					while (inputStream.read() != -1);
				}
			}
			catch (IOException ioException) {
				throw new IOException(
					"Failed to consume error connection data", ioException);
			}
		}
	}

	private static HttpURLConnection _openConnection(String uri)
		throws IOException {

		ContextUtil.Context context = ContextUtil.getContext();

		URL url = new URL("http", context.getHostName(), 8080, uri);

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		httpURLConnection.setConnectTimeout(0);
		httpURLConnection.setReadTimeout(0);

		return httpURLConnection;
	}

	private static HttpResponse _readConnection(
			HttpURLConnection httpURLConnection)
		throws Exception {

		long startTime = System.currentTimeMillis();

		httpURLConnection.connect();

		int statusCode = httpURLConnection.getResponseCode();

		String statusLine = httpURLConnection.getResponseMessage();

		Map<String, List<String>> responseHeaders =
			httpURLConnection.getHeaderFields();

		ByteBuffer byteBuffer = _collect(httpURLConnection.getInputStream());

		String text = new String(
			byteBuffer.array(), 0, byteBuffer.limit(), "utf-8");

		long duration = System.currentTimeMillis() - startTime;

		return new HttpResponse(
			statusCode, statusLine, responseHeaders, text, duration);
	}

	private static void _setCSRFToken(HttpURLConnection httpURLConnection) {
		ContextUtil.Context context = ContextUtil.getContext();

		String csrfToken = context.getCSRFToken();

		if (csrfToken != null) {
			httpURLConnection.addRequestProperty("X-Csrf-Token", csrfToken);
		}
	}

	private static final ThreadLocal<byte[]> _byteBuffer =
		CentralizedThreadLocal.withInitial(() -> new byte[8192]);

	static {
		HttpURLConnection.setFollowRedirects(false);

		CookieHandler.setDefault(
			new CookieManager(null, CookiePolicy.ACCEPT_ALL));
	}

}