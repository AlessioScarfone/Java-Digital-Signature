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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.Files;
import com.unical.argparser.ArgsParser;
import com.unical.utils.Utility;

import eu.europa.esig.dss.DSSASN1Utils;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.x509.CertificateToken;

public abstract class AbstractSignatureFactory implements ISignatureFactory {

	protected File inputFile;
	private String keyUsageLabel[] = { "digitalSignature", "nonRepudiation", "keyEncipherment", "dataEncipherment",
			"keyAgreement", "keyCertSign", "cRLSign", "encipherOnly", "decipherOnly" };

	private final int nonRepudiationIndex = 1;

	public AbstractSignatureFactory(File inputFile) {
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
					int n = Utility.getValidIntInRange("Select a certificate to use:", 0, usable_keys.size());
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

	@Override
	public void createSignedFile(DSSDocument signedDocument) {
		String newfilename = getNameNewFile();

		String dir = getOutputDirectory();

		writeFile(dir, newfilename, signedDocument);

	}

	protected abstract String getNameNewFile();

	public String getOutputDirectory() {
		String dir = ArgsParser.getInstance().getOutputDirectory();
		if (dir == null)
			dir = inputFile.getParent();
		if (dir == null)
			dir = ".";
		File fileDest = new File(dir);
		if (!fileDest.exists())
			fileDest.mkdirs();

		return dir;
	}

	protected String checkIfFileExist(String newfilename) {
		// check if file already exist
		String dir = getOutputDirectory();
		int c = 1;
		String originalFileName = Files.getNameWithoutExtension(newfilename);
		String originalExt = Files.getFileExtension(originalFileName);
		String currentName = newfilename;
		while (new File(Utility.buildFilePath(dir, currentName)).exists()) {

			if (Files.getFileExtension(newfilename).equals("p7m")) {
				// if the file is in cades format it has two extensions.The original
				// and the p7m extension, then adds the counter before the first extension
				currentName = Files.getNameWithoutExtension(originalFileName) + "(" + c + ")." + originalExt + ".p7m";
			} else {
				currentName = originalFileName + "(" + c + ")." + Files.getFileExtension(newfilename);
			}
			c++;
		}
		return currentName;
	}

	public void writeFile(String dir, String newfilename, DSSDocument signedDocument) {
		System.out.println("Create signed file: " + newfilename);
//		System.out.println("Create signed file: " + Utility.buildFilePath(dir,newfilename));
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
