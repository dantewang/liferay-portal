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

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.petra.io.OutputStreamWriter;
import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncBufferedWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.freemarker.FreeMarkerUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.tools.ToolDependencies;
import com.liferay.portal.tools.sample.sql.builder.io.CharPipe;
import com.liferay.portal.tools.sample.sql.builder.io.UnsyncTeeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class SampleSQLBuilder {

	public static void main(String[] args) {
		try {
			URLClassLoader classLoader = new URLClassLoader(_getDependencies());

			Class<?> clazz = classLoader.loadClass(
				"com.liferay.portal.tools.sample.sql.builder.SampleSQLBuilder");

			clazz.newInstance();
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public SampleSQLBuilder() throws Exception {
		ToolDependencies.wireBasic();

		File outputDir = new File(BenchmarksPropsValues.OUTPUT_DIR);

		outputDir.mkdirs();

		File sampleSQLFile = new File(outputDir, "sample.sql");

		compressSQL(generateSQL(sampleSQLFile), outputDir);

		sampleSQLFile.delete();
	}

	protected void compressSQL(
			DB db, File directory, Map<String, Writer> insertSQLWriters,
			Map<String, StringBundler> sqls, String insertSQL)
		throws IOException, SQLException {

		String tableName = insertSQL.substring(0, insertSQL.indexOf(' '));

		int index = insertSQL.indexOf(" values ") + 8;

		StringBundler sb = sqls.get(tableName);

		if ((sb == null) || (sb.index() == 0)) {
			sb = new StringBundler();

			sqls.put(tableName, sb);

			sb.append("insert into ");
			sb.append(insertSQL.substring(0, index));
			sb.append("\n");
		}
		else {
			sb.append(",\n");
		}

		String values = insertSQL.substring(index, insertSQL.length() - 1);

		sb.append(values);

		if (sb.index() >= BenchmarksPropsValues.OPTIMIZE_BUFFER_SIZE) {
			sb.append(";\n");

			insertSQL = db.buildSQL(sb.toString());

			sb.setIndex(0);

			writeToInsertSQLFile(
				directory, tableName, insertSQLWriters, insertSQL);
		}
	}

	protected void compressSQL(Reader reader, File dir) throws Exception {
		DB db = DBManagerUtil.getDB(BenchmarksPropsValues.DB_TYPE, null);

		if ((BenchmarksPropsValues.DB_TYPE == DBType.MARIADB) ||
			(BenchmarksPropsValues.DB_TYPE == DBType.MYSQL)) {

			db = new SampleMySQLDB(db.getMajorVersion(), db.getMinorVersion());
		}

		Map<String, Writer> insertSQLWriters = new HashMap<>();
		Map<String, StringBundler> insertSQLs = new HashMap<>();
		List<String> miscSQLs = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(reader)) {

			String s = null;

			while ((_freeMarkerThrowable == null) &&
				   ((s = unsyncBufferedReader.readLine()) != null)) {

				s = s.trim();

				if (s.length() > 0) {
					if (s.startsWith("insert into ")) {
						if (!s.endsWith(");")) {
							StringBundler sb = new StringBundler();

							while (!s.endsWith(");")) {
								sb.append(s);
								sb.append(StringPool.NEW_LINE);

								s = unsyncBufferedReader.readLine();
							}

							sb.append(s);

							s = sb.toString();
						}

						compressSQL(
							db, dir, insertSQLWriters, insertSQLs,
							s.substring(12));
					}
					else {
						miscSQLs.add(s);
					}
				}
			}
		}

		if (_freeMarkerThrowable != null) {
			throw new Exception(
				"Unable to process FreeMarker template ", _freeMarkerThrowable);
		}

		for (Map.Entry<String, StringBundler> entry : insertSQLs.entrySet()) {
			String tableName = entry.getKey();
			StringBundler sb = entry.getValue();

			if (sb.index() > 0) {
				String insertSQL = db.buildSQL(sb.toString());

				writeToInsertSQLFile(
					dir, tableName, insertSQLWriters, insertSQL);
			}

			try (Writer insertSQLWriter = insertSQLWriters.remove(tableName)) {
				insertSQLWriter.write(";\n");
			}
		}

		try (Writer miscSQLWriter = new FileWriter(new File(dir, "misc.sql"))) {
			for (String miscSQL : miscSQLs) {
				miscSQL = db.buildSQL(miscSQL);

				miscSQLWriter.write(miscSQL);

				miscSQLWriter.write(StringPool.NEW_LINE);
			}
		}
	}

	protected Writer createFileWriter(File file) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(file);

		Writer writer = new OutputStreamWriter(fileOutputStream);

		return new UnsyncBufferedWriter(writer, _WRITER_BUFFER_SIZE);
	}

	protected Reader generateSQL(File sampleSQLFile) {
		final CharPipe charPipe = new CharPipe(_PIPE_BUFFER_SIZE);

		Thread thread = new Thread(
			() -> {
				try (CSVFileWriter csvFileWriter = new CSVFileWriter();
					Writer sampleSQLWriter = new UnsyncTeeWriter(
						new UnsyncBufferedWriter(
							charPipe.getWriter(), _WRITER_BUFFER_SIZE),
						createFileWriter(sampleSQLFile))) {

					FreeMarkerUtil.process(
						BenchmarksPropsValues.SCRIPT,
						HashMapBuilder.<String, Object>put(
							"csvFileWriter", csvFileWriter
						).put(
							"dataFactory", new DataFactory()
						).build(),
						sampleSQLWriter);
				}
				catch (Throwable throwable) {
					_freeMarkerThrowable = throwable;
				}
				finally {
					charPipe.close();
				}
			});

		thread.start();

		return charPipe.getReader();
	}

	protected void writeToInsertSQLFile(
			File dir, String tableName, Map<String, Writer> insertSQLWriters,
			String insertSQL)
		throws IOException {

		Writer insertSQLWriter = insertSQLWriters.get(tableName);

		if (insertSQLWriter == null) {
			File file = new File(dir, tableName + ".sql");

			insertSQLWriter = createFileWriter(file);

			insertSQLWriters.put(tableName, insertSQLWriter);
		}

		insertSQLWriter.write(insertSQL);
	}

	private static void _appendClassPath(StringBuilder sb, File dir)
		throws Exception {

		if (dir.exists() && dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				String fileName = file.getName();

				if (file.isFile() && fileName.endsWith("jar")) {
					sb.append(file.getCanonicalPath());
					sb.append(File.pathSeparator);
				}
				else if (file.isDirectory()) {
					_appendClassPath(sb, file);
				}
			}
		}
	}

	private static String _getClassPath() throws Exception {
		StringBuilder sb = new StringBuilder();

		String liferayClassPath = System.getenv("LIFERAY_CLASSPATH");

		if ((liferayClassPath != null) && !liferayClassPath.isEmpty()) {
			sb.append(liferayClassPath);
			sb.append(File.pathSeparator);
		}

		_appendClassPath(sb, new File(_jarDir, "lib"));
		_appendClassPath(sb, _jarDir);
		sb.append(File.pathSeparator);

		return sb.toString();
	}

	private static URL[] _getDependencies() throws Exception {
		List<URL> urls = new ArrayList<>();

		String[] classPaths = _getClassPath().split(File.pathSeparator);

		for (String classPath : classPaths) {
			urls.add(new URL("file:" + classPath));
		}

		String sdkDistDir = _jarDir.getAbsolutePath();

		String rootDir = sdkDistDir.substring(0, sdkDistDir.indexOf("tools"));

		urls.add(
			new URL(
				StringBundler.concat(
					"file:", rootDir, "portal-impl", File.separator,
					"portal-impl.jar")));
		urls.add(
			new URL(
				StringBundler.concat(
					"file:", rootDir, "portal-kernel", File.separator,
					"portal-kernel.jar")));

		for (String name : _getLibs(rootDir + "lib", "development")) {
			urls.add(new URL("file:" + name));
		}

		for (String name : _getLibs(rootDir + "lib", "global")) {
			urls.add(new URL("file:" + name));
		}

		for (String name : _getLibs(rootDir + "lib", "portal")) {
			urls.add(new URL("file:" + name));
		}

		return urls.toArray(new URL[0]);
	}

	private static List<String> _getLibs(String libDir, String subdir)
		throws Exception {

		List<String> libs = new ArrayList<>();

		File baseDir = new File(libDir, subdir);

		Files.walkFileTree(
			baseDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String fileName = String.valueOf(path.getFileName());

					if (fileName.endsWith(".jar")) {
						libs.add(String.valueOf(path.toAbsolutePath()));
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return libs;
	}

	private static final int _PIPE_BUFFER_SIZE = 16 * 1024 * 1024;

	private static final int _WRITER_BUFFER_SIZE = 16 * 1024;

	private static File _jarDir;

	static {
		ProtectionDomain protectionDomain =
			SampleSQLBuilder.class.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		try {
			Path path = Paths.get(url.toURI());

			File jarFile = path.toFile();

			_jarDir = jarFile.getParentFile();
		}
		catch (URISyntaxException uriSyntaxException) {
			throw new ExceptionInInitializerError(uriSyntaxException);
		}
	}

	private volatile Throwable _freeMarkerThrowable;

}