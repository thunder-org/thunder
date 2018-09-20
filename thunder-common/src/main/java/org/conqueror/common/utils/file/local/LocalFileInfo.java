package org.conqueror.common.utils.file.local;

import org.conqueror.common.utils.file.FileInfo;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class LocalFileInfo extends FileInfo {
	
	private static final long serialVersionUID = -1269573516970492755L;

	private File file;
	
	public LocalFileInfo(String path) {
		try {
			URI uri = new URI(path);
			this.file = new File(uri.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public LocalFileInfo(File file) {
		this.file = file;
	}

	@Override
	public File getFile() {
		return this.file;
	}
	
	@Override
	public void close() {
	}
	
	@Override
	public String toString() {
		return file.toString();
	}

}
