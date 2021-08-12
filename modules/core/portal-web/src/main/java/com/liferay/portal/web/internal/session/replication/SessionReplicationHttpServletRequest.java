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

package com.liferay.portal.web.internal.session.replication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.security.Principal;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

/**
 * @author Dante Wang
 */
public class SessionReplicationHttpServletRequest
	implements HttpServletRequest {

	public SessionReplicationHttpServletRequest(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	@Override
	public boolean authenticate(HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		return _httpServletRequest.authenticate(httpServletResponse);
	}

	@Override
	public String changeSessionId() {
		return _httpServletRequest.changeSessionId();
	}

	@Override
	public AsyncContext getAsyncContext() {
		return _httpServletRequest.getAsyncContext();
	}

	@Override
	public Object getAttribute(String name) {
		return _httpServletRequest.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return _httpServletRequest.getAttributeNames();
	}

	@Override
	public String getAuthType() {
		return _httpServletRequest.getAuthType();
	}

	@Override
	public String getCharacterEncoding() {
		return _httpServletRequest.getCharacterEncoding();
	}

	@Override
	public int getContentLength() {
		return _httpServletRequest.getContentLength();
	}

	@Override
	public long getContentLengthLong() {
		return _httpServletRequest.getContentLengthLong();
	}

	@Override
	public String getContentType() {
		return _httpServletRequest.getContentType();
	}

	@Override
	public String getContextPath() {
		return _httpServletRequest.getContextPath();
	}

	@Override
	public Cookie[] getCookies() {
		return _httpServletRequest.getCookies();
	}

	@Override
	public long getDateHeader(String name) {
		return _httpServletRequest.getDateHeader(name);
	}

	@Override
	public DispatcherType getDispatcherType() {
		return _httpServletRequest.getDispatcherType();
	}

	@Override
	public String getHeader(String name) {
		return _httpServletRequest.getHeader(name);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return _httpServletRequest.getHeaderNames();
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		return _httpServletRequest.getHeaders(name);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return _httpServletRequest.getInputStream();
	}

	@Override
	public int getIntHeader(String name) {
		return _httpServletRequest.getIntHeader(name);
	}

	@Override
	public String getLocalAddr() {
		return _httpServletRequest.getLocalAddr();
	}

	@Override
	public Locale getLocale() {
		return _httpServletRequest.getLocale();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return _httpServletRequest.getLocales();
	}

	@Override
	public String getLocalName() {
		return _httpServletRequest.getLocalName();
	}

	@Override
	public int getLocalPort() {
		return _httpServletRequest.getLocalPort();
	}

	@Override
	public String getMethod() {
		return _httpServletRequest.getMethod();
	}

	@Override
	public String getParameter(String name) {
		return _httpServletRequest.getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _httpServletRequest.getParameterMap();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return _httpServletRequest.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String name) {
		return _httpServletRequest.getParameterValues(name);
	}

	@Override
	public Part getPart(String name) throws IOException, ServletException {
		return _httpServletRequest.getPart(name);
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		return _httpServletRequest.getParts();
	}

	@Override
	public String getPathInfo() {
		return _httpServletRequest.getPathInfo();
	}

	@Override
	public String getPathTranslated() {
		return _httpServletRequest.getPathTranslated();
	}

	@Override
	public String getProtocol() {
		return _httpServletRequest.getProtocol();
	}

	@Override
	public String getQueryString() {
		return _httpServletRequest.getQueryString();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return _httpServletRequest.getReader();
	}

	@Override
	public String getRealPath(String path) {
		return _httpServletRequest.getRealPath(path);
	}

	@Override
	public String getRemoteAddr() {
		return _httpServletRequest.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return _httpServletRequest.getRemoteHost();
	}

	@Override
	public int getRemotePort() {
		return _httpServletRequest.getRemotePort();
	}

	@Override
	public String getRemoteUser() {
		return _httpServletRequest.getRemoteUser();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return _httpServletRequest.getRequestDispatcher(path);
	}

	@Override
	public String getRequestedSessionId() {
		return _httpServletRequest.getRequestedSessionId();
	}

	@Override
	public String getRequestURI() {
		return _httpServletRequest.getRequestURI();
	}

	@Override
	public StringBuffer getRequestURL() {
		return _httpServletRequest.getRequestURL();
	}

	@Override
	public String getScheme() {
		return _httpServletRequest.getScheme();
	}

	@Override
	public String getServerName() {
		return _httpServletRequest.getServerName();
	}

	@Override
	public int getServerPort() {
		return _httpServletRequest.getServerPort();
	}

	@Override
	public ServletContext getServletContext() {
		return _httpServletRequest.getServletContext();
	}

	@Override
	public String getServletPath() {
		return _httpServletRequest.getServletPath();
	}

	@Override
	public HttpSession getSession() {
		HttpSession httpSession = _httpServletRequest.getSession();

		if (httpSession == null) {
			return null;
		}

		return new SessionReplicationHttpSessionWrapper(httpSession);
	}

	@Override
	public HttpSession getSession(boolean create) {
		HttpSession httpSession = _httpServletRequest.getSession(create);

		if (httpSession == null) {
			return null;
		}

		return new SessionReplicationHttpSessionWrapper(httpSession);
	}

	@Override
	public Principal getUserPrincipal() {
		return _httpServletRequest.getUserPrincipal();
	}

	@Override
	public boolean isAsyncStarted() {
		return _httpServletRequest.isAsyncStarted();
	}

	@Override
	public boolean isAsyncSupported() {
		return _httpServletRequest.isAsyncSupported();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return _httpServletRequest.isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return _httpServletRequest.isRequestedSessionIdFromUrl();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return _httpServletRequest.isRequestedSessionIdFromURL();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return _httpServletRequest.isRequestedSessionIdValid();
	}

	@Override
	public boolean isSecure() {
		return _httpServletRequest.isSecure();
	}

	@Override
	public boolean isUserInRole(String role) {
		return _httpServletRequest.isUserInRole(role);
	}

	@Override
	public void login(String userName, String password)
		throws ServletException {

		_httpServletRequest.login(userName, password);
	}

	@Override
	public void logout() throws ServletException {
		_httpServletRequest.logout();
	}

	@Override
	public void removeAttribute(String name) {
		_httpServletRequest.removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		_httpServletRequest.setAttribute(name, value);
	}

	@Override
	public void setCharacterEncoding(String enc)
		throws UnsupportedEncodingException {

		_httpServletRequest.setCharacterEncoding(enc);
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		return _httpServletRequest.startAsync();
	}

	@Override
	public AsyncContext startAsync(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IllegalStateException {

		return _httpServletRequest.startAsync(servletRequest, servletResponse);
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(
			Class<T> httpUpgradeHandlerClass)
		throws IOException, ServletException {

		return _httpServletRequest.upgrade(httpUpgradeHandlerClass);
	}

	private final HttpServletRequest _httpServletRequest;

}