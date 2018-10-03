/*Copyright 2018 Alessio Scarfone
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.unical.argparser;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.unical.argparser.ArgsParser.FileConverter;

import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters.VisualSignatureAlignmentHorizontal;
import eu.europa.esig.dss.pades.SignatureImageParameters.VisualSignatureAlignmentVertical;

@Parameters(commandDescription = "PAdES sign format")
public class PAdESCommand extends CommonParam {

	@Parameter(description = "FileToSign", converter = FileConverter.class, arity = 1, order = 5)
	private File fileToSign;

	@Parameter(names = { "-v",
			"--visible-signature" }, description = "add visible signature - only text ", arity = 0, order = 6)
	private boolean useTextVisibleSignature;

	@Parameter(names = { "-vi",
			"--visible-signature-image" }, description = "add visible signature - text and image ", arity = 1, order = 7)
	private File useImageVisibleSignature;
	
	@Parameter(names = { "-f",
	"--field-to-sign" }, description = "name of the field to sign ", arity = 1, order = 8)
	private String fieldName;
	
	@Parameter(names = { "-pg",
	"--page" }, description = "page of signature  [If a field is selected this option is ignored]", arity = 1, order = 9)
	private int page = 1;
	
	@Parameter(names = { "-pv",
			"--vertical-signature-position" }, description = "vertical position of visible signature: T(op) - M(iddle) - B(ottom) [If a field is selected this option is ignored]", arity = 1, order = 10)
	private String posV;

	@Parameter(names = { "-ph",
			"--horizontal-signature-position" }, description = "horizontal position of visible signature: L(eft) - C(enter) - R(ight) [If a field is selected this option is ignored]", arity = 1, order = 11)
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
	
	
	
	

}