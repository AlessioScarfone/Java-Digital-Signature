package com.unical.utils;

import java.io.Console;

public class Utility {
	
	public static String separator = System.getProperty("file.separator");
	
	public static String buildFilePath(String... strings) {
		String path = "";
		for (int i = 0; i < strings.length; i++) {
			path = path + strings[i];
			if (i != strings.length - 1)
				path = path + separator;
		}
		return path;
	}
	
	public static String checkOSArchitecture() {
		String arch = System.getenv("PROCESSOR_ARCHITECTURE");
		String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
		String realArch = null;
		if (arch != null && arch.endsWith("64") || wow64Arch != null && wow64Arch.endsWith("64"))
			realArch = "64";
		else
			realArch = "32";
		return realArch;
	}
	
	public  static char[] getPassword() {
		Console cnsl = null;

		try {
			// creates a console object
			cnsl = System.console();
			// if console is not null
			if (cnsl != null) {
				char[] pwd = cnsl.readPassword("Password: ");
				return pwd;
			}
		} catch (Exception ex) {

			// if any error occurs
			ex.printStackTrace();
		}
		return null;
	}
	
}
