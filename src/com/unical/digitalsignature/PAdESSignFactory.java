package com.unical.digitalsignature;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

import com.google.common.io.Files;

import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.signature.AbstractSignatureService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;

public class PAdESSignFactory extends AbstractSignFactory{
	
	private boolean visibleSignature = false;

	public PAdESSignFactory(boolean useVisibleSignature) {
		visibleSignature = useVisibleSignature;
	}
	
	public PAdESSignFactory() {
	}

	public AbstractSignatureParameters setParameter(DSSPrivateKeyEntry signer) {
		PAdESSignatureParameters parameters = new PAdESSignatureParameters();
		// We choose the level of the signature (-B, -T, -LT, -LTA).
		parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
		// We set the digest algorithm to use with the signature algorithm.
		// You must use the same parameter when you invoke the method sign on the token.
		// The default value is SHA256
		parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
		// We set the signing certificate
		parameters.setSigningCertificate(signer.getCertificate());
		if(visibleSignature)
			parameters.setSignatureImageParameters(addImageParameters());
		
		return parameters;
	}
	
	private SignatureImageParameters addImageParameters() {
		// Initialize visual signature
		SignatureImageParameters imageParameters = new SignatureImageParameters();
		// the origin is the left and top corner of the page
		imageParameters.setxAxis(0);
		imageParameters.setyAxis(0);
		// Initialize text to generate for visual signature
		SignatureImageTextParameters textParameters = new SignatureImageTextParameters();
		textParameters.setFont(new Font("serif", Font.PLAIN, 14));
		textParameters.setTextColor(Color.BLUE);
		textParameters.setText("My visual signature");
		imageParameters.setTextParameters(textParameters);	
		
		return imageParameters;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AbstractSignatureService createService() {
		// Create common certificate verifier
		CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
		// Create PAdESService for signature
		PAdESService service = new PAdESService(commonCertificateVerifier);
		return service;
	}
	
	@Override
	public void createSignedFile(DSSDocument signedDocument, File inputFile) {
		String newfilename = Files.getNameWithoutExtension(inputFile.getName()) + "-signed.pdf";
		int c = 1;
		while (new File(newfilename).exists()) {
			newfilename = Files.getNameWithoutExtension(inputFile.getName()) + "-signed(" + c + ").pdf";
			c++;
		}

		String dir = inputFile.getParent();
		if (dir == null)
			dir = ".";

		writeFile(dir,newfilename,signedDocument);
	}


	

}
