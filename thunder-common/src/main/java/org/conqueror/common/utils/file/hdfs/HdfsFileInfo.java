package org.conqueror.common.utils.file.hdfs;

import org.apache.hadoop.fs.Path;
import org.conqueror.common.utils.file.FileInfo;

import java.io.IOException;

public class HdfsFileInfo extends FileInfo {

	private static final long serialVersionUID = -9207089639728824431L;

	private HdfsFile file;
	
	public HdfsFileInfo(String path) throws IOException, InterruptedException {
		this(new Path(path));
	}

	public HdfsFileInfo(Path path) throws IOException, InterruptedException {
		this.file = new HdfsFile(path);
	}

	public HdfsFileInfo(HdfsFile file) throws IOException {
		this.file = file;
	}

	@Override
	public HdfsFile getFile() {
		return this.file;
	}
	
	@Override
	public void close() throws IOException {
		this.file.close();
	}
	
	@Override
	public String toString() {
		return file.getPath().toString();
	}
	
}