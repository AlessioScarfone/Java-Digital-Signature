package com.unical.utils;

import java.io.File;

public class PAdESProp {
	
	private boolean use = false;
	private File file = null;
	
	public PAdESProp(boolean use, File file) {
		super();
		this.use = use;
		this.file = file;
	}

	public boolean getUse() {
		return use;
	}

	public File getFile() {
		return file;
	}
	
}
