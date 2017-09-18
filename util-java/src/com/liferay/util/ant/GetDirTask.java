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

import com.liferay.portal.kernel.util.StringPool;

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
public class GetDirTask extends Task {

	@Override
	public void execute() throws BuildException {
		if (_fileName == null) {
			_LOGGER.log(Level.WARNING, "FileName is not set!");

			return;
		}

		String baseDIR = _rootDir + File.separator + _baseDir;
		String classFileName = _fileName + ".class";

		List classResultList = new ArrayList();

		findFiles(baseDIR, classFileName, classResultList);

		if (classResultList.isEmpty()) {
			_LOGGER.log(Level.WARNING, "{0} does not exist!", classFileName);
		}
		else {
			String propertyValue = classResultList.get(0).toString();

			getProject().setProperty(_classFilePath, propertyValue);
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
			String[] filelist = baseDir.list();

			File readfile = null;

			for (int i = 0; i < filelist.length; i++) {
				readfile = new File(baseDirPath + File.separator + filelist[i]);

				if (!readfile.isDirectory()) {
					tempName = readfile.getName();

					if (wildcardMatch(targetFileName, tempName)) {
						fileList.add(readfile.getAbsoluteFile());
					}
				}
				else if (readfile.isDirectory()) {
					String subdirpath = null;
	
					subdirpath = baseDirPath + File.separator + filelist[i];

					findFiles(subdirpath, targetFileName, fileList);
				}
			}
		}
	}

	public void setBaseDir(String baseDir) {
		_baseDir = baseDir;
	}

	public void setClassFilePath(String classFilePath) {
		_classFilePath = classFilePath;
	}

	public void setfileName(String fileName) {
		_fileName = fileName;
	}

	public void setRootDir(String rootDir) {
		_rootDir = rootDir;
	}

	public boolean wildcardMatch(String pattern, String str) {
		int patternLength = pattern.length();
		int strLength = str.length();
		int strIndex = 0;
		char ch;

		for (int patternIndex = 0; patternIndex < patternLength;
			patternIndex++) {

			ch = pattern.charAt(patternIndex);

			if (ch == '*') {
				while (strIndex < strLength) {
					String subpattern = pattern.substring(patternIndex + 1);

					if (wildcardMatch(subpattern, str.substring(strIndex))) {
						return true;
					}

					strIndex++;
				}
			}
			else if (ch == '?') {
				strIndex++;

				if (strIndex > strLength) {
					return false;
				}
			}
			else {
				if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
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

	private static final Logger _LOGGER = Logger.getLogger(
		GetDirTask.class.getName());

	private String _baseDir;
	private String _classFilePath;
	private String _fileName;
	private String _rootDir;

}