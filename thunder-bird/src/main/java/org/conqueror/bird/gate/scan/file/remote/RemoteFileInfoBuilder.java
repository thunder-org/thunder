package org.conqueror.bird.gate.scan.file.remote;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.conqueror.bird.exceptions.parse.ParseException;
import org.conqueror.bird.gate.scan.file.FileInfoBuilder;
import org.conqueror.common.utils.file.remote.RemoteFileInfo;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RemoteFileInfoBuilder extends FileInfoBuilder {

    private static RemoteFileInfoBuilder fileInfoBuilder = new RemoteFileInfoBuilder();

    public static RemoteFileInfoBuilder getInstance() {
        return fileInfoBuilder;
    }

    @Override
    public List<RemoteFileInfo> build(String uris, String delimiter) throws ParseException {
        try {
            List<String> uriList = toFileUris(uris, delimiter);
            List<RemoteFileInfo> fileInfos = new ArrayList<>();
            for (String uri : uriList) {
                fileInfos.add(new RemoteFileInfo(uri, 3));
            }

            return fileInfos;
        } catch (FileSystemException | URISyntaxException e) {
            throw new ParseException(e);
        }
    }

    public List<String> buildUriString(String uris, String delimiter) throws ParseException {
        return toFileUris(uris, delimiter);
    }

    @Override
    protected String[] getFilePathsInDirectory(String dirUri, String fileRegexp) throws ParseException {
        try {
            RemoteFileInfo fileInfo = new RemoteFileInfo(dirUri, 3);
            FileObject dir = fileInfo.getFile();
            FileObject[] children = null;

            try {
                if (dir.exists()) {
                    FileType type = dir.getType();
                    if (type == FileType.FOLDER) {
                        children = dir.getChildren();
                        return toUriStringList(children, fileRegexp);
                    }
                }
            } finally {
                if (children != null) {
                    for (FileObject child : children) {
                        child.close();
                    }
                }
                fileInfo.close();
            }
        } catch (FileSystemException | URISyntaxException e) {
            throw new ParseException(e);
        }
        return new String[0];
    }

    private static String[] toUriStringList(FileObject[] files, String fileRegexp)
        throws FileSystemException {
        List<String> uris = new ArrayList<>();
        for (FileObject file : files) {
            String path = file.getURL().getPath();
            if (path.substring(path.lastIndexOf("/") + 1).matches(fileRegexp)) {
                uris.add(file.getURL().toString());
            }
        }
        return uris.toArray(new String[0]);
    }

}
