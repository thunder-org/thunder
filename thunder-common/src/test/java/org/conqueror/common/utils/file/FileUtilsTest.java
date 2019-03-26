package org.conqueror.common.utils.file;

import org.conqueror.common.utils.test.TestClass;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;


public class FileUtilsTest extends TestClass {

    private File testFile = getResourceFile("local-files/local-file-test1.txt");
    private File writeTestFile = getResourceFile("local-files/write_test.txt");
    private String testDirPath = testFile.getParentFile().getAbsolutePath();
    private String testFilePath = testFile.getAbsolutePath();
    private String testFileName = testFile.getName();
    private URL testFileURL = testFile.toURI().toURL();

    public FileUtilsTest() throws MalformedURLException {
    }

    @Before
    public void setUp() throws Exception {
        if (writeTestFile.exists()) {
            if (!writeTestFile.delete()) {
                throw new IOException("failed to delete file : " + writeTestFile.getAbsolutePath());
            }
        }
        if (!writeTestFile.createNewFile()) {
            throw new IOException("failed to create file : " + writeTestFile.getAbsolutePath());
        }
    }

    @Test
    public void test() throws IOException {
        String fileContent = "abcdefg\r\n1234567890";

        List<String> testFileContentLines = new ArrayList<>(2);
        testFileContentLines.add("abcdefg");
        testFileContentLines.add("1234567890");

        List<String> writeTestFileContentLines = new ArrayList<>(2);
        writeTestFileContentLines.add("abcdefg");
        writeTestFileContentLines.add("1234567890");
        writeTestFileContentLines.add("hijklmnop");
        writeTestFileContentLines.add("0987654321");

        List<String> appendTestFileContentLines = new ArrayList<>(2);
        appendTestFileContentLines.add("hijklmnop");
        appendTestFileContentLines.add("0987654321");

        Assert.assertEquals(
            fileContent
            , FileUtils.getFileContent(testFilePath)
        );
        Assert.assertEquals(
            fileContent
            , FileUtils.getFileContent(testFilePath, Charset.forName("utf-8"))
        );
        Assert.assertEquals(
            fileContent
            , FileUtils.getFileContent(testDirPath, testFileName)
        );
        Assert.assertEquals(
            fileContent
            , FileUtils.getFileContent(testDirPath, testFileName, Charset.forName("utf-8"))
        );
        Assert.assertEquals(
            fileContent
            , FileUtils.getFileContent(new FileReader(testFile))
        );
        Assert.assertEquals(
            testDirPath
            , FileUtils.getDirectoryPath(testFilePath)
        );
        Assert.assertEquals(
            testFileName
            , FileUtils.getFileName(testFilePath)
        );
        Assert.assertThat(
            FileUtils.makeLinesToList(testFilePath)
            , is(testFileContentLines)
        );
        Assert.assertThat(
            FileUtils.makeLinesToList(testFilePath, Charset.forName("utf-8"))
            , is(testFileContentLines)
        );
        Assert.assertEquals(
            testFileURL
            , FileUtils.toURL(testFilePath)
        );
        FileUtils.appendTextLine(writeTestFile.getAbsolutePath(), "abcdefg");
        FileUtils.appendTextLine(writeTestFile.getAbsolutePath(), "1234567890");
        Assert.assertThat(
            FileUtils.makeLinesToList(writeTestFile.getAbsolutePath())
            , is(testFileContentLines)
        );
        FileUtils.writeList(writeTestFile.getAbsolutePath(), appendTestFileContentLines, true);
        Assert.assertThat(
            FileUtils.makeLinesToList(writeTestFile.getAbsolutePath())
            , is(writeTestFileContentLines)
        );
        FileUtils.writeList(writeTestFile.getAbsolutePath(), writeTestFileContentLines, false);
        Assert.assertThat(
            FileUtils.makeLinesToList(writeTestFile.getAbsolutePath())
            , is(writeTestFileContentLines)
        );
    }

    @Test
    public void createDirctory() throws IOException {
        Path path = Paths.get(getTargetDirectory().getAbsolutePath(), "a", "b", "c");
        FileUtils.createDirectory(getTargetDirectory().getAbsolutePath(), "a", "b", "c");
        Assert.assertTrue(Files.exists(path));
        path = Paths.get(getTargetDirectory().getAbsolutePath(), "a", "b", "c");
        Files.delete(path);
        path = Paths.get(getTargetDirectory().getAbsolutePath(), "a", "b");
        Files.delete(path);
        path = Paths.get(getTargetDirectory().getAbsolutePath(), "a");
        Files.delete(path);
    }

    @After
    public void tearDown() throws Exception {
        if (writeTestFile.exists()) {
            if (!writeTestFile.delete()) {
                throw new IOException("failed to delete file : " + writeTestFile.getAbsolutePath());
            }
        }
    }

}
