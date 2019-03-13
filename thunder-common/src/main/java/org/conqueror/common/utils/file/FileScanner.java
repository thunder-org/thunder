package org.conqueror.common.utils.file;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public abstract class FileScanner implements Closeable {

    public abstract boolean isFile(FileInfo file) throws IOException;

    public abstract boolean isDirectory(FileInfo file) throws IOException;

    public abstract List<FileInfo> getChildren(FileInfo directory, int depth) throws IOException;

    public abstract List<FileInfo> getChildren(FileInfo directory, String fileRegexp) throws IOException, InterruptedException, URISyntaxException;

    public abstract BufferedReader getReader(FileInfo file) throws IOException;

    public abstract void close();

    public abstract FileInfo makeFileInfo(URI fileUri) throws IOException, InterruptedException, URISyntaxException;

}
