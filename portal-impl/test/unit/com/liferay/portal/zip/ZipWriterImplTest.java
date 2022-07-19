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

package com.liferay.portal.zip;

import com.liferay.portal.kernel.test.util.DependenciesTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.uuid.PortalUUIDImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Manuel de la Peña
 */
public class ZipWriterImplTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		PortalUUIDUtil portalUUIDUtil = new PortalUUIDUtil();

		portalUUIDUtil.setPortalUUID(new PortalUUIDImpl());

		_expectedEntryContent = StringUtil.read(
			DependenciesTestUtil.getDependencyAsInputStream(
				ZipWriterImplTest.class, _ENTRY_FILE_PATH));
	}

	@Before
	public void setUp() throws IOException {
		_tempZipFilePath = Files.createTempDirectory("A");

		_zipFile = new File(_tempZipFilePath.toFile(), "B.zip");
	}

	@After
	public void tearDown() throws IOException {
		Files.delete(_tempZipFilePath);
	}

	@Test
	public void testAddEntryFromBytes() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(_zipFile);

		zipWriter.addEntry(
			_ENTRY_FILE_PATH,
			FileUtil.getBytes(
				DependenciesTestUtil.getDependencyAsFile(
					getClass(), _ENTRY_FILE_PATH)));

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		try {
			Assert.assertTrue(file.exists());

			Assert.assertEquals(
				_expectedEntryContent,
				zipReader.getEntryAsString(_ENTRY_FILE_PATH));
		}
		finally {
			zipReader.close();

			file.delete();
		}
	}

	@Test
	public void testAddEntryFromBytesThatAreEmpty() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(_zipFile);

		zipWriter.addEntry("empty.txt", new byte[0]);

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		try {
			Assert.assertEquals("", zipReader.getEntryAsString("empty.txt"));
		}
		finally {
			zipReader.close();

			file.delete();
		}
	}

	@Test
	public void testAddEntryFromInputStream() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(_zipFile);

		zipWriter.addEntry(
			_ENTRY_FILE_PATH,
			DependenciesTestUtil.getDependencyAsInputStream(
				getClass(), _ENTRY_FILE_PATH));

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		try {
			Assert.assertEquals(
				_expectedEntryContent,
				zipReader.getEntryAsString(_ENTRY_FILE_PATH));
		}
		finally {
			zipReader.close();

			file.delete();
		}
	}

	@Test
	public void testAddEntryFromInputStreamThatIsNull() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(_zipFile);

		zipWriter.addEntry("null.txt", (InputStream)null);

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		try {
			Assert.assertNull(zipReader.getEntryAsString("null.txt"));
		}
		finally {
			zipReader.close();

			file.delete();
		}
	}

	@Test
	public void testAddEntryFromInputStreamThatStartsWithSlash()
		throws Exception {

		ZipWriter zipWriter = new ZipWriterImpl(_zipFile);

		zipWriter.addEntry(
			"/" + _ENTRY_FILE_PATH,
			DependenciesTestUtil.getDependencyAsInputStream(
				getClass(), _ENTRY_FILE_PATH));

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		try {
			Assert.assertEquals(
				_expectedEntryContent,
				zipReader.getEntryAsString(_ENTRY_FILE_PATH));
		}
		finally {
			zipReader.close();

			file.delete();
		}
	}

	@Test
	public void testAddEntryFromString() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(_zipFile);

		zipWriter.addEntry("string.txt", "This is a string.");

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		try {
			Assert.assertEquals(
				"This is a string.", zipReader.getEntryAsString("string.txt"));
		}
		finally {
			zipReader.close();

			file.delete();
		}
	}

	@Test
	public void testAddEntryFromStringBuilder() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(_zipFile);

		StringBuilder sb = new StringBuilder();

		sb.append("This is a string.");

		zipWriter.addEntry("string.txt", sb);

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		try {
			Assert.assertEquals(
				"This is a string.", zipReader.getEntryAsString("string.txt"));
		}
		finally {
			zipReader.close();

			file.delete();
		}
	}

	@Test
	public void testAddEntryFromStringBuilderThatIsEmpty() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(_zipFile);

		zipWriter.addEntry("empty.txt", new StringBuilder());

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		try {
			Assert.assertEquals("", zipReader.getEntryAsString("empty.txt"));
		}
		finally {
			zipReader.close();

			file.delete();
		}
	}

	@Test
	public void testAddEntryFromStringBuilderThatIsNull() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(_zipFile);

		zipWriter.addEntry("null.txt", (StringBuilder)null);

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		try {
			Assert.assertNull(zipReader.getEntryAsString("null.txt"));
		}
		finally {
			zipReader.close();

			file.delete();
		}
	}

	@Test
	public void testAddEntryFromStringThatIsEmpty() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(_zipFile);

		zipWriter.addEntry("empty.txt", "");

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		try {
			Assert.assertEquals("", zipReader.getEntryAsString("empty.txt"));
		}
		finally {
			zipReader.close();

			file.delete();
		}
	}

	@Test
	public void testAddEntryFromStringThatIsNull() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(_zipFile);

		zipWriter.addEntry("null.txt", (String)null);

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		try {
			Assert.assertNull(zipReader.getEntryAsString("null.txt"));
		}
		finally {
			zipReader.close();

			file.delete();
		}
	}

	@Test
	public void testConstructor() {
		ZipWriter zipWriter1 = new ZipWriterImpl();

		File file1 = zipWriter1.getFile();

		Assert.assertNotNull(file1);

		file1.delete();

		ZipWriter zipWriter2 = new ZipWriterImpl(_zipFile);

		File file2 = zipWriter2.getFile();

		try {
			Assert.assertNotNull(file2);
			Assert.assertTrue(file2.exists());
			Assert.assertEquals(_zipFile.getPath(), file2.getPath());
		}
		finally {
			file2.delete();
		}
	}

	@Test
	public void testConstructorWithSpecialCharacters() throws IOException {
		Path path = Files.createTempDirectory("A B ");

		File zipFile = new File(path.toFile(), "C D .zip");

		ZipWriter zipWriter = new ZipWriterImpl(zipFile);

		File file = zipWriter.getFile();

		try {
			Assert.assertNotNull(file);
			Assert.assertTrue(file.exists());
			Assert.assertEquals(zipFile.getPath(), file.getPath());
		}
		finally {
			file.delete();
		}
	}

	@Test
	public void testFinish() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(_zipFile);

		StringBuilder sb = new StringBuilder();

		sb.append("This is a string.");

		zipWriter.addEntry("string.txt", sb);

		byte[] bytes = zipWriter.finish();

		try {
			Assert.assertArrayEquals(FileUtil.getBytes(_zipFile), bytes);
		}
		finally {
			File file = zipWriter.getFile();

			file.delete();
		}
	}

	/**
	 * Tests that {@link ZipWriter#finish()} can execute without error on a ZIP
	 * writer that's been created by the default constructor and that has no
	 * entries.
	 *
	 * @throws Exception
	 */
	@Test
	public void testFinishIfZipFileIsNotSet() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl();

		zipWriter.finish();

		File file = zipWriter.getFile();

		file.delete();
	}

	/**
	 * Tests that {@link ZipWriter#finish()} can execute without error on a ZIP
	 * writer that's been created for an existing ZIP file and that has no
	 * entries.
	 *
	 * @throws Exception if an exception occurred
	 */
	@Test
	public void testFinishIfZipFileIsSet() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(_zipFile);

		zipWriter.finish();

		File file = zipWriter.getFile();

		file.delete();
	}

	private static final String _ENTRY_FILE_PATH = "entry.txt";

	private static String _expectedEntryContent;

	private Path _tempZipFilePath;
	private File _zipFile;

}