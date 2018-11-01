package org.conqueror.bird.gate.scan.file.local;

import org.conqueror.bird.exceptions.parse.ParseException;
import org.conqueror.bird.gate.scan.file.FileInfoBuilder;
import org.conqueror.common.utils.file.local.LocalFileInfo;

import java.io.File;
import java.io.FilenameFilter;
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
            fileInfos.add(new LocalFileInfo(filePath));
        }

        return fileInfos;
    }

    @Override
    protected String[] getFilePathsInDirectory(String dirPath, String fileRegexp) {
        return new File(dirPath).list(new RegexpFilenameFilter(fileRegexp));
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


}
