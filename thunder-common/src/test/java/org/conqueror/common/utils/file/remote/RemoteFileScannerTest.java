package org.conqueror.common.utils.file.remote;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.Selectors;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellCommandFactory;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;


public class RemoteFileScannerTest extends TestClass {

    private final File testDir = getResourceFile("local-files");
    private final File testFile1 = getResourceFile("local-files/local-file-test1.txt");
    private final File testFile2 = getResourceFile("local-files/local-file-test2.txt");
    private FileInfo dirInfo;
    private FileInfo file1Info;
    private FileInfo file2Info;

    private SshServer sshd;

    private final String server = "localhost";
    private final String remoteDirPath = "/" + testDir.getName();

    private final URI remoteDirURI = new URI("sftp://" + MyPasswordAuthenticator.USER + ':' + MyPasswordAuthenticator.PASSWORD + '@' + server + ":22" + remoteDirPath);
    private final URI remoteFile1URI = new URI("sftp://" + MyPasswordAuthenticator.USER + ':' + MyPasswordAuthenticator.PASSWORD + '@' + server + ":22" + remoteDirPath + '/' + testFile1.getName());
    private final URI remoteFile2URI = new URI("sftp://" + MyPasswordAuthenticator.USER + ':' + MyPasswordAuthenticator.PASSWORD + '@' + server + ":22" + remoteDirPath + '/' + testFile2.getName());

    public RemoteFileScannerTest() throws URISyntaxException {
    }


    @Before
    public void setUp() throws Exception {
        setupSSHServer();
    }

    @Test
    public void test() throws IOException, URISyntaxException, InterruptedException {
        FileScanner scanner = new RemoteFileScanner(3);

        dirInfo = scanner.makeFileInfo(remoteDirURI);
        FileObject dirFileObj = (FileObject) dirInfo.getFile();
        if (!dirFileObj.exists()) {
            dirFileObj.createFolder();
        }

        file1Info = scanner.makeFileInfo(remoteFile1URI);
        FileObject fileFileObj = (FileObject) file1Info.getFile();
        if (!fileFileObj.exists()) {
            fileFileObj.copyFrom((FileObject) scanner.makeFileInfo(testFile1.toURI()).getFile(), Selectors.SELECT_SELF);
        }

        file2Info = scanner.makeFileInfo(remoteFile2URI);
        fileFileObj = (FileObject) file2Info.getFile();
        if (!fileFileObj.exists()) {
            fileFileObj.copyFrom((FileObject) scanner.makeFileInfo(testFile2.toURI()).getFile(), Selectors.SELECT_SELF);
        }

        Assert.assertTrue(
            scanner.isDirectory(dirInfo)
        );
        Assert.assertTrue(
            scanner.isFile(file1Info)
        );
        Assert.assertTrue(
            scanner.isFile(file2Info)
        );

        List<FileInfo> children = scanner.getChildren(dirInfo, 1);
        Assert.assertEquals(
            2
            , children.size()
        );
        FileInfo child1 = children.get(0);
        FileInfo child2 = children.get(1);
        Assert.assertEquals(
            "local-file-test1.txt"
            , ((FileObject) child1.getFile()).getName().getBaseName()
        );
        Assert.assertEquals(
            "local-file-test2.txt"
            , ((FileObject) child2.getFile()).getName().getBaseName()
        );

        children = scanner.getChildren(dirInfo, "local[a-z\\-]+1.txt");
        Assert.assertEquals(
            1
            , children.size()
        );
        child1 = children.get(0);
        Assert.assertEquals(
            "local-file-test1.txt"
            , ((FileObject) child1.getFile()).getName().getBaseName()
        );
        BufferedReader reader = scanner.getReader(file1Info);
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
        file1Info.close();
        file2Info.close();

        sshd.close();
    }

    private void setupSSHServer() throws IOException {
        // Init sftp server stuff
        sshd = SshServer.setUpDefaultServer();
        sshd.setPasswordAuthenticator(new MyPasswordAuthenticator());
        sshd.setPublickeyAuthenticator(new MyPublicKeyAuthenticator());
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
        sshd.setCommandFactory(new ProcessShellCommandFactory());
        sshd.setPort(22);

        sshd.start();
    }

}