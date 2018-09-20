package org.conqueror.common.utils.file.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;


public class HdfsFile {

	private static final Class<LocalFileSystem> HDFS_LOCAL_FS_CLASS = LocalFileSystem.class;
	private static final Class<DistributedFileSystem> HDFS_DFS_CLASS = DistributedFileSystem.class;
	
	private FileSystem hdfs;
	private Path file;
	
	private static abstract class HdfsAction<T> implements PrivilegedExceptionAction<T> {
		
		private Configuration conf;
		
		public HdfsAction(Configuration conf) {
			this.conf = conf;
		}

		@Override
		public T run() throws Exception {
			return execute();
		}
		
		public abstract T execute() throws Exception;
		
		public Configuration getConf() {
			return conf;
		}
		
	}
	
	public HdfsFile(String file) throws IOException, InterruptedException {
		this(new Path(file));
	}

	public HdfsFile(Path file) throws IOException, InterruptedException {
		this(buildHdfs(file), file);
	}
	
	public HdfsFile(FileSystem hdfs, Path file) {
		this.hdfs = hdfs;
		this.file = file;
	}
	
	private static FileSystem buildHdfs(Path file) throws IOException, InterruptedException {
		URI uri = file.toUri();

		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", uri.getScheme() + "://" + uri.getUserInfo() + "@" + uri.getHost() + ":" + uri.getPort());
		conf.set("fs.file.impl", HDFS_LOCAL_FS_CLASS.getName());
		conf.set("fs.hdfs.impl", HDFS_DFS_CLASS.getName());
		
		return UserGroupInformation.createRemoteUser(uri.getUserInfo()).doAs(new HdfsAction<FileSystem>(conf) {
			@Override
			public FileSystem execute() throws Exception {
				return FileSystem.get(getConf());
			}
		});
	}

	public FileSystem getFileSystem() {
		return hdfs;
	}
	
	public Path getPath() {
		return file;
	}

	public boolean exists() throws IOException {
		return hdfs.exists(file);
	}

	public boolean notExists() throws IOException {
		return !exists();
	}

	public boolean isFile() throws IOException {
		return hdfs.getFileStatus(file).isFile();
	}
	
	public boolean isDirectory() throws IOException {
		return hdfs.getFileStatus(file).isDirectory();
	}
	
	public List<HdfsFile> getChildren(boolean recursive) throws IOException {
		List<HdfsFile> children = new ArrayList<>();
		RemoteIterator<LocatedFileStatus> iterator = hdfs.listFiles(file, recursive);
		while (iterator.hasNext()) {
			children.add(new HdfsFile(hdfs, iterator.next().getPath()));
		}
		return children;
	}
	
	public FSDataInputStream getInputStream() throws IOException {
		return hdfs.open(file);
	}

	public FSDataOutputStream getOutputStream() throws IOException {
		return hdfs.create(file);
	}

	public void close() throws IOException {
		if (hdfs != null) hdfs.close();
	}

}
