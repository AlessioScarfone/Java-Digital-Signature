package com.unical.digitalsignature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.unical.utils.Utility;

import eu.europa.esig.dss.DSSASN1Utils;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.x509.CertificateToken;

public abstract class AbstractSignFactory implements ISignFactory {

	protected File inputFile;
	private String keyUsageLabel[] = { "digitalSignature", "nonRepudiation", "keyEncipherment", "dataEncipherment",
			"keyAgreement", "keyCertSign", "cRLSign", "encipherOnly", "decipherOnly" };

	private final int nonRepudiationIndex = 1;

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
		for (int i = 0; i < 9; i++) {
			format(keyUsageLabel[i], keyUsageValue[i]);
		}
	}

	// SELECT CERTIFICATE TO USE
	// only certificate with NonRepudiation can be used
	@Override
	public DSSPrivateKeyEntry getSigner(List<DSSPrivateKeyEntry> keys, boolean choose_cert) {
		DSSPrivateKeyEntry selectedKey = null;
		if (Utils.isCollectionEmpty(keys)) {
			throw new RuntimeException("No certificate found", null);
		} else {
			// TODO allow the user to choose the certificate to use
			List<DSSPrivateKeyEntry> usable_keys = new ArrayList<DSSPrivateKeyEntry>();
			if (choose_cert) {
				int c = 0;
				for (DSSPrivateKeyEntry key : keys) {
					if (haveNonRepudiation(key)) {
						usable_keys.add(key);
						printCertificateSelectionField(key, c);
						c++;
					}
				}
				if (!usable_keys.isEmpty()) {
					int n = Utility.getValidIntInRange("Select a certificate to use:",0,usable_keys.size());
					selectedKey = usable_keys.get(n);
				}
			} else {
				// use first key with "NonRepudiation"
				for (DSSPrivateKeyEntry key : keys) {
					if (haveNonRepudiation(key)) {
						selectedKey = key;
						break;
					}
				}
			}
			if (selectedKey == null) {
				System.err.println("Impossible to find a certificate that could be used to sign");
			}
		}
		return selectedKey;
	}

	public void writeFile(String dir, String newfilename, DSSDocument signedDocument) {
		System.out.println("Create signed file: " + newfilename);
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

	private boolean haveNonRepudiation(DSSPrivateKeyEntry key) {
		boolean[] keyUsage = key.getCertificate().getCertificate().getKeyUsage();
		return keyUsage[nonRepudiationIndex];
	}

	private void format(String s, boolean val) {
		System.out.println(" " + s + " => " + val);
	}

	private void printCertificateSelectionField(DSSPrivateKeyEntry key, int index) {
		CertificateToken ct = key.getCertificate();
		String humanReadableSigner = DSSASN1Utils.getHumanReadableName(ct);
		System.out.println("[" + index + "] - certificate:" + humanReadableSigner);
	}

}
