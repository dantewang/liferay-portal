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

package com.liferay.portal.service;

import com.liferay.petra.string.StringBundler;

import java.io.IOException;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Preston Crary
 */
public class ServiceXMLTest {

	@Test
	public void testServiceXML() throws Exception {
		Stream<Path> stream = Files.find(
			Paths.get(System.getProperty("user.dir")), Integer.MAX_VALUE,
			ServiceXMLTest::_isServiceXml, FileVisitOption.FOLLOW_LINKS);

		stream.forEach(
			path -> {
				_assertNameCharacters(path);
				_assertNoTXRequiredElement(path);
			});
	}

	private static boolean _isServiceXml(
		Path path, BasicFileAttributes basicFileAttributes) {

		Path fileNamePath = path.getFileName();

		if (Objects.equals(fileNamePath.toString(), "service.xml")) {
			return true;
		}

		return false;
	}

	private void _assertNameCharacters(Path path) {
		try {
			List<String> lines = Files.readAllLines(path);

			for (String line : lines) {
				for (String nameToken : _NAME_TOKENS) {
					int beginIndex = line.indexOf(nameToken);

					if (beginIndex < 0) {
						continue;
					}

					beginIndex = beginIndex + nameToken.length();

					int endIndex = line.indexOf("\"", beginIndex);

					String name = line.substring(beginIndex, endIndex);

					Assert.assertTrue(
						StringBundler.concat(
							"Invalid character found in ", nameToken, name,
							"\" in ", path.toString()),
						_isValidName(name));
				}
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _assertNoTXRequiredElement(Path path) {
		try {
			Stream<String> stream = Files.lines(path);

			Assert.assertFalse(
				"Remove deprecated tx-required element from " + path,
				stream.anyMatch(line -> line.contains("<tx-required>")));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private boolean _isValidName(String name) {
		for (char c : name.toCharArray()) {
			if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) ||
				(c == '_') || ((c >= '0') && (c <= '9'))) {

				continue;
			}

			return false;
		}

		return true;
	}

	private static final String[] _NAME_TOKENS = {
		" name=\"", " variable-name=\"", " plural-name=\"",
		" plural-variable-name=\"", " table=\"", " db-name=\""
	};

}