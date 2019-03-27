package org.conqueror.common.utils.file.remote;

import com.jcraft.jsch.Session;
import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpClientFactory;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.conqueror.common.utils.file.FileInfo;

import java.net.URI;
import java.net.URISyntaxException;


public class RemoteFileInfo extends FileInfo {

    private StandardFileSystemManager manager = new StandardFileSystemManager();
    private static FileSystemOptions options = new FileSystemOptions();

    private FileObject file = null;

    static {
        try {
            SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(options, "no");
        } catch (FileSystemException e) {
        }
        SftpFileSystemConfigBuilder.getInstance().setTimeout(options, 10000);
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
    }

    public RemoteFileInfo(String schema, String domain, String filePath, String user, String password, int port)
        throws FileSystemException, URISyntaxException {
        this(schema, domain, filePath, user, password, port, 1);
    }

    public RemoteFileInfo(String schema, String domain, String filePath, String user, String password, int port, int tries)
        throws FileSystemException, URISyntaxException {
        this(makeUri(schema, domain, filePath, user, password, port), tries);
    }

    public RemoteFileInfo(String uri) throws FileSystemException, URISyntaxException {
        this(uri, 1);
    }

    public RemoteFileInfo(String uri, int tries) throws FileSystemException, URISyntaxException {
        this(new URI(uri), tries);
    }

    public RemoteFileInfo(URI uri) throws FileSystemException {
        this(uri, 1);
    }

    public RemoteFileInfo(URI uri, int tries) throws FileSystemException {
//		Session session = SftpClientFactory.createConnection(hostname, port, username, password, fileSystemOptions);
        initFileSystem(manager);
        boolean succ = false;
        FileSystemException exception = null;

        while (tries-- > 0) {
            try {
                file = manager.resolveFile(uri);
                if (file.exists()) {
                    succ = true;
                    break;
                }
            } catch (FileSystemException e) {
                exception = e;
            }
        }

        if (!succ && exception != null) throw exception;
    }

    public RemoteFileInfo(FileObject file) {
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
