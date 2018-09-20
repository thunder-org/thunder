package org.conqueror.common.utils.file.remote;

import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.conqueror.common.utils.file.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteFileInfo extends FileInfo {
	
	private static final Logger logger = LoggerFactory.getLogger(RemoteFileInfo.class);

	private static final long serialVersionUID = 9010590906673739776L;

	private StandardFileSystemManager manager = new StandardFileSystemManager();
	private static FileSystemOptions options = new FileSystemOptions();

	private FileObject file = null;
	
	static {
		try {
			SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(options, "no");
		} catch (FileSystemException e) {
			e.printStackTrace();
		}
		SftpFileSystemConfigBuilder.getInstance().setTimeout(options, 10000);
		SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
	}

	public RemoteFileInfo(String schema, String domain, String filePath, String user, String password, int port)
			throws FileSystemException {
		this(schema, domain, filePath, user, password, port, 1);
	}

	public RemoteFileInfo(String schema, String domain, String filePath, String user, String password, int port, int tries)
			throws FileSystemException {
		this(makeUri(schema, domain, filePath, user, password, port), tries);
	}

	public RemoteFileInfo(String uri) throws FileSystemException {
		this(uri, 1);
	}

	public RemoteFileInfo(String uri, int tries) throws FileSystemException {
//		Session session SftpClientFactory.createConnection(hostname, port, username, password, fileSystemOptions);
		initFileSystem(manager);
		boolean succ = false;
		FileSystemException exception = null;
		
		while ( !succ && (tries-- > 0) ) {
			try {
				this.file = manager.resolveFile(uri, options);
				succ = true;
			} catch (FileSystemException e) {
				succ = false;
				exception = e;
			}
		}
		
		if (!succ) throw exception;
	}
	
	public RemoteFileInfo(FileObject file) throws FileSystemException {
		this.file = file;
	}

	@Override
	public FileObject getFile() {
		return file;
	}

	@Override
	public void close() throws FileSystemException {
		file.close();
		manager.close();
	}
	
	@Override
	public String toString() {
		try {
			return file.getURL().toExternalForm();
		} catch (FileSystemException e) {
			return "";
		}
	}
	
	private void initFileSystem(StandardFileSystemManager manager) throws FileSystemException {
		manager.setCacheStrategy(CacheStrategy.ON_RESOLVE);
		manager.init();
	}
	
	private static String makeUri(String schema, String domain, String filePath, String user, String password, int port) {
		if (password == null) {
			return schema + "://" + user + "@" + domain + ":" + port + "/" + filePath;
		}
		return schema + "://" + user + ":" + password + "@" + domain + ":" + port + "/" + filePath;
	}
	
}
