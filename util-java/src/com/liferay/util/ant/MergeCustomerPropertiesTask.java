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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Properties;

import org.apache.tools.ant.Task;

/**
 * @author Lily Chi
 */
public class MergeCustomerPropertiesTask extends Task {

	@Override
	public void execute() {
		File inputFile = new File(_rootDirName, _inputPropertiesFile);

		File outputFile = new File(_rootDirName, _outputPropertiesFile);

		Properties properties = new Properties();

		try (BufferedReader reader = new BufferedReader(
				new FileReader(inputFile))) {

			properties.load(reader);
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}

		try (BufferedWriter writer = new BufferedWriter(
				new FileWriter(outputFile))) {

			properties.store(writer, "");
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void setInputPropertiesFile(String inputPropertiesFile) {
		_inputPropertiesFile = inputPropertiesFile;
	}

	public void setOutputPropertiesFile(String outputPropertiesFile) {
		_outputPropertiesFile = outputPropertiesFile;
	}

	public void setRootDir(String rootDirName) {
		_rootDirName = rootDirName;
	}

	private String _inputPropertiesFile;
	private String _outputPropertiesFile;
	private String _rootDirName;

}