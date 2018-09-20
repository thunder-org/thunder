package org.conqueror.common.utils.file.local;

import org.conqueror.common.utils.file.FileInfo;
import org.conqueror.common.utils.file.FileScanner;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LocalFileScanner extends FileScanner {

	private static final class FileSource {

		private BufferedReader reader;

		public FileSource(BufferedReader reader) {
			this.reader = reader;
		}

		public void close() throws IOException {
			reader.close();
		}

	}
	
	private static class RegexpFilenameFilter implements FilenameFilter {

		private String regexp;
	
		public RegexpFilenameFilter(String regexp) {
			this.regexp = regexp;
		}
	
		public boolean accept(File dir, String name) {
			return name.matches(regexp);
		}

	}

	private Map<File, FileSource> fileSources = new HashMap<>();

	@Override
	public boolean isFile(FileInfo file) {
		return isFile((LocalFileInfo)file);
	}

	public boolean isFile(LocalFileInfo file) {
		return file.getFile().isFile();
	}

	@Override
	public boolean isDirectory(FileInfo file) {
		return isDirectory((LocalFileInfo)file);
	}

	public boolean isDirectory(LocalFileInfo file) {
		return file.getFile().isDirectory();
	}

	@Override
	public List<LocalFileInfo> getChildren(FileInfo directory, int depth) {
		return getChildren((LocalFileInfo)directory, depth);
	}

	public List<LocalFileInfo> getChildren(LocalFileInfo directory, int depth) {
		return getChildren(directory.getFile(), depth);
	}

	public List<LocalFileInfo> getChildren(File directory, int depth) {
		List<File> files = new ArrayList<>();
		getChildren(directory, files, depth);
		return toFileInfoList(files);
	}

	@Override
	public List<LocalFileInfo> getChildren(FileInfo directory, String fileRegexp) {
		return getChildren((LocalFileInfo)directory, fileRegexp);
	}

	public List<LocalFileInfo> getChildren(LocalFileInfo directory, String fileRegexp) {
		return getChildren(directory.getFile(), fileRegexp);
	}

	public List<LocalFileInfo> getChildren(File directory, String fileRegexp) {
		String[] children = directory.list(new RegexpFilenameFilter(fileRegexp));
		return toFileInfoList(children);
	}

	public BufferedReader getReader(FileInfo file) throws IOException {
		return getReader(((LocalFileInfo)file).getFile());
	}
	
	public BufferedReader getReader(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file))));
		this.fileSources.put(file, new FileSource(reader));
		return reader;
	}
	
	public void close() {
		for (Entry<File, FileSource> entry : fileSources.entrySet()) {
			try {
				entry.getValue().close();
			} catch (IOException e) {
			}
		}
		fileSources.clear();
	}

	@Override
	public FileInfo makeFileInfo(String fileUri) {
		return new LocalFileInfo(fileUri);
	}

	private static void getChildren(File root, List<File> files, int depth) {
		getChildren(root, files, depth, null);
	}
	
	private static void getChildren(File root, List<File> files, int depth, String exceptPrefix) {
		if (root.isDirectory()) {
			if (depth > 0) {
				File[] children = root.listFiles();
				if (children != null) {
					for (File file : children) {
						getChildren(file, files, --depth, exceptPrefix);
					}
				}
			}
		} else {
			if (exceptPrefix != null) {
				if (!root.getName().startsWith(exceptPrefix)) files.add(root);
			} else {
				files.add(root);
			}
		}
	}
	
	private static List<LocalFileInfo> toFileInfoList(List<File> files) {
		List<LocalFileInfo> fileInfos = new ArrayList<>();
		for (File child : files) {
			fileInfos.add(new LocalFileInfo(child));
		}
		return fileInfos;
	}

	private static List<LocalFileInfo> toFileInfoList(String[] files) {
		List<LocalFileInfo> fileInfos = new ArrayList<>();
		for (String child : files) {
			fileInfos.add(new LocalFileInfo(child));
		}
		return fileInfos;
	}
	
}
