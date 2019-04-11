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

package com.unical.argparser;

import com.beust.jcommander.Parameter;

/*
 * Class that contain the common parameters of all commands
 */
public abstract class CommonParam {
	@Parameter(names = { "-h", "--help" }, description = "display help", help = true, order = 0)
	private boolean help = false;
	
	@Parameter(names = { "-c", "--choose-certificate" }, description = "choose certificate to use", help = true, order = 1)
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
