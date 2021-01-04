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
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolDependencies;
import com.liferay.portal.tools.sample.sql.builder.io.CharPipe;
import com.liferay.portal.tools.sample.sql.builder.io.UnsyncTeeWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class SampleSQLBuilder {

	public static void main(String[] args) {
		ToolDependencies.wireBasic();

		try {
			new SampleSQLBuilder();
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public SampleSQLBuilder() throws Exception {
		File outputDir = new File(BenchmarksPropsValues.OUTPUT_DIR);

		outputDir.mkdirs();

		File sampleSQLFile = new File(outputDir, "sample.sql");

		compressSQL(generateSQL(sampleSQLFile), outputDir);

		sampleSQLFile.delete();

		File createSQLFile = new File(BenchmarksPropsValues.OUTPUT_FILE);

		try (BufferedWriter createSQLFileBufferWriter = new BufferedWriter(
				new FileWriter(createSQLFile.getAbsoluteFile()))) {

			_mergeCreateSQLStatements(createSQLFileBufferWriter);
		}
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

	private List<String> _getModuleServiceJarNames() throws Exception {
		List<String> moduleServiceJarNames = new ArrayList<>();

		for (String bndFile : BenchmarksPropsValues.MODULE_BND_FILES) {
			String bndContent = FileUtil.read(bndFile);

			int index1 = bndContent.indexOf("Bundle-SymbolicName: ");
			int index2 = bndContent.indexOf("Bundle-Version:");

			String bundleSymbolicName = bndContent.substring(
				index1 + 21, index2 - 1);

			String bundleVersion = bndContent.substring(
				index2 + 16, index2 + 21);

			moduleServiceJarNames.add(
				StringBundler.concat(
					bundleSymbolicName, "-", bundleVersion, ".jar"));
		}

		return moduleServiceJarNames;
	}

	private void _mergeCreateSQLStatements(Writer writer) throws Exception {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		StringBundler sb1 = new StringBundler();

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(
					classLoader.getResourceAsStream(
						_CORE_DEPENDENCIES_DIR + _CORE_SQL_FILE_NAME +
							".sql")))) {

			String line;

			while ((line = reader.readLine()) != null) {
				sb1.append(line);
				sb1.append(System.lineSeparator());
			}
		}

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(
					classLoader.getResourceAsStream(
						_CORE_DEPENDENCIES_DIR + _CORE_COMMON_SQL_FILE_NAME +
							".sql")))) {

			String line;

			while ((line = reader.readLine()) != null) {
				sb1.append(line);
				sb1.append(System.lineSeparator());
			}
		}

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(
					classLoader.getResourceAsStream(
						_CORE_DEPENDENCIES_DIR + _CORE_CUNTER_SQL_FILE_NAME +
							".sql")))) {

			String line;

			while ((line = reader.readLine()) != null) {
				sb1.append(line);
				sb1.append(System.lineSeparator());
			}
		}

		_translateCreateSQLFile(writer, sb1.toString());

		StringBundler sb2 = new StringBundler();

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(
					classLoader.getResourceAsStream(
						_CORE_DEPENDENCIES_DIR + _INDEX_SQL_FILE_NAME +
							".sql")))) {

			String line;

			while ((line = reader.readLine()) != null) {
				sb2.append(line);
				sb2.append(System.lineSeparator());
			}
		}

		_translateCreateSQLFile(writer, sb1.toString(), sb2.toString());

		List<String> moduleServiceJarNames = _getModuleServiceJarNames();

		for (String jarName : moduleServiceJarNames) {
			JarFile jarFile = new JarFile(
				BenchmarksPropsValues.SERVICE_JARS_DIR + jarName);

			JarEntry tableEntry = jarFile.getJarEntry(
				StringBundler.concat(
					_MODULE_SQL_PATH, _MODULE_SQL_FILE_NAME, ".sql"));
			JarEntry indexEntry = jarFile.getJarEntry(
				StringBundler.concat(
					_MODULE_SQL_PATH, _INDEX_SQL_FILE_NAME, ".sql"));

			StringBundler sb3 = new StringBundler();

			if (tableEntry != null) {
				BufferedReader reader = new BufferedReader(
					new InputStreamReader(jarFile.getInputStream(tableEntry)));
				String line;

				while ((line = reader.readLine()) != null) {
					sb3.append(line);
					sb3.append(System.lineSeparator());
				}

				reader.close();
			}

			_translateCreateSQLFile(writer, sb3.toString());

			StringBundler sb4 = new StringBundler();

			if (indexEntry != null) {
				BufferedReader reader = new BufferedReader(
					new InputStreamReader(jarFile.getInputStream(indexEntry)));
				String line;

				while ((line = reader.readLine()) != null) {
					sb4.append(line);
					sb4.append(System.lineSeparator());
				}

				reader.close();
			}

			_translateCreateSQLFile(writer, sb3.toString(), sb4.toString());

			jarFile.close();
		}
	}

	private String _removeBooleanIndexes(String portalData, String indexData)
		throws Exception {

		if (Validator.isNull(portalData)) {
			return StringPool.BLANK;
		}

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(indexData))) {

			StringBundler sb = new StringBundler();

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				boolean append = true;

				int x = line.indexOf(" on ");

				if (x != -1) {
					int y = line.indexOf(" (", x);

					String table = line.substring(x + 4, y);

					x = y + 2;

					y = line.indexOf(")", x);

					String[] columns = StringUtil.split(line.substring(x, y));

					x = portalData.indexOf("create table " + table + " (");

					y = portalData.indexOf(");", x);

					String portalTableData = portalData.substring(x, y);

					for (String column : columns) {
						if (portalTableData.contains(
								column.trim() + " BOOLEAN")) {

							append = false;

							break;
						}
					}
				}

				if (append) {
					sb.append(line);
					sb.append("\n");
				}
			}

			return sb.toString();
		}
	}

	private void _translateCreateSQLFile(Writer writer, String... templates)
		throws Exception {

		String template = "";

		if (templates.length == 1) {
			StringBundler sb = new StringBundler();

			try (UnsyncBufferedReader unsyncBufferedReader =
					new UnsyncBufferedReader(
						new UnsyncStringReader(templates[0]))) {

				String line = null;

				while ((line = unsyncBufferedReader.readLine()) != null) {
					sb.append(line);
					sb.append("\n");
				}
			}

			template = sb.toString();
		}
		else if (templates.length == 2) {
			if (BenchmarksPropsValues.DB_TYPE == DBType.SYBASE) {
				template = _removeBooleanIndexes(templates[0], templates[1]);
			}
			else {
				template = templates[1];
			}
		}

		if (Validator.isNull(template)) {
			return;
		}

		DB db = DBManagerUtil.getDB(BenchmarksPropsValues.DB_TYPE, null);

		template = db.buildSQL(template);

		writer.write(template);
	}

	private static final String _CORE_COMMON_SQL_FILE_NAME =
		"portal-data-common";

	private static final String _CORE_CUNTER_SQL_FILE_NAME =
		"portal-data-counter";

	private static final String _CORE_DEPENDENCIES_DIR =
		"com/liferay/portal/tools/sql/dependencies/";

	private static final String _CORE_SQL_FILE_NAME = "portal-tables";

	private static final String _INDEX_SQL_FILE_NAME = "indexes";

	private static final String _MODULE_SQL_FILE_NAME = "tables";

	private static final String _MODULE_SQL_PATH = "META-INF/sql/";

	private static final int _PIPE_BUFFER_SIZE = 16 * 1024 * 1024;

	private static final int _WRITER_BUFFER_SIZE = 16 * 1024;

	private volatile Throwable _freeMarkerThrowable;

}