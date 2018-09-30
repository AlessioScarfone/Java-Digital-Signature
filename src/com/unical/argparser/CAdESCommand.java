package com.unical.argparser;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.unical.argparser.ArgsParser.FileConverter;

@Parameters(commandDescription = "CAdES sign format")
public class CAdESCommand extends CommonParam {
	@Parameter(description = "FileToSign", converter = FileConverter.class)
	private File fileToSign;

	public File getFileToSign() {
		return fileToSign;
	}
}
