package org.conqueror.common.utils.file.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.conqueror.common.utils.file.FileInfo;
import org.conqueror.common.utils.test.TestClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;


public class HdfsFileScannerTest extends TestClass {

    private final File testDir = getResourceFile("local-files");
    private final File testFile1 = getResourceFile("local-files/local-file-test1.txt");
    private final File testFile2 = getResourceFile("local-files/local-file-test2.txt");
    private FileInfo dirInfo;
    private FileInfo file1Info;
    private FileInfo file2Info;

//    private final URI remoteDirURI = new URI("hdfs://" + MyPasswordAuthenticator.USER + ':' + MyPasswordAuthenticator.PASSWORD + '@' + server + ":22" + remoteDirPath);
//    private final URI remoteFile1URI = new URI("hdfs://" + MyPasswordAuthenticator.USER + ':' + MyPasswordAuthenticator.PASSWORD + '@' + server + ":22" + remoteDirPath + '/' + testFile1.getName());
//    private final URI remoteFile2URI = new URI("hdfs://" + MyPasswordAuthenticator.USER + ':' + MyPasswordAuthenticator.PASSWORD + '@' + server + ":22" + remoteDirPath + '/' + testFile2.getName());

    @Before
    public void setUp() throws Exception {
        File baseDir = getTargetFile("hdfs");
        FileUtil.fullyDelete(baseDir);

        Configuration conf = new Configuration();
        conf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, baseDir.getAbsolutePath());

//        MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(conf);
//        MiniDFSCluster hdfsCluster = builder.build();
//        hdfsCluster.getFileSystem().copyFromLocalFile(new Path(testFile1.getAbsolutePath()), new Path("hdfs://localhost:"+ hdfsCluster.getNameNodePort() + "/"));
//        String hdfsURI = "hdfs://localhost:"+ hdfsCluster.getNameNodePort() + "/";
    }

    @Test
    public void test() {

    }

    @After
    public void tearDown() throws Exception {
    }

}