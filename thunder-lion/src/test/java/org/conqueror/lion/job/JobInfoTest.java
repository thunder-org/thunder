package org.conqueror.lion.job;

import com.typesafe.config.Config;
import org.conqueror.common.utils.config.ConfigLoader;
import org.conqueror.common.utils.config.Loader;
import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;


public class JobInfoTest {

    @Test
    public void jobInfoSerializingTest() throws SerializableException {
        TestJobConfig config = (TestJobConfig) ConfigLoader.build(TestJobConfig.class, "G:\\workspace\\thunder\\thunder-lion\\src\\test\\resources\\testjob.conf");
        JobInfo jobInfo = new JobInfo("master-id", TestJob.class, config);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput dataOutput = new DataOutputStream(baos);
        jobInfo.writeObject(dataOutput);

        DataInput dataInput = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        JobInfo deserializedJobInfo = JobInfo.getEmptyJobInfo().readObject(dataInput);

        assertEquals(jobInfo, deserializedJobInfo);

        System.out.println(deserializedJobInfo);
    }

}