package org.conqueror.bird.gate.scan.file.hdfs;

import org.apache.hadoop.fs.Path;
import org.conqueror.bird.exceptions.parse.ParseException;
import org.conqueror.bird.gate.scan.file.FileInfoBuilder;
import org.conqueror.common.utils.file.hdfs.HdfsFile;
import org.conqueror.common.utils.file.hdfs.HdfsFileInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HdfsFileInfoBuilder extends FileInfoBuilder {

    private static HdfsFileInfoBuilder fileInfoBuilder = new HdfsFileInfoBuilder();

    public static HdfsFileInfoBuilder getInstance() {
        return fileInfoBuilder;
    }

    @Override
    public List<HdfsFileInfo> build(String uris, String delimiter) throws ParseException {
        List<String> uriList = toFileUris(uris, delimiter);
        List<HdfsFileInfo> fileInfos = new ArrayList<>();
        for (String uri : uriList) {
            try {
                fileInfos.add(new HdfsFileInfo(uri));
            } catch (InterruptedException | IOException e) {
                throw new ParseException(e);
            }
        }
        return fileInfos;
    }

    public List<String> buildUriString(String uris, String delimiter) throws ParseException {
        return toFileUris(uris, delimiter);
    }

    @Override
    protected String[] getFilePathsInDirectory(String dirPath, String fileRegexp) {
        try {
            HdfsFile dir = new HdfsFileInfo(dirPath).getFile();
            if (dir.isDirectory()) {
                return toUriStringList(dir.getChildren(false), fileRegexp);
            }
        } catch (IllegalArgumentException | IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String[] toUriStringList(List<HdfsFile> files, String fileRegexp) {
        List<String> uris = new ArrayList<>();
        for (HdfsFile file : files) {
            Path path = file.getPath();
            if (path.getName().matches(fileRegexp)) {
                uris.add(path.toUri().toString());
            }
        }
        return uris.toArray(new String[0]);
    }

}
