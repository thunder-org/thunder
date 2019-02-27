package org.conqueror.common.utils.file;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;


public class FileUtils {

    public static URL toURL(String filePath) {
        try {
            return new URL(filePath);
        } catch (MalformedURLException e) {
            try {
                return new URL("file", null, -1, filePath);
            } catch (MalformedURLException e1) {
                return null;
            }
        }
    }

    public static String getDirectoryPath(String filePath) {
        int idx = filePath.lastIndexOf(File.separator);
        if (idx == -1) idx = filePath.lastIndexOf('/');
        if (idx != -1) {
            return filePath.substring(0, idx);
        }
        return null;
    }

    public static String getFileName(String filePath) {
        int idx = filePath.lastIndexOf(File.separator);
        if (idx == -1) idx = filePath.lastIndexOf('/');
        if (idx != -1) {
            return filePath.substring(idx + 1);
        }
        return filePath;
    }

    public static String getFileContent(String filePath) {
        return getFileContent(filePath, Charset.defaultCharset());
    }

    public static String getFileContent(String filePath, Charset charset) {
        try {
            return new String(Files.readAllBytes(FileSystems.getDefault().getPath(filePath)), charset);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileContent(String directory, String fileName) {
        return getFileContent(directory, fileName, Charset.defaultCharset());
    }

    public static String getFileContent(String directory, String fileName, Charset charset) {
        try {
            return IOUtils.toString(new FileInputStream(
                new File(directory + ((directory.contains(File.separator))? File.separator : '/') + fileName))
                , charset);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void appendTextLine(String filePath, String text) throws IOException {
        Path path = FileSystems.getDefault().getPath(filePath);

        if (!Files.exists(path)) Files.createFile(path);
        else if (Files.size(path) > 0) text = "\n" + text;

        Files.write(path, text.getBytes(), StandardOpenOption.APPEND);
    }

    public static List<String> makeLinesToList(String filePath) throws IOException {
        return makeLinesToList(filePath, Charset.defaultCharset());
    }

    public static List<String> makeLinesToList(String filePath, Charset charset) throws IOException {
        Path path = FileSystems.getDefault().getPath(filePath);

        return Files.readAllLines(path, charset);
    }

    public static void writeList(String filePath, List<String> list, boolean append) throws IOException {
        Path path = FileSystems.getDefault().getPath(filePath);

        if (append && Files.size(path) > 0) Files.write(path, "\n".getBytes(), StandardOpenOption.APPEND);

        Files.write(path, list, StandardOpenOption.CREATE, append? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING );
    }
}
