package com.unical.digitalsignature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.unical.utils.Utility;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.x509.CertificateToken;

public abstract class AbstractSignFactory implements ISignFactory {
	
	protected File inputFile;
	
	public AbstractSignFactory(File inputFile) {
		this.inputFile = inputFile;
	}

	@Override
	public Pkcs11SignatureToken connectToToken(File driverPath, char[] pass) {
		Pkcs11SignatureToken token = new Pkcs11SignatureToken(driverPath.getAbsolutePath(), pass);
		return token;
	}

	@Override
	public void showCertificateData(CertificateToken ct) {
		System.out.println(ct.getCertificate().toString());
	}

	@Override
	public void showKeyUsage(CertificateToken ct) {
		// the KeyUsage extension of this certificate, represented as an array of
		// booleans.
		// KeyUsage ::= BIT STRING {
		// * digitalSignature (0),
		// * nonRepudiation (1),
		// * keyEncipherment (2),
		// * dataEncipherment (3),
		// * keyAgreement (4),
		// * keyCertSign (5),
		// * cRLSign (6),
		// * encipherOnly (7),
		// * decipherOnly (8)
		// }
		boolean[] keyUsageValue = ct.getCertificate().getKeyUsage();
		String keyUsageLabel[] = { "digitalSignature", "nonRepudiation", "keyEncipherment", "dataEncipherment",
				"keyAgreement", "keyCertSign", "cRLSign", "encipherOnly", "decipherOnly" };
		for (int i = 0; i < 9; i++) {
			format(keyUsageLabel[i], keyUsageValue[i]);
		}
	}

	private void format(String s, boolean val) {
		System.out.println(" " + s + " => " + val);
	}

	// SELECT CERTIFICATE TO USE
	@Override
	public DSSPrivateKeyEntry getSigner(List<DSSPrivateKeyEntry> keys) {
		DSSPrivateKeyEntry selectedKey = null;
		if (Utils.isCollectionEmpty(keys)) {
			throw new RuntimeException("No certificate found", null);
		} else if (Utils.collectionSize(keys) == 1) {
			selectedKey = keys.get(0);
		} else {
			//TODO user can select certificate to use
			
			selectedKey = keys.get(0);
			// Map<String, DSSPrivateKeyEntry> map = new TreeMap<String,
			// DSSPrivateKeyEntry>();
			// for (DSSPrivateKeyEntry dssPrivateKeyEntry : keys) {
			// CertificateToken certificate = dssPrivateKeyEntry.getCertificate();
			// String text = DSSASN1Utils.getHumanReadableName(certificate) + " (" +
			// certificate.getSerialNumber()
			// + ")";
			// map.put(text, dssPrivateKeyEntry);
			// }
			// System.out.println("Certificates:");
			// for (String k : map.keySet()) {
			// System.out.println(k+"\n");
			// //get key usage
			// System.out.println("CERT DATA:");
			// System.out.println(map.get(k).getCertificate().getCertificate().toString());
			// }
		}
		return selectedKey;
	}

	public void writeFile(String dir, String newfilename, DSSDocument signedDocument) {
		System.out.println("\nCreate signed file: " + newfilename);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(dir + Utility.separator + newfilename);
			Utils.copy(signedDocument.openStream(), fos);
			Utils.closeQuietly(fos);
		} catch (DSSException | IOException e) {
			System.err.println("Error write file");
			// e.printStackTrace();
		}
	}

}
