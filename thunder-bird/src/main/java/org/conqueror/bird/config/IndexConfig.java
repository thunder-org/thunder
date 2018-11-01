package org.conqueror.bird.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import org.conqueror.bird.index.IndexInformation;
import org.conqueror.common.utils.config.ConfigLoader;
import org.conqueror.common.utils.file.FileUtils;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.exceptions.Serialize.SerializableException;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IndexConfig extends JobConfig<IndexConfig> {

    /* Gate */
    private Map<String, String> schemas = new HashMap<>(10);
    private List<String> destFileList = null;
    private int numberOfScanners = 0;
    private int numberOfSources = 0;

    /* Analyzer */
    private int documentQueueSize = 0;
    private int numberOfAnalyzers = 0;
    private String analyzerConfFilePath = null;

    /* Indexer */
    private int contentQueueSize = 0;
    private int capacity = 0;
    private int initContentQueuePoolSize = 0;
    private int numberOfExecutors = 0;
    private IndexInformation indexInfo = null;

    public IndexConfig() {
        super();
    }

    public IndexConfig(String configFile) {
        this(ConfigLoader.load(configFile));
    }

    public IndexConfig(Config config) {
        super(config);
        buildConfig(config);
    }

    private void buildConfig(Config config) {
        String confDir = getStringFromConfig(config, "conf-path", true);
        String schemaDir = getStringFromConfig(config, "job.schema-path", true);
        Config schemaConfig = getConfig(config, "job.gate.schemas");
        String mappingDir = getStringFromConfig(config, "job.mapping-path", true);
        Config mappingConfig = getConfig(config, "job.index.es.mappings");
        for (Map.Entry<String, ConfigValue> entry : schemaConfig.entrySet()) {
            addSchema(entry.getKey(), FileUtils.getFileContent(schemaDir, (String) entry.getValue().unwrapped()));
        }

        /* for gate */
        setDestFileList(getStringListFromConfig(config, "job.gate.input-file-list", true));
        setNumberOfScanners(getIntegerFromConfig(config, "job.gate.scanner-number", true));
        setNumberOfSources(getIntegerFromConfig(config, "job.gate.source-number", true));


        /* for analyzer */
//        setDocumentQueueSize(getIntegerFromConfig(config, "client.analysis.document-queue-size", true));
//        setNumberOfAnalyzers(getIntegerFromConfig(config, "client.analysis.analyzer-number", true));
//        setAnalyzerConfFilePath(confDir + File.separator + getStringFromConfig(config, "client.analysis.word-analyzer.configure", true));

        /* for indexer */
//        setContentQueueSize(getIntegerFromConfig(config, "client.index.content-queue-size", true));
//        setCapacity(getIntegerFromConfig(config, "client.index.bulk-size", true));
//        setInitContentQueuePoolSize(getIntegerFromConfig(config, "client.index.content-queue-pool-size", true));
//        setNumberOfExecutors(getIntegerFromConfig(config, "client.index.executor-number", true));

        IndexInformation indexInfo = new IndexInformation();
//        indexInfo.setAddresses(getStringListFromConfig(config, "client.index.es.addresses", true));
//        indexInfo.setCluster(getStringFromConfig(config, "client.index.es.cluster", true));
//        indexInfo.setParentMappingName(getStringFromConfig(config, "client.index.es.parent-mapping-name", true));
//        indexInfo.setChildMappingName(getStringFromConfig(config, "client.index.es.child-mapping-name", true));
//        indexInfo.setPingTimeOutSec(getIntegerFromConfig(config, "client.index.es.ping-timeout", 60));
//        indexInfo.setNodeSamplerIntervalSec(getIntegerFromConfig(config, "client.index.es.node-sampler-interval", 60));
        for (Map.Entry<String, ConfigValue> entry : mappingConfig.entrySet()) {
            indexInfo.addMappingJson(entry.getKey(), FileUtils.getFileContent(mappingDir, (String) entry.getValue().unwrapped()));
        }
        setIndexInfo(indexInfo);
    }

    public void addSchema(String schemaName, String json) {
        schemas.put(schemaName, json);
    }

    public String getSchema(String schemaName) {
        return schemas.get(schemaName);
    }

    public List<String> getDestFileList() {
        return destFileList;
    }

    public void setDestFileList(List<String> destFileList) {
        this.destFileList = destFileList;
    }

    public int getNumberOfScanners() {
        return numberOfScanners;
    }

    public void setNumberOfScanners(int numberOfScanners) {
        this.numberOfScanners = numberOfScanners;
    }

    public int getNumberOfSources() {
        return numberOfSources;
    }

    public void setNumberOfSources(int numberOfSources) {
        this.numberOfSources = numberOfSources;
    }

    public int getDocumentQueueSize() {
        return documentQueueSize;
    }

    public int getNumberOfAnalyzers() {
        return numberOfAnalyzers;
    }

    public void setNumberOfAnalyzers(int numberOfAnalyzers) {
        this.numberOfAnalyzers = numberOfAnalyzers;
    }

    public void setDocumentQueueSize(int documentQueueSize) {
        this.documentQueueSize = documentQueueSize;
    }

    public String getAnalyzerConfFilePath() {
        return analyzerConfFilePath;
    }

    public void setAnalyzerConfFilePath(String analyzerConfFilePath) {
        this.analyzerConfFilePath = analyzerConfFilePath;
    }

    public int getContentQueueSize() {
        return contentQueueSize;
    }

    public void setContentQueueSize(int contentQueueSize) {
        this.contentQueueSize = contentQueueSize;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getInitContentQueuePoolSize() {
        return initContentQueuePoolSize;
    }

    public void setInitContentQueuePoolSize(int initContentQueuePoolSize) {
        this.initContentQueuePoolSize = initContentQueuePoolSize;
    }

    public int getNumberOfExecutors() {
        return numberOfExecutors;
    }

    public void setNumberOfExecutors(int numberOfExecutors) {
        this.numberOfExecutors = numberOfExecutors;
    }

    public IndexInformation getIndexInfo() {
        return indexInfo;
    }

    public void setIndexInfo(IndexInformation indexInfo) {
        this.indexInfo = indexInfo;
    }

    @Override
    public IndexConfig readObject(DataInput input) throws SerializableException {
        try {
            return new IndexConfig(ConfigFactory.parseString(input.readUTF()));
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
