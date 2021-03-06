package org.conqueror.common.utils.file.hdfs;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.conqueror.common.utils.file.FileInfo;
import org.conqueror.common.utils.file.FileScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class HdfsFileScanner extends FileScanner {

    private static final class FileSource {

        private FSDataInputStream inputStream;

        public FileSource(FSDataInputStream inputStream) {
            this.inputStream = inputStream;
        }

        public void close() {
            try {
                inputStream.close();
            } catch (IOException ignored) {
                inputStream = null;
            }
        }
    }

    private Map<HdfsFile, FileSource> fileSources = new HashMap<>();

    @Override
    public boolean isFile(FileInfo file) throws IOException {
        return isFile((HdfsFileInfo) file);
    }

    public boolean isFile(HdfsFileInfo file) throws IOException {
        return isFile(file.getFile());
    }

    public boolean isFile(HdfsFile file) throws IOException {
        return file.isFile();
    }

    @Override
    public boolean isDirectory(FileInfo file) throws IOException {
        return isDirectory((HdfsFileInfo) file);
    }

    public boolean isDirectory(HdfsFileInfo file) throws IOException {
        return isDirectory(file.getFile());
    }

    public boolean isDirectory(HdfsFile file) throws IOException {
        return file.isDirectory();
    }

    @Override
    public List<FileInfo> getChildren(FileInfo directory, int depth)
        throws IOException {
        return getChildren((HdfsFileInfo) directory, depth);
    }

    public List<FileInfo> getChildren(HdfsFileInfo directory, int depth)
        throws IOException {
        return getChildren(directory.getFile(), depth);
    }

    public List<FileInfo> getChildren(HdfsFile directory, int depth)
        throws IOException {
        List<HdfsFile> files = new ArrayList<>();
        getChildren(directory, files, depth);
        return toFileInfoList(files);
    }

    @Override
    public List<FileInfo> getChildren(FileInfo directory, String fileRegexp)
        throws IOException, InterruptedException {
        return getChildren((HdfsFileInfo) directory, fileRegexp);
    }

    public List<FileInfo> getChildren(HdfsFileInfo directory, String fileRegexp)
        throws IOException, InterruptedException {
        return getChildren(directory.getFile(), fileRegexp);
    }

    public List<FileInfo> getChildren(HdfsFile directory, String fileRegexp)
        throws IOException, InterruptedException {
        List<FileInfo> fileList = new ArrayList<>();
        List<HdfsFile> files = new ArrayList<>();
        getChildren(directory, files, 1);
        for (HdfsFile file : files) {
            Path path = file.getPath();
            if (path.getName().matches(fileRegexp)) {
                fileList.add(new HdfsFileInfo(path));
            }
        }

        return fileList;
    }

    @Override
    public BufferedReader getReader(FileInfo file) throws IOException {
        FSDataInputStream inputStream = ((HdfsFileInfo) file).getFile().getInputStream();
        HdfsFile hdfsFile = ((HdfsFileInfo) file).getFile();

        FileSource source = new FileSource(inputStream);
        if (hdfsFile.exists()) {
            fileSources.put(hdfsFile, source);
        } else {
            source.close();
            return null;
        }

        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    @Override
    public void close() {
        for (Entry<HdfsFile, FileSource> entry : fileSources.entrySet()) {
            try {
                entry.getKey().close();
                entry.getValue().close();
            } catch (IOException ignored) {
                entry.setValue(null);
            }
        }
        fileSources.clear();
    }

    @Override
    public FileInfo makeFileInfo(URI fileUri) throws IOException, InterruptedException {
        return new HdfsFileInfo(fileUri.getPath());
    }

    private static void getChildren(HdfsFile root, List<HdfsFile> files, int depth)
        throws IOException {
        getChildren(root, files, depth, null);
    }

    private static void getChildren(HdfsFile root, List<HdfsFile> files, int depth, String exceptPrefix)
        throws IOException {
        if (root.isDirectory()) {
            if (depth > 0) {
                for (HdfsFile file : root.getChildren(false)) {
                    getChildren(file, files, --depth, exceptPrefix);
                }
            }
        } else {
            if (exceptPrefix != null) {
                if (!root.getPath().getName().startsWith(exceptPrefix)) files.add(root);
            } else {
                files.add(root);
            }
        }
    }

    private static List<FileInfo> toFileInfoList(List<HdfsFile> files) {
        List<FileInfo> fileInfos = new ArrayList<>();
        for (HdfsFile child : files) {
            fileInfos.add(new HdfsFileInfo(child));
        }
        return fileInfos;
    }

}
