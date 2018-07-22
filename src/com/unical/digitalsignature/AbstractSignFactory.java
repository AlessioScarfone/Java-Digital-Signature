package com.unical.digitalsignature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unical.utils.Utility;

import eu.europa.esig.dss.DSSASN1Utils;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.x509.CertificateToken;

public abstract class AbstractSignFactory implements ISignFactory {
	
	public Pkcs11SignatureToken connectToToken(File driverPath, char[] pass) {
		Pkcs11SignatureToken token = new Pkcs11SignatureToken(driverPath.getAbsolutePath(), pass);
		return token;
	}
	
	public void printTokenData(List<DSSPrivateKeyEntry> keys) {
		for (DSSPrivateKeyEntry entry : keys) {
			System.out.println(entry.getCertificate().getCertificate());
		}
	}

	// SELECT CERTIFICATE TO USE
	public DSSPrivateKeyEntry getSigner(List<DSSPrivateKeyEntry> keys) {
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
			System.out.println("Certificates:");
			for (String k : map.keySet()) {
				System.out.println(k+"\n");
			}
		}
		return selectedKey;
	}
	
	public void writeFile(String dir,String newfilename,DSSDocument signedDocument) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(dir + Utility.separator + newfilename);
			Utils.copy(signedDocument.openStream(), fos);
			Utils.closeQuietly(fos);
		} catch (DSSException | IOException e) {
			System.err.println("Error write file");
//			e.printStackTrace();
		}
	}
}
