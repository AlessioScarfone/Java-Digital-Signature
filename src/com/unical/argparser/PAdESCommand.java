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

import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters.VisualSignatureAlignmentHorizontal;
import eu.europa.esig.dss.pades.SignatureImageParameters.VisualSignatureAlignmentVertical;

@Parameters(commandDescription = "PAdES signature format")
public class PAdESCommand extends CommonParam {

	@Parameter(description = "FileToSign", converter = FileConverter.class, arity = 1, order = 5)
	private File fileToSign;

	@Parameter(names = { "-v",
			"--visible-signature" }, description = "add visible signature - only text ", arity = 0, order = 6)
	private boolean useTextVisibleSignature;

	@Parameter(names = { "-vi",
			"--visible-signature-image" }, description = "add visible signature - text and image ", arity = 1, order = 7)
	private File useImageVisibleSignature;
	
	@Parameter(names = { "-s",
	"--skip-field-selection" }, description = "skip the selection of the field to use", arity = 0, order =8)
	private boolean skipFieldToUseSelection;
	
	@Parameter(names = { "-f",
	"--field-to-sign" }, description = "name of the field to sign ", arity = 1, order = 9)
	private String fieldName;
	
	@Parameter(names = { "-pg",
	"--page" }, description = "page of signature  [If a field is selected this option is ignored]", arity = 1, order = 10)
	private int page = 1;
	
	@Parameter(names = { "-pv",
			"--vertical-signature-position" }, description = "vertical position of visible signature: T(op) - M(iddle) - B(ottom) [If a field is selected this option is ignored]", arity = 1, order = 11)
	private String posV;

	@Parameter(names = { "-ph",
			"--horizontal-signature-position" }, description = "horizontal position of visible signature: L(eft) - C(enter) - R(ight) [If a field is selected this option is ignored]", arity = 1, order = 12)
	private String posH;
	
	

	public File getFileToSign() {
		return fileToSign;
	}

	public boolean useTextVisibleSignature() {
		return useTextVisibleSignature;
	}

	public File getVisibleSignatureImage() {
		return useImageVisibleSignature;
	}

	public VisualSignatureAlignmentVertical getPosVertical() {
		if (posV != null) {
			if (posV.equalsIgnoreCase("Top") || posV.equalsIgnoreCase("T"))
				return SignatureImageParameters.VisualSignatureAlignmentVertical.TOP;
			else if (posV.equalsIgnoreCase("Middle") || posV.equalsIgnoreCase("M"))
				return SignatureImageParameters.VisualSignatureAlignmentVertical.MIDDLE;
			else if (posV.equalsIgnoreCase("Bottom") || posV.equalsIgnoreCase("B"))
				return SignatureImageParameters.VisualSignatureAlignmentVertical.BOTTON;
		}
		return null;

	}

	public VisualSignatureAlignmentHorizontal getPosHorizontal() {
		if (posH != null) {
			if (posH.equalsIgnoreCase("Left") || posH.equalsIgnoreCase("L"))
				return SignatureImageParameters.VisualSignatureAlignmentHorizontal.LEFT;
			else if (posH.equalsIgnoreCase("Right") || posH.equalsIgnoreCase("R"))
				return SignatureImageParameters.VisualSignatureAlignmentHorizontal.RIGHT;
			else if (posH.equalsIgnoreCase("Center") || posH.equalsIgnoreCase("C"))
				return SignatureImageParameters.VisualSignatureAlignmentHorizontal.CENTER;
		}
		return null;
	}

	public int getPage() {
		return page;
	}

	public String getNameFieldToSign() {
		return fieldName;
	}

	public boolean getSkipFieldToUseSelection() {
		return skipFieldToUseSelection;
	}

}