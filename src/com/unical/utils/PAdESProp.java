package com.unical.utils;

import java.io.File;

import eu.europa.esig.dss.pades.SignatureImageParameters.VisualSignatureAlignmentHorizontal;
import eu.europa.esig.dss.pades.SignatureImageParameters.VisualSignatureAlignmentVertical;

public class PAdESProp {
	
	private boolean useVisibileSign = false;
	private File imageFile = null;
	
	private VisualSignatureAlignmentHorizontal posH = VisualSignatureAlignmentHorizontal.LEFT; 
	private VisualSignatureAlignmentVertical posV = VisualSignatureAlignmentVertical.BOTTON;
	
	private float posX = 0;
	private float posY = 0;
	
	private int page = 1; //page start from 1
	
	public PAdESProp(boolean use, File file,VisualSignatureAlignmentHorizontal posHor,VisualSignatureAlignmentVertical posVer) {
		super();
		this.useVisibileSign = use;
		this.imageFile = file;
		if(posHor != null)
			this.posH = posHor;
		if(posVer != null)
			this.posV = posVer;
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
	
	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public String toString() {
		return "PAdESProp [useVisibileSign=" + useVisibileSign + ", imageFile=" + imageFile + ", posH=" + posH
				+ ", posV=" + posV + ", posX=" + posX + ", posY=" + posY + ", page=" + page + "]";
	}
	
	
	
	
	
	
	
}
