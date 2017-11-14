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
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileSet;

/**
 * @author Lily Chi
 */
public class GetFileSetTask extends Task {

	@Override
	public void execute() throws BuildException {
		File baseDir = new File(_rootDir);
		String[] classNames = _classNames.split(",");
		List<String> names = new ArrayList<>();

		for (String name : classNames) {
			names.add(name);
		}

		List classResultList = new ArrayList();
		List srcResultList = new ArrayList();

		findFiles(baseDir, names, classResultList, srcResultList);

		if (srcResultList.isEmpty()) {
			for (String name : names) {
				_LOGGER.log(Level.WARNING, "{0}.java did not be found!", name);
			}

			return;
		}

		HashSet<String> srcFileNames = new HashSet<>();

		DirSet srcDirSet = new DirSet();

		srcDirSet.setProject(getProject());
		srcDirSet.setDir(baseDir);

		for (int i = 0; i < srcResultList.size(); i++) {
			String srcResult = String.valueOf(srcResultList.get(i));

			srcDirSet.setIncludes(srcResult);

			int startIndex = srcResult.lastIndexOf(File.separator);
			int endIndex = srcResult.lastIndexOf(".");

			srcFileNames.add(srcResult.substring(startIndex + 1, endIndex));
		}

		if (srcFileNames.size() < names.size()) {
			names.removeAll(srcFileNames);

			for (int i = 0; i < names.size(); i++) {
				_LOGGER.log(
					Level.WARNING, "{0}.java did not be found!", names.get(i));
			}
		}

		getProject().addReference("srcSet", srcDirSet);

		FileSet classFileSet = new FileSet();

		classFileSet.setProject(getProject());

		classFileSet.setDir(baseDir);

		for (int i = 0; i < classResultList.size(); i++) {
			String filePath = String.valueOf(classResultList.get(i));

			filePath = filePath.substring(_rootDir.length() + 1);

			classFileSet.setIncludes(filePath);
		}

		getProject().addReference("classSet", classFileSet);
	}

	public void findFiles(
		File baseDir, List<String> targetNames, List<File> classFileList,
		List<File> srcFileList) {

		if (!baseDir.exists() || !baseDir.isDirectory()) {
			return;
		}

		try {
			Files.walkFileTree(
				baseDir.toPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(
							Path file, BasicFileAttributes attrs)
						throws IOException {

						Path absoluteNamePath = file.toAbsolutePath();

						String fileName = absoluteNamePath.toString();

						if (_ifSkipDirectory(fileName)) {
							return FileVisitResult.SKIP_SUBTREE;
						}
						else {
							return FileVisitResult.CONTINUE;
						}
					}

					@Override
					public FileVisitResult visitFile(
							Path file, BasicFileAttributes attrs)
						throws IOException {

						Path fileNamePath = file.getFileName();

						String fileName = fileNamePath.toString();

						for (String targetFileName : targetNames) {
							String targetClassName = targetFileName + ".class";
							String targetSrcName = targetFileName + ".java";

							if (targetClassName.equals(fileName)) {
								classFileList.add(file.toFile());
							}
							else if (targetSrcName.equals(fileName)) {
								srcFileList.add(file.toFile());
							}
						}

						return FileVisitResult.CONTINUE;
					}

				});
		}
		catch (IOException ioe) {
			throw new BuildException(ioe);
		}
	}

	public void setclassNames(String classNames) {
		_classNames = classNames;
	}

	public void setRootDir(String rootDir) {
		_rootDir = rootDir;
	}

	private boolean _ifSkipDirectory(String fileName) {
		if (_SKIP_LIST.contains(fileName) || fileName.contains(".")) {
			return true;
		}
		else {
			return false;
		}
	}

	private static final Logger _LOGGER = Logger.getLogger(
		GetFileSetTask.class.getName());

	private static final List _SKIP_LIST = Arrays.asList(
		"node_modules", "benchmarks", "definitions", "gradle", "lib",
		"nbproject", "oss-licenses", "portal-client", "readme", "sql", "tools");

	private String _classNames;
	private String _rootDir;

}