package org.conqueror.lion.serialize;

import org.conqueror.common.exceptions.serialize.SerializableException;
import org.junit.Test;

import java.io.*;


public class LionSerializerTest {

    @Test
    public void toBinary() throws IOException, SerializableException, ClassNotFoundException {
        /*
        TestJobConfig config = new TestJobConfig("G:\\workspace\\thunder\\thunder-lion\\src\\main\\resources\\test-job.conf");
        LionSerializer serializer = new LionSerializer();
        byte[] bytes = serializer.toBinary(config);
        TestJobConfig reco = (TestJobConfig) serializer.fromBinaryJava(bytes, TestJobConfig.class);

        SerializableObject obj = new SerializableObject("ha");
        byte[] bytes1 = serializer.toBinary(obj);
        SerializableObject recoObj = (SerializableObject) serializer.fromBinaryJava(bytes1, SerializableObject.class);



        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        ((ThunderSerializable) obj).writeObject(out);
//        out.writeUTF("aa");
        out.close();
        byte[] bytes2 = baos.toByteArray();
        System.out.println(bytes2.length);

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes2);
        ObjectInputStream in = new ObjectInputStream(bais);
//        in.readUTF();
        SerializableObject newObj = new SerializableObject().readObject(in);
        in.close();
        */
    }

    @Test
    public void fromBinaryJava() {
    }

}