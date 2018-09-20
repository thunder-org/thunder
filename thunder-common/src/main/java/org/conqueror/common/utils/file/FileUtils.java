package org.conqueror.common.utils.file;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileUtils {

	public static String getDirectoryPath(String filePath) {
		int idx = filePath.lastIndexOf(File.separator);
		if (idx != -1) {
			return filePath.substring(0, idx);
		}
		return null;
	}

	public static String getFileName(String filePath) {
		int idx = filePath.lastIndexOf(File.separator);
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
			return IOUtils.toString(new FileInputStream(new File(directory + File.separator + fileName)), charset);
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

}
