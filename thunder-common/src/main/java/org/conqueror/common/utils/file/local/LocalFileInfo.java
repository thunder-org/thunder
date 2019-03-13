package org.conqueror.common.utils.file.local;

import org.conqueror.common.utils.file.FileInfo;

import java.io.File;
import java.net.URI;


public class LocalFileInfo extends FileInfo {

    private File file;

    public LocalFileInfo(URI uri) {
        this.file = new File(uri);
    }

    public LocalFileInfo(File file) {
        this.file = file;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void close() {
        file = null;
    }

    @Override
    public String toString() {
        return file != null ? file.toString() : "";
    }

}
