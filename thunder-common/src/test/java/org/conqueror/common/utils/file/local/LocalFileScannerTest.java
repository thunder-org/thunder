package org.conqueror.common.utils.file.local;

import org.conqueror.common.utils.file.FileInfo;
import org.conqueror.common.utils.file.FileScanner;
import org.conqueror.common.utils.test.TestClass;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;


public class LocalFileScannerTest extends TestClass {

    private File testDir = getResourceFile("local-files");
    private File testFile = getResourceFile("local-files/local-file-test1.txt");

    private FileScanner scanner;
    private FileInfo dirInfo;
    private FileInfo fileInfo;

    @Before
    public void setUp() throws InterruptedException, IOException, URISyntaxException {
        scanner = new LocalFileScanner();
        dirInfo = scanner.makeFileInfo(testDir.toURI());
        fileInfo = scanner.makeFileInfo(testFile.toURI());
    }

    @Test
    public void test() throws URISyntaxException, IOException, InterruptedException {
        Assert.assertFalse(
            scanner.isDirectory(fileInfo)
        );
        Assert.assertTrue(
            scanner.isFile(fileInfo)
        );

        List<FileInfo> children = scanner.getChildren(dirInfo, 0);
        Assert.assertEquals(
            0
            , children.size()
        );

        children = scanner.getChildren(dirInfo, 1);
        Assert.assertEquals(
            2
            , children.size()
        );
        FileInfo child1 = children.get(0);
        FileInfo child2 = children.get(1);
        Assert.assertEquals(
            "local-file-test1.txt"
            , ((File) child1.getFile()).getName()
        );
        Assert.assertEquals(
            "local-file-test2.txt"
            , ((File) child2.getFile()).getName()
        );

        children = scanner.getChildren(dirInfo, "local[a-z\\-]+1.txt");
        Assert.assertEquals(
            1
            , children.size()
        );
        child1 = children.get(0);
        Assert.assertEquals(
            "local-file-test1.txt"
            , ((File) child1.getFile()).getName()
        );
        BufferedReader reader = scanner.getReader(fileInfo);
        String line1 = reader.readLine();
        Assert.assertEquals(
            "abcdefg"
            , line1
        );
        String line2 = reader.readLine();
        Assert.assertEquals(
            "1234567890"
            , line2
        );
        reader.close();

        scanner.close();
    }

    @After
    public void tearDown() throws Exception {
        dirInfo.close();
        fileInfo.close();
    }
}