package org.conqueror.bird.gate;

import org.conqueror.common.utils.file.FileInfo;
import org.conqueror.common.utils.file.FileScanner;
import org.conqueror.common.utils.file.FileUtils;
import org.conqueror.common.utils.file.local.LocalFileInfo;
import org.conqueror.common.utils.file.local.LocalFileScanner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class DocIdInsert {

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        String uri = args[0];
        FileScanner scanner = new LocalFileScanner();
        FileInfo fileInfo = scanner.makeFileInfo(new URI(uri));
        List<FileInfo> files;
        if (scanner.isDirectory(fileInfo)) {
            files = scanner.getChildren(fileInfo, 1);
        } else if (scanner.isFile(fileInfo)) {
            files = new ArrayList<>(1);
            files.add((LocalFileInfo) fileInfo);
        } else {
            files = new ArrayList<>(0);
        }

        int id = 0;
        for (FileInfo file : files) {
            LocalFileInfo localFileInfo = (LocalFileInfo) file;
            List<String> lines = new ArrayList<>();
            for (String line : FileUtils.makeLinesToList(localFileInfo.getFile().getPath())) {
                lines.add(line.replace("{", "{\"id\":\"" + ++id + "\","));
            }

            FileUtils.writeList(localFileInfo.getFile().getPath(), lines, false);
        }

    }

}
