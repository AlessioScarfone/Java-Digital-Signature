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

package com.unical.digitalsignature;

import java.io.File;

import com.google.common.io.Files;
import com.unical.argparser.ArgsParser;

import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.SignaturePackaging;
import eu.europa.esig.dss.cades.CAdESSignatureParameters;
import eu.europa.esig.dss.cades.signature.CAdESService;
import eu.europa.esig.dss.signature.AbstractSignatureService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;

public class CAdESSignatureFactory extends AbstractSignatureFactory {

	public CAdESSignatureFactory(File inputFile) {
		super(inputFile);
	}

	public AbstractSignatureParameters createParameter(DSSPrivateKeyEntry signer) {
		CAdESSignatureParameters parameters = new CAdESSignatureParameters();
		parameters.setSignatureLevel(SignatureLevel.CAdES_BASELINE_B);

		SignaturePackaging packaging = SignaturePackaging.ENVELOPING;
		parameters.setSignaturePackaging(packaging);

		// We set the digest algorithm to use with the signature algorithm.
		// You must use the same parameter when you invoke the method sign on the token.
		// The default value is SHA256
		parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
		// We set the signing certificate
		parameters.setSigningCertificate(signer.getCertificate());

		return parameters;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AbstractSignatureService createService() {
		// Create common certificate verifier
		CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
		// Create CAdESService for signature
		CAdESService service = new CAdESService(commonCertificateVerifier);
		return service;
	}

	
	protected String getNameNewFile() {
		String newfilename = ArgsParser.getInstance().getNameNewFile();
		// use default name
		if (newfilename == null) {
			if(Files.getFileExtension(inputFile.getName()).equals("p7m") == false)
				newfilename = inputFile.getName() + ".p7m";
			else {
				newfilename = inputFile.getName();
			}
		}
		
		//add original extension if is not present
		if(Files.getFileExtension(newfilename).equals("")) {
			//file to sign is already a p7m 
			if(Files.getFileExtension(inputFile.getName()).equals("p7m")) {
				String originalExt = Files.getFileExtension(Files.getNameWithoutExtension(inputFile.getName()));
				newfilename = newfilename +"."+originalExt+".p7m";
			}
			else {
				//add original extension
				newfilename = newfilename +"."+Files.getFileExtension(inputFile.getName());
			}
		}
		
		
		// add p7m extension if is not present
		if (!Files.getFileExtension(newfilename).equals("p7m"))
			newfilename = newfilename + ".p7m";
		
		return checkIfFileExist(newfilename);
	}

}
