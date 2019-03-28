package org.conqueror.bird.config;

import org.conqueror.common.utils.test.TestClass;
import org.conqueror.lion.config.NodeConfig;
import org.junit.Assert;
import org.junit.Test;


public class IndexConfigTest extends TestClass {

    @Test
    public void test() {
        NodeConfig nodeConfig = new NodeConfig(getResourceFile("node1.conf"));
        String jsonConfig = "{\n" +
            "    \"job\" : {\n" +
            "        \"name\" : \"index-test\"\n" +
            "        , \"id\" : 1\n" +
            "        , \"schedule\" : \"0\"\n" +
            "        , \"task\" : {\n" +
            "            \"manager.number\" : 1\n" +
            "            , \"worker.number\" : 1\n" +
            "            , \"wating-time-sec\" : 3\n" +
            "        }\n" +
            "        , \"job-manager\" : {\n" +
            "            \"class\" : \"org.conqueror.bird.job.IndexJobManager\"\n" +
            "        }\n" +
            "        , \"task-manager\" : {\n" +
            "            \"class\" : \"org.conqueror.bird.task.IndexTaskManager\"\n" +
            "        }\n" +
            "        , \"task-worker\" : {\n" +
            "            \"class\" : \"org.conqueror.bird.task.IndexTaskWorker\"\n" +
            "        }\n" +
            "        , \"node-master\" : {\n" +
            "            \"urls\" : [\"akka.tcp://ThunderBirdWorker-Master@localhost:2551\"]\n" +
            "        }\n" +
            "        , \"ask\" : {\n" +
            "            \"timeout\" : 10\n" +
            "        }\n" +
            "        , \"gate\" : {\n" +
            "            \"input-file-list\" : [\n" +
            "                \"file;json-array;movie;movie;file:///G:/workspace/data/raw/@regexp{[0-9]+.json}\"\n" +
            "            ]\n" +
            "            , \"source-number\" : 1\n" +
            "        }\n" +
            "        , \"analysis\" : {\n" +
            "            \"analyzer\" : {\n" +
            "                \"number\" : 1\n" +
            "            }\n" +
            "        }\n" +
            "        , \"index\" : {\n" +
            "            \"name-max-size\" : 20\n" +
            "            , \"content-queue-size\" : 100\n" +
            "            , \"indexer-number\" : 10\n" +
            "            , \"bulk-size\" : 100\n" +
            "            , \"es\" : {\n" +
            "                \"addresses\" : [\"192.168.203.118:9300\"]\n" +
            "                , \"cluster\" : \"my-application\"\n" +
            "                , \"parent-mapping-name\" : \"DOCUMENT\"\n" +
            "                , \"ping-timeout\" : 120\n" +
            "                , \"node-sampler-interval\" : 5\n" +
            "                , \"request-retries\" : 3\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";
        System.out.println(jsonConfig);
        IndexConfig indexConfig = new IndexConfig(nodeConfig.getConfig(), jsonConfig);
        Assert.assertEquals("index-test", indexConfig.getName());
    }
}