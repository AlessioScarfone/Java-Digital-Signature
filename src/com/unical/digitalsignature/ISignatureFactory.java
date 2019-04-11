/*  
 *  Java-Digital-Signature
 *  Java command line tool for digital signature with PKCS#11 token.
 *  Copyright (C) 2018,2019  Alessio Scarfone
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	 This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	 GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.unical.digitalsignature;

import java.io.File;
import java.util.List;

import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.signature.AbstractSignatureService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.x509.CertificateToken;

public interface ISignatureFactory {

	public Pkcs11SignatureToken connectToToken(File driverPath, char[] pass);

	public DSSPrivateKeyEntry getSigner(List<DSSPrivateKeyEntry> keys,boolean choose_cert);
	
	public void showKeyUsage(CertificateToken ct);

	public void showCertificateData(CertificateToken ct);

	public AbstractSignatureParameters createParameter(DSSPrivateKeyEntry signer);

	public AbstractSignatureService<?> createService();

	public void createSignedFile(DSSDocument signedDocument);


}
