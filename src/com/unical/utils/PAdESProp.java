package com.unical.utils;

import java.io.File;

import eu.europa.esig.dss.pades.SignatureImageParameters.VisualSignatureAlignmentHorizontal;
import eu.europa.esig.dss.pades.SignatureImageParameters.VisualSignatureAlignmentVertical;

public class PAdESProp {
	
	private boolean useVisibileSign = false;
	private File imageFile = null;
	
	private VisualSignatureAlignmentHorizontal posH = VisualSignatureAlignmentHorizontal.LEFT; 
	private VisualSignatureAlignmentVertical posV = VisualSignatureAlignmentVertical.BOTTON; 
	
	public PAdESProp(boolean use, File file,VisualSignatureAlignmentHorizontal posHor,VisualSignatureAlignmentVertical posVer) {
		super();
		this.useVisibileSign = use;
		this.imageFile = file;
		if(posHor != null)
			this.posH = posHor;
		if(posVer != null)
			this.posV = posVer;
	}

	public boolean getUseVisibleSign() {
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
	
	
	@Override
	public String toString() {
		String s= "Use Visible Sign:"+getUseVisibleSign()+"\nHorizontal pos:"+
				getPosHorizontal().toString()+"\nVerticalPos:"+getPosVertical().toString();
		if(getImageFile() != null)
			s=s+"\nImage File:"+getImageFile().toString();
		
		return s;
	}
	
	
	
	
	
}
