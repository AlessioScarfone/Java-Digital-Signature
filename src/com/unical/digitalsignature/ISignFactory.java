package com.unical.digitalsignature;

import java.io.File;
import java.util.List;

import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.signature.AbstractSignatureService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.x509.CertificateToken;

public interface ISignFactory {

	public Pkcs11SignatureToken connectToToken(File driverPath, char[] pass);

	public DSSPrivateKeyEntry getSigner(List<DSSPrivateKeyEntry> keys);
	
	public void showKeyUsage(CertificateToken ct);

	public void showCertificateData(CertificateToken ct);

	public AbstractSignatureParameters createParameter(DSSPrivateKeyEntry signer);

	public AbstractSignatureService<?> createService();

	public void createSignedFile(DSSDocument signedDocument);

}
