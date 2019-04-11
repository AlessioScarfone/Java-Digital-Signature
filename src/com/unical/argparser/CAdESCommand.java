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

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.unical.argparser.ArgsParser.FileConverter;

@Parameters(commandDescription = "CAdES signature format")
public class CAdESCommand extends CommonParam {
	@Parameter(description = "FileToSign", converter = FileConverter.class)
	private File fileToSign;

	public File getFileToSign() {
		return fileToSign;
	}
}
