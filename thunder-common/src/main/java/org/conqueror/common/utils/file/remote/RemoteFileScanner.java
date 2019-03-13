package org.conqueror.common.utils.file.remote;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.conqueror.common.utils.file.FileInfo;
import org.conqueror.common.utils.file.FileScanner;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class RemoteFileScanner extends FileScanner {

    private static final class FileSource {

        private FileContent content;
        private BufferedReader reader;

        public FileSource(FileContent content, BufferedReader reader) {
            this.content = content;
            this.reader = reader;
        }

        public void close() {
            try {
                reader.close();
                content.close();
            } catch (IOException ignored) {
                reader = null;
                content = null;
            }
        }
    }

    private final int retries;

    private Map<FileObject, FileSource> fileSources = new HashMap<>();

    public RemoteFileScanner(int retries) {
        this.retries = retries;
    }

    @Override
    public boolean isFile(FileInfo file) throws FileSystemException {
        return isFile((RemoteFileInfo) file);
    }

    public boolean isFile(RemoteFileInfo file) throws FileSystemException {
        return isFile(file.getFile());
    }

    public boolean isFile(FileObject file) throws FileSystemException {
        return isFile(file, retries);
    }

    @Override
    public boolean isDirectory(FileInfo file) throws FileSystemException {
        return isDirectory((RemoteFileInfo) file);
    }

    public boolean isDirectory(RemoteFileInfo file) throws FileSystemException {
        return isDirectory(file.getFile());
    }

    public boolean isDirectory(FileObject file) throws FileSystemException {
        return isDirectory(file, retries);
    }

    public List<FileInfo> getChildren(FileInfo directory, int depth)
        throws FileSystemException {
        return getChildren((RemoteFileInfo) directory, depth);
    }

    public List<FileInfo> getChildren(RemoteFileInfo directory, int depth)
        throws FileSystemException {
        return getChildren(directory.getFile(), depth);
    }

    public List<FileInfo> getChildren(FileObject directory, int depth)
        throws FileSystemException {
        List<FileObject> files = new ArrayList<>();
        getChildren(directory, files, depth);
        return toFileInfoList(files);
    }

    public List<FileInfo> getChildren(FileInfo directory, String fileRegexp)
        throws FileSystemException {
        return getChildren((RemoteFileInfo) directory, fileRegexp);
    }

    public List<FileInfo> getChildren(RemoteFileInfo directory, String fileRegexp)
        throws FileSystemException {
        return getChildren(directory.getFile(), fileRegexp);
    }

    public List<FileInfo> getChildren(FileObject directory, String fileRegexp)
        throws FileSystemException {
        List<FileInfo> fileList = new ArrayList<>();
        List<FileObject> files = new ArrayList<>();
        getChildren(directory, files, 1);
        for (FileObject file : files) {
            if (file.getName().getBaseName().matches(fileRegexp)) {
                fileList.add(new RemoteFileInfo(file));
            }
        }

        return fileList;
    }

    @Override
    public BufferedReader getReader(FileInfo file) throws FileSystemException {
        return getReader(((RemoteFileInfo) file).getFile());
    }

    public BufferedReader getReader(FileObject file) throws FileSystemException {
        FileContent content = file.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(content.getInputStream())));

        FileSource source = new FileSource(content, reader);
        try {
            if (file.isReadable()) {
                fileSources.put(file, source);
            } else {
                source.close();
                reader = null;
            }
        } catch (FileSystemException e) {
            try {
                if (reader.ready()) {
                    fileSources.put(file, source);
                }
            } catch (IOException e1) {
                throw new FileSystemException(e);
            }
        }

        return reader;
    }

    public void close() {
        for (Entry<FileObject, FileSource> entry : fileSources.entrySet()) {
            entry.getValue().close();
        }
        fileSources.clear();
    }

    @Override
    public FileInfo makeFileInfo(URI fileUri) {
        try {
            return new RemoteFileInfo(fileUri);
        } catch (FileSystemException e) {
            return null;
        }
    }

    private void getChildren(FileObject root, List<FileObject> files, int depth)
        throws FileSystemException {
        getChildren(root, files, depth, null);
    }

    private void getChildren(FileObject root, List<FileObject> files, int depth, String exceptPrefix)
        throws FileSystemException {
        if (isDirectory(root, retries)) {
            if (depth > 0) {
                for (FileObject file : root.getChildren()) {
                    getChildren(file, files, --depth, exceptPrefix);
                }
            }
        } else {
            if (exceptPrefix != null) {
                if (!root.getName().getBaseName().startsWith(exceptPrefix)) files.add(root);
            } else {
                files.add(root);
            }
        }
    }

    private static boolean isFile(FileObject file, int retries) throws FileSystemException {
        while (retries-- > 0) {
            try {
                if (file.exists()) {
                    FileType type = file.getType();
                    if (type == FileType.FILE) return true;
                    else break;
                }
            } catch (FileSystemException e) {
                if (retries == 0) throw e;
            }
        }
        return false;
    }

    private static boolean isDirectory(FileObject file, int retries) throws FileSystemException {
        while (retries-- > 0) {
            try {
                if (file.exists()) {
                    if (file.getType() == FileType.FOLDER) return true;
                    else break;
                }
            } catch (FileSystemException e) {
                if (retries == 0) throw e;
            }
        }
        return false;
    }

    private static List<FileInfo> toFileInfoList(List<FileObject> files) {
        List<FileInfo> fileInfos = new ArrayList<>();
        for (FileObject child : files) {
            fileInfos.add(new RemoteFileInfo(child));
        }
        return fileInfos;
    }

}
