package org.conqueror.bird.gate.scan.file.local;

import org.conqueror.bird.exceptions.parse.ParseException;
import org.conqueror.bird.gate.scan.file.FileInfoBuilder;
import org.conqueror.common.utils.file.local.LocalFileInfo;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class LocalFileInfoBuilder extends FileInfoBuilder {

    private static LocalFileInfoBuilder fileInfoBuilder = new LocalFileInfoBuilder();

    public static LocalFileInfoBuilder getInstance() {
        return fileInfoBuilder;
    }

    @Override
    public List<LocalFileInfo> build(String fileInfo, String delimiter) throws ParseException {
        List<String> filePaths = toFileUris(fileInfo, delimiter);
        List<LocalFileInfo> fileInfos = new ArrayList<>();
        for (String filePath : filePaths) {
            try {
                fileInfos.add(new LocalFileInfo(new URI(filePath)));
            } catch (URISyntaxException e) {
                throw new ParseException(e);
            }
        }

        return fileInfos;
    }

    @Override
    protected String[] getFilePathsInDirectory(String dirPath, String fileRegexp) {
        try {
            File[] files = new File(new URI(dirPath)).listFiles(new RegexpFilenameFilter(fileRegexp));
            if (files == null) return null;
            String[] filePaths = new String[files.length];
            int idx = 0;
            for (File file : files) {
                filePaths[idx++] = file.toURI().toString();
            }

            return filePaths;
        } catch (URISyntaxException e) {
            return null;
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

    public static void main(String[] args) throws URISyntaxException {
        LocalFileInfoBuilder builder = new LocalFileInfoBuilder();
        String[] files = new File(new URI("file:///g:/workspace/data/raw")).list(new RegexpFilenameFilter("[0-9]+.json"));
        int idx=0;
        for (String file : files) {
            System.out.println(++idx + ":" + file);
        }
    }

}
