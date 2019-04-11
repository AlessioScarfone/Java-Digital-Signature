/*  
 *  Java-Digital-Signature
 *  Java command line tool for digital signature with PKCS#11 token.
 *  Copyright (C) 2018,2019  Alessio Scarfone
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	 This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	 GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

	public static char[] readPasswordFromConsole() {
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

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	/** get a integer in [ minInt , maxInt [ **/
	public static int getValidIntInRange(String text, int minInt, int maxInt) {
		System.out.print(text);
		int n = -1;
		String readLine = System.console().readLine();
		if(readLine == null)
			System.exit(0);
		
		if (readLine.isEmpty())
			n = -1;
		else if (Utility.isInteger(readLine))
			n = Integer.parseInt(readLine);
		while (n >= maxInt || n < minInt) { // if n is out of bound, read again
			System.out.println("Input Not Valid");
			System.out.print(text);
			readLine = System.console().readLine();
			if (readLine.isEmpty())
				n = -1;
			else if (Utility.isInteger(readLine))
				n = Integer.parseInt(readLine);

		}
		return n;
	}

}
