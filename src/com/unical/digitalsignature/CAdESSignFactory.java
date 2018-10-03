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

public class CAdESSignFactory extends AbstractSignFactory {

	public CAdESSignFactory(File inputFile) {
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
		if (newfilename == null)
			newfilename = inputFile.getName() + ".p7m";
		
		//add original extension if is not present
		if(Files.getFileExtension(newfilename).equals(""))
			newfilename = newfilename +"."+Files.getFileExtension(inputFile.getName());
		
		// add p7m extension if is not present
		if (!Files.getFileExtension(newfilename).equals("p7m"))
			newfilename = newfilename + ".p7m";
		
		return checkIfFileExist(newfilename);
	}

}
