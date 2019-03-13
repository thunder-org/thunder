package org.conqueror.common.utils.file;

import java.io.IOException;
import java.io.Serializable;


public abstract class FileInfo implements Serializable {

    public abstract Object getFile();

    public abstract void close() throws IOException;

    public abstract String toString();

}
