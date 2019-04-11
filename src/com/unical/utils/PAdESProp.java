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

package com.unical.utils;

import java.io.File;

import eu.europa.esig.dss.pades.SignatureImageParameters.VisualSignatureAlignmentHorizontal;
import eu.europa.esig.dss.pades.SignatureImageParameters.VisualSignatureAlignmentVertical;

public class PAdESProp {
	
	private boolean useVisibileSign = false;
	private File imageFile = null;
	
	private String fieldName;
	
	private VisualSignatureAlignmentHorizontal posH = VisualSignatureAlignmentHorizontal.LEFT; 
	private VisualSignatureAlignmentVertical posV = VisualSignatureAlignmentVertical.BOTTON;
	
	private boolean skipFieldToUseSelection = false;
	
//	private float posX = 0;
//	private float posY = 0;
	
	private int page = 1; //page start from 1
	
	public PAdESProp(boolean useVisibleSignature, File file,String fieldNameToSign,VisualSignatureAlignmentHorizontal posHor,VisualSignatureAlignmentVertical posVer,int page, boolean skipFieldToUseSelection) {
		super();
		this.useVisibileSign = useVisibleSignature;
		this.imageFile = file;
		this.fieldName = fieldNameToSign;
		if(posHor != null)
			this.posH = posHor;
		if(posVer != null)
			this.posV = posVer;
		this.page = page;
		this.skipFieldToUseSelection = skipFieldToUseSelection;
	}

	public boolean useVisibleSign() {
		return useVisibileSign;
	}

	public File getImageFile() {
		return imageFile;
	}

	public VisualSignatureAlignmentHorizontal getPosHorizontal() {
		return posH;
	}

	public VisualSignatureAlignmentVertical getPosVertical() {
		return posV;
	}
	
//	public float getPosX() {
//		return posX;
//	}
//
//	public void setPosX(float posX) {
//		this.posX = posX;
//	}

//	public float getPosY() {
//		return posY;
//	}
//
//	public void setPosY(float posY) {
//		this.posY = posY;
//	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getFieldName() {
		return fieldName;
	}

	public boolean getSkipFieldToUseSelection() {
		return skipFieldToUseSelection;
	}


}
