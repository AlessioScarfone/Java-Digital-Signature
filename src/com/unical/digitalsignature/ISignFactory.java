package com.unical.digitalsignature;

import java.io.File;
import java.util.List;

import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.signature.AbstractSignatureService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;

public interface ISignFactory {

	public Pkcs11SignatureToken connectToToken(File driverPath, char[] pass);

	public void printTokenData(List<DSSPrivateKeyEntry> keys);

	public DSSPrivateKeyEntry getSigner(List<DSSPrivateKeyEntry> keys);

	public AbstractSignatureParameters setParameter(DSSPrivateKeyEntry signer);

	public AbstractSignatureService<?> createService();

	public void createSignedFile(DSSDocument signedDocument, File inputFile);

}
