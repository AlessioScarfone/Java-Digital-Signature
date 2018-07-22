package com.unical.digitalsignature;

import java.io.File;

import com.google.common.io.Files;

import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.SignaturePackaging;
import eu.europa.esig.dss.cades.CAdESSignatureParameters;
import eu.europa.esig.dss.cades.signature.CAdESService;
import eu.europa.esig.dss.signature.AbstractSignatureService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;

public class SignFactoryCAdES extends AbstractSignFactory {

	public AbstractSignatureParameters setParameter(DSSPrivateKeyEntry signer) {
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

	@Override
	public void createSignedFile(DSSDocument signedDocument, File inputFile) {
		String newfilename = inputFile.getName() + ".p7m";
		int c = 1;
		while (new File(newfilename).exists()) {
			newfilename = Files.getNameWithoutExtension(inputFile.getName()) + "(" + c + ")."
					+ Files.getFileExtension(inputFile.getName()) + ".p7m";
			c++;
		}

		String dir = inputFile.getParent();
		if (dir == null)
			dir = ".";

		writeFile(dir,newfilename,signedDocument);
	}

}
