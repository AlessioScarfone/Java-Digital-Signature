package com.unical.digitalsignature;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

import com.google.common.io.Files;
import com.unical.utils.PAdESProp;

import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.DSSASN1Utils;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.FileDocument;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters.VisualSignatureRotation;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.signature.AbstractSignatureService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.x509.CertificateToken;

public class PAdESSignFactory extends AbstractSignFactory {

	private PAdESProp prop;

	public PAdESSignFactory() {
	}

	public PAdESSignFactory(PAdESProp padesProp) {
		prop = padesProp;
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
		if (prop.getUseVisibleSign()) {
			CertificateToken ct = signer.getCertificate();
			String humanReadableSigner = DSSASN1Utils.getHumanReadableName(ct);
			parameters.setSignatureImageParameters(addImageParameters(humanReadableSigner, prop));
		}

		return parameters;
	}

	private SignatureImageParameters addImageParameters(String humanReadableSigner, PAdESProp prop) {
		// Initialize visual signature
		SignatureImageParameters imageParameters = new SignatureImageParameters();
		// the origin is the left and top corner of the page

		imageParameters.setPage(1);
		imageParameters.setRotation(VisualSignatureRotation.AUTOMATIC);
		System.out.println(prop.toString());
		imageParameters.setAlignmentHorizontal(prop.getPosHorizontal());
		imageParameters.setAlignmentVertical(prop.getPosVertical());
		if (prop.getImageFile() != null) {
			if (prop.getImageFile().exists()) {
				// TODO: check if the file is a png,jpg ecc
				imageParameters.setImage(new FileDocument(prop.getImageFile()));
			} else {
				System.err.println("Image not exist. Sign only with text.\n");
			}
		}
		// imageParameters.setxAxis(0);
		// imageParameters.setyAxis(0);

		// Initialize text to generate for visual signature
		SignatureImageTextParameters textParameters = new SignatureImageTextParameters();
		textParameters.setFont(new Font("serif", Font.PLAIN, 13));
		textParameters.setTextColor(Color.BLACK);
		textParameters.setText(humanReadableSigner);
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

		writeFile(dir, newfilename, signedDocument);
	}

}
