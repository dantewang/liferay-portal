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

package com.liferay.util.ant;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author Lily Chi
 */
public class GetPathTask extends Task {

	@Override
	public void execute() throws BuildException {
		String baseDIR = _rootDir;

		if (_includeClassFile) {
			String[] classNames = _classNames.split(",");
			StringBuilder classPropertyValue = new StringBuilder();
			List classResultList = new ArrayList();

			for (String className : classNames) {
				String classFileName = className + ".class";

				findFiles(baseDIR, classFileName, classResultList);

				if (classResultList.isEmpty()) {
					_LOGGER.log(
						Level.WARNING, "{0} does not exist!", classFileName);
				}
				else {
					_classResultList.addAll(classResultList);
				}

				classResultList.removeAll(classResultList);
			}

			if (!_classResultList.isEmpty()) {
				for (int i = 0; i < _classResultList.size(); i++) {
					classPropertyValue.append(
						String.valueOf(_classResultList.get(i)));
					classPropertyValue.append(",");
				}

				classPropertyValue = classPropertyValue.deleteCharAt(
					classPropertyValue.lastIndexOf(","));

				getProject().setProperty(
					_classFiles, classPropertyValue.toString());
			}
		}

		if (_includeSrcFile) {
			_srcFileName = _srcFileName + ".java";
			List srcResultList = new ArrayList();
			StringBuilder srcPropertyValue = new StringBuilder();

			findFiles(baseDIR, _srcFileName, srcResultList);

			if (srcResultList.isEmpty()) {
				_LOGGER.log(Level.WARNING, "Java File does not exist!");
			}
			else {
				for (int i = 0; i < srcResultList.size(); i++) {
					String srcPath = String.valueOf(srcResultList.get(i));

					if (_ifMathch(srcPath, _absolutePathClassFile)) {
						int endIndex = srcPath.indexOf("src");

						srcPropertyValue.append(srcPath.substring(0, endIndex));
					}
				}

				getProject().setProperty(
					_srcFileBaseDir, srcPropertyValue.toString());
			}
		}
	}

	public void findFiles(
		String baseDirPath, String targetFileName, List fileList) {

		String tempName = null;

		File baseDir = new File(baseDirPath);

		if (!baseDir.exists() || !baseDir.isDirectory()) {
			return;
		}
		else {
			String[] baseDirfileList = baseDir.list();

			File readfile = null;

			for (int i = 0; i < baseDirfileList.length; i++) {
				readfile = new File(
					baseDirPath + File.separator + baseDirfileList[i]);

				if (!readfile.isDirectory()) {
					tempName = readfile.getName();

					if (wildcardMatch(targetFileName, tempName)) {
						fileList.add(readfile.getAbsoluteFile());
					}
				}
				else if (readfile.isDirectory()) {
					String subdirpath = null;

					subdirpath =
						baseDirPath + File.separator + baseDirfileList[i];

					findFiles(subdirpath, targetFileName, fileList);
				}
			}
		}
	}

	public void setAbsolutePathClassFile(String absolutePathClassFile) {
		_absolutePathClassFile = absolutePathClassFile;
	}

	public void setClassFiles(String classFiles) {
		_classFiles = classFiles;
	}

	public void setclassNames(String classNames) {
		_classNames = classNames;
	}

	public void setIncludeClassFile(boolean includeClassFile) {
		_includeClassFile = includeClassFile;
	}

	public void setIncludeSrcFile(boolean includeSrcFile) {
		_includeSrcFile = includeSrcFile;
	}

	public void setRootDir(String rootDir) {
		_rootDir = rootDir;
	}

	public void setSrcFileBaseDir(String srcFilesBaseDir) {
		_srcFileBaseDir = srcFilesBaseDir;
	}

	public void setSrcFileName(String srcFileName) {
		_srcFileName = srcFileName;
	}

	public boolean wildcardMatch(String targetFileName, String fileName) {
		int patternLength = targetFileName.length();
		int strLength = fileName.length();
		int strIndex = 0;
		char ch;

		for (int patternIndex = 0; patternIndex < patternLength;
			patternIndex++) {

			ch = targetFileName.charAt(patternIndex);

			if (ch == '*') {
				while (strIndex < strLength) {
					String subpattern = targetFileName.substring(
						patternIndex + 1);

					if (wildcardMatch(
							subpattern, fileName.substring(strIndex))) {

						return true;
					}

					strIndex++;
				}
			}
			else {
				if ((strIndex >= strLength) ||
					(ch != fileName.charAt(strIndex))) {

					return false;
				}

				strIndex++;
			}
		}

		if (strIndex == strLength) {
			return true;
		}

		return false;
	}

	private boolean _ifMathch(String srcPath, String classPath) {
		String tempClassValue = classPath.replace(
			_CLASS_PARAMETER, File.separator);

		tempClassValue = tempClassValue.substring(
			0, tempClassValue.lastIndexOf("."));

		if (srcPath.contains(_SRC_PARAMETER)) {
			String tempSrcValue = srcPath.replace(
				_SRC_PARAMETER, File.separator);

			tempSrcValue = tempSrcValue.substring(
				0, tempSrcValue.lastIndexOf("."));

			if (tempSrcValue.equals(tempClassValue)) {
				return true;
			}
			else {
				return false;
			}
		}
		else if (srcPath.contains(_MODULE_SRC_PARAMETER)) {
			String tempSrcValue = srcPath.replace(
				_MODULE_SRC_PARAMETER, File.separator);

			tempSrcValue = tempSrcValue.substring(
				0, tempSrcValue.lastIndexOf("."));

			if (tempSrcValue.equals(tempClassValue)) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	private static final String _CLASS_PARAMETER =
		File.separator + "classes" + File.separator;

	private static final Logger _LOGGER = Logger.getLogger(
		GetPathTask.class.getName());

	private static final String _MODULE_SRC_PARAMETER =
		File.separator + "src" + File.separator + "main" + File.separator +
			"java" + File.separator;

	private static final String _SRC_PARAMETER =
		File.separator + "src" + File.separator;

	private String _absolutePathClassFile;
	private String _classFiles;
	private String _classNames;
	private final List _classResultList = new ArrayList();
	private boolean _includeClassFile;
	private boolean _includeSrcFile;
	private String _rootDir;
	private String _srcFileBaseDir;
	private String _srcFileName;

}