package com.unical.argparser;

import com.beust.jcommander.Parameter;

/*
 * Class that contain the common parameters of all commands
 */
public abstract class CommonParam {
	@Parameter(names = { "-h", "--help" }, description = "show usage", help = true, order = 0)
	private boolean help = false;
	
	@Parameter(names = { "-c", "--choose-certificate" }, description = "choose certificate tu use", help = true, order = 1)
	private boolean choice_certificate = false;
	
	@Parameter(names = { "-o", "--output-folder" }, description = "set destination FOLDER for the output file", help = true, order = 2)
	private String outputFolder;
	
	@Parameter(names = { "-n", "--newfile-name" }, description = "set name of the new file", help = true, order = 3)
	private String nameNewFile;
	
	
	public boolean isHelp() {
		return help;
	}

	public boolean isChoice_certificate() {
		return choice_certificate;
	}

	public String getOutputDirectory() {
		return outputFolder;
	}

	public String getNameNewFile() {
		return nameNewFile;
	}
	
}
