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
	
//	private float posX = 0;
//	private float posY = 0;
	
	private int page = 1; //page start from 1
	
	public PAdESProp(boolean useVisibleSignature, File file,String fieldNameToSign,VisualSignatureAlignmentHorizontal posHor,VisualSignatureAlignmentVertical posVer,int page) {
		super();
		this.useVisibileSign = useVisibleSignature;
		this.imageFile = file;
		this.fieldName = fieldNameToSign;
		if(posHor != null)
			this.posH = posHor;
		if(posVer != null)
			this.posV = posVer;
		this.page = page;
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

}
