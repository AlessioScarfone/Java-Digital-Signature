package com.unical.digitalsignature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.io.Files;

import eu.europa.esig.dss.DSSASN1Utils;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.x509.CertificateToken;

public class SignService {

	public static Pkcs11SignatureToken connectToToken(File driverPath, String pass) {

		Pkcs11SignatureToken token = new Pkcs11SignatureToken(driverPath.getAbsolutePath(), pass.toCharArray());
		return token;
	}

	public void printTokenData(List<DSSPrivateKeyEntry> keys) {
		for (DSSPrivateKeyEntry entry : keys) {
			System.out.println(entry.getCertificate().getCertificate());
		}
	}

	public static PAdESSignatureParameters setPAdESSParameter(DSSPrivateKeyEntry signer) {
		PAdESSignatureParameters parameters = new PAdESSignatureParameters();
		// We choose the level of the signature (-B, -T, -LT, -LTA).

		// TODO: discover what is used from ArubaSign
		parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
		// We set the digest algorithm to use with the signature algorithm.
		// You must use the same parameter when you invoke the method sign on the token.
		// The default value is SHA256
		parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
		// We set the signing certificate
		parameters.setSigningCertificate(signer.getCertificate());
		return parameters;
	}

	public static PAdESService createPAdESService() {
		// Create common certificate verifier
		CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
		// Create PAdESService for signature
		PAdESService service = new PAdESService(commonCertificateVerifier);
		return service;
	}

	public static void createSignedPDF(DSSDocument signedDocument, File inputFile) {
		String separator = System.getProperty("file.separator");
		String newfilename = Files.getNameWithoutExtension(inputFile.getName())+"-signed.pdf";
		String dir = inputFile.getParent();
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(dir+separator+newfilename);
			Utils.copy(signedDocument.openStream(), fos);
			Utils.closeQuietly(fos);
		} catch (DSSException | IOException e) {
			e.printStackTrace();
		}
	}

	// SELECT CERTIFICATE TO USE
	public static DSSPrivateKeyEntry getSigner(List<DSSPrivateKeyEntry> keys) {
		DSSPrivateKeyEntry selectedKey = null;
		if (Utils.isCollectionEmpty(keys)) {
			throw new RuntimeException("No certificate found", null);
		} else if (Utils.collectionSize(keys) == 1) {
			selectedKey = keys.get(0);
		} else {
			selectedKey = keys.get(0);
			Map<String, DSSPrivateKeyEntry> map = new HashMap<String, DSSPrivateKeyEntry>();
			for (DSSPrivateKeyEntry dssPrivateKeyEntry : keys) {
				CertificateToken certificate = dssPrivateKeyEntry.getCertificate();
				String text = DSSASN1Utils.getHumanReadableName(certificate) + " (" + certificate.getSerialNumber()
						+ ")";
				map.put(text, dssPrivateKeyEntry);
			}
			for (String k : map.keySet()) {
				System.out.println(k + "\n\t\t" + map.get(k).toString() + "\n");
			}
		}
		return selectedKey;
	}

}
