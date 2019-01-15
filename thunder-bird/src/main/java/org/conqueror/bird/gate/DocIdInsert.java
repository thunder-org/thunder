package org.conqueror.bird.gate;

import org.conqueror.common.utils.file.FileInfo;
import org.conqueror.common.utils.file.FileScanner;
import org.conqueror.common.utils.file.FileUtils;
import org.conqueror.common.utils.file.local.LocalFileInfo;
import org.conqueror.common.utils.file.local.LocalFileScanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DocIdInsert {

    public static void main(String[] args) throws IOException, InterruptedException {
        String uri = args[0];
        FileScanner scanner = new LocalFileScanner();
        FileInfo fileInfo = scanner.makeFileInfo(uri);
        List<LocalFileInfo> files;
        if (scanner.isDirectory(fileInfo)) {
            files = (List<LocalFileInfo>) scanner.getChildren(fileInfo, 1);
        } else if (scanner.isFile(fileInfo)) {
            files = new ArrayList<>(1);
            files.add((LocalFileInfo) fileInfo);
        } else {
            files = new ArrayList<>(0);
        }

        int id = 0;
        for (LocalFileInfo file : files) {
            List<String> lines = new ArrayList<>();
            for (String line : FileUtils.makeLinesToList(file.getFile().getPath())) {
                lines.add(line.replace("{", "{\"id\":\"" + ++id + "\","));
            }

            FileUtils.writeList(file.getFile().getPath(), lines, false);
        }

    }

}
