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

package com.liferay.portal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;
import java.io.FileOutputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Brian Wing Shun Chan
 * @author Roberto Díaz
 */
public class FileImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());
	}

	@Test
	public void testAppendParentheticalSuffixWhenFileNameHasParenthesis() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test(1).jsp", "1");

		Assert.assertEquals("test(1) (1).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithMultipleCharacterValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "1!$eae1");

		Assert.assertEquals("test (1!$eae1).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithMultipleNumericalValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "1111111");

		Assert.assertEquals("test (1111111).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithMultipleStringValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "AAAAAAA");

		Assert.assertEquals("test (AAAAAAA).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithMultipleStringWithSpaceValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "A B");

		Assert.assertEquals("test (A B).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithSingleNumericalValue() {
		String fileName = _fileImpl.appendParentheticalSuffix("test.jsp", "1");

		Assert.assertEquals("test (1).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithSingleStringValue() {
		String fileName = _fileImpl.appendParentheticalSuffix("test.jsp", "A");

		Assert.assertEquals("test (A).jsp", fileName);
	}

	@Test
	public void testAppendSuffix() {
		Assert.assertEquals("test_rtl", _fileImpl.appendSuffix("test", "_rtl"));
		Assert.assertEquals(
			"test_rtl.css", _fileImpl.appendSuffix("test.css", "_rtl"));
		Assert.assertEquals(
			"/folder/test_rtl.css",
			_fileImpl.appendSuffix("/folder/test.css", "_rtl"));
	}

	@Test
	public void testGetPathBackSlashForwardSlash() {
		Assert.assertEquals(
			"aaa\\bbb/ccc\\ddd",
			_fileImpl.getPath("aaa\\bbb/ccc\\ddd/eee.fff"));
	}

	@Test
	public void testGetPathForwardSlashBackSlash() {
		Assert.assertEquals(
			"aaa/bbb\\ccc/ddd", _fileImpl.getPath("aaa/bbb\\ccc/ddd\\eee.fff"));
	}

	@Test
	public void testGetPathNoPath() {
		Assert.assertEquals(StringPool.SLASH, _fileImpl.getPath("aaa.bbb"));
	}

	@Test
	public void testGetShortFileNameBackSlashForwardSlash() {
		Assert.assertEquals(
			"eee.fff", _fileImpl.getShortFileName("aaa\\bbb/ccc\\ddd/eee.fff"));
	}

	@Test
	public void testGetShortFileNameForwardSlashBackSlash() {
		Assert.assertEquals(
			"eee.fff", _fileImpl.getShortFileName("aaa/bbb\\ccc/ddd\\eee.fff"));
	}

	@Test
	public void testGetShortFileNameNoPath() {
		Assert.assertEquals("aaa.bbb", _fileImpl.getShortFileName("aaa.bbb"));
	}

	@Test
	public void testStripSuffixAppendedWhenFileNameHasParenthesis() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test(1).jsp", "1");

		Assert.assertEquals(
			"test(1).jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithMultipleCharacterValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "1!$eae1");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithMultipleNumericalValue() {
		String fileName2 = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "1111111");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName2));
	}

	@Test
	public void testStripSuffixAppendedWithMultipleStringValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "AAAAAAA");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithMultipleStringWithSpaceValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "A B");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithSingleNumericalValue() {
		String fileName = _fileImpl.appendParentheticalSuffix("test.jsp", "1");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithSingleStringValue() {
		String fileName = _fileImpl.appendParentheticalSuffix("test.jsp", "A");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixWhenFileNameHasInvertedParenthesis() {
		Assert.assertEquals(
			"test)1(.jsp", _fileImpl.stripParentheticalSuffix("test)1(.jsp"));
	}

	@Test
	public void testStripSuffixWhenFileNameHasNoCloseParenthesis() {
		Assert.assertEquals(
			"test(1.jsp", _fileImpl.stripParentheticalSuffix("test(1.jsp"));
	}

	@Test
	public void testStripSuffixWhenFileNameHasNoExtension() {
		Assert.assertEquals(
			"test", _fileImpl.stripParentheticalSuffix("test (1)"));
	}

	@Test
	public void testStripSuffixWhenFileNameHasNoParentheticalSuffix() {
		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix("test.jsp"));
	}

	@Test
	public void testStripSuffixWhenFileNameHasParenthesisAtStart() {
		Assert.assertEquals(
			"()test.jsp", _fileImpl.stripParentheticalSuffix("()test.jsp"));
	}

	@Test
	public void testUnzip() throws Exception {
		File file = _createZipFile(
			"test.zip",
			HashMapBuilder.put(
				"zip/test/entry/entry.txt", "Test String"
			).build());

		Path destinationPath = Files.createTempDirectory(null);

		try {
			_fileImpl.unzip(file, destinationPath.toFile());

			Assert.assertTrue(
				Files.exists(
					destinationPath.resolve("zip/test/entry/entry.txt")));
		}
		finally {
			_fileImpl.deltree(destinationPath.toFile());
			_fileImpl.deltree(file.getParentFile());
		}
	}

	@Test
	public void testUnzipZipSlipVulnerable() throws Exception {
		File file = _createZipFile(
			"test_slip.zip",
			HashMapBuilder.put(
				"../bad.txt", "I am bad!"
			).put(
				"good.txt", "I am good!"
			).build());

		Path parentPath = Files.createTempDirectory(null);

		Path destinationPath = Files.createTempDirectory(parentPath, null);

		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				FileImpl.class.getName(), Level.WARNING)) {

			_fileImpl.unzip(file, destinationPath.toFile());

			Assert.assertTrue(
				Files.exists(destinationPath.resolve("good.txt")));
			Assert.assertFalse(Files.exists(parentPath.resolve("bad.txt")));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Entry is outside of the target dir: ../bad.txt",
				logEntry.getMessage());
		}
		finally {
			_fileImpl.deltree(parentPath.toFile());
			_fileImpl.deltree(file.getParentFile());
		}
	}

	private File _createZipFile(String fileName, Map<String, String> entries)
		throws Exception {

		File tempFolderForCreatingZip = _fileImpl.createTempFolder();

		File zipFile = new File(
			tempFolderForCreatingZip.getCanonicalPath(), fileName);

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				new FileOutputStream(zipFile))) {

			for (Map.Entry<String, String> entry : entries.entrySet()) {
				ZipEntry zipEntry = new ZipEntry(entry.getKey());

				zipOutputStream.putNextEntry(zipEntry);

				String content = entry.getValue();

				if (content != null) {
					byte[] data = content.getBytes();

					zipOutputStream.write(data, 0, data.length);
				}

				zipOutputStream.closeEntry();
			}
		}

		return zipFile;
	}

	private final FileImpl _fileImpl = new FileImpl();

}