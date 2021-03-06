package org.conqueror.bird.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import org.conqueror.bird.index.IndexInformation;
import org.conqueror.common.exceptions.serialize.SerializableException;
import org.conqueror.common.utils.config.ConfigLoader;
import org.conqueror.common.utils.file.FileUtils;
import org.conqueror.lion.config.JobConfig;

import java.io.DataInput;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IndexConfig extends JobConfig<IndexConfig> {

    private static final String NODE_CONF_PATH = "node";

    /* Gate */
    private Map<String, String> schemas = new HashMap<>(10);
    private List<String> destFileList = null;
    private int numberOfSources = 0;

    /* Analyzer */
    private String analyzerConfFilePath = null;
    private int numberOfAnalyzers = 0;

    /* Indexer */
    private int indexNameMaxSize = 20;
    private int bulkSize = 10;
    private int numberOfIndexers = 0;
    private IndexInformation indexInfo = null;

    public IndexConfig() {
        super();
    }

    public IndexConfig(String configFile) {
        this(ConfigLoader.load(configFile));
    }

    public IndexConfig(Config nodeConfig, String json) {
        this(ConfigLoader.merge(nodeConfig.getConfig(NODE_CONF_PATH), ConfigLoader.parseJson(json)));
    }

    public IndexConfig(Config config) {
        super(config);
        buildConfig(config);
    }

    private void buildConfig(Config config) {

        /* for gate */
        setDestFileList(getStringListFromConfig(config, "job.gate.input-file-list", true));
        setNumberOfSources(getIntegerFromConfig(config, "job.gate.source-number", true));
        String schemaDir = getStringFromConfig(config, "job.common.schema-path", true);
        Config schemaConfig = getConfig(config, "job.common.schemas");
        for (Map.Entry<String, ConfigValue> entry : schemaConfig.entrySet()) {
            addSchema(entry.getKey(), FileUtils.getFileContent(schemaDir, (String) entry.getValue().unwrapped()));
        }

        /* for analysis */
        setAnalyzerConfFilePath(getStringFromConfig(config, "job.common.analyzer-path", true));
        setNumberOfAnalyzers(getIntegerFromConfig(config, "job.analysis.analyzer.number", true));

        /* for indexer */
        setIndexNameMaxSize(getIntegerFromConfig(config, "job.index.name-max-size", true));
        setBulkSize(getIntegerFromConfig(config, "job.index.bulk-size", true));
        setNumberOfIndexers(getIntegerFromConfig(config, "job.index.indexer-number", true));

        IndexInformation indexInfo = new IndexInformation();
        indexInfo.setAddresses(getStringListFromConfig(config, "job.index.es.addresses", true));
        indexInfo.setCluster(getStringFromConfig(config, "job.index.es.cluster", true));
        indexInfo.setParentMappingName(getStringFromConfig(config, "job.index.es.parent-mapping-name", true));
        indexInfo.setChildMappingName(getStringFromConfig(config, "job.index.es.child-mapping-name", false));
        indexInfo.setPingTimeOutSec(getIntegerFromConfig(config, "job.index.es.ping-timeout", 60));
        indexInfo.setNodeSamplerIntervalSec(getIntegerFromConfig(config, "job.index.es.node-sampler-interval", 60));
        indexInfo.setRequestRetries(getIntegerFromConfig(config, "job.index.es.request-retries", 0));
        String mappingDir = getStringFromConfig(config, "job.common.mapping-path", true);
        Config mappingConfig = getConfig(config, "job.common.mappings");
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

    public int getNumberOfSources() {
        return numberOfSources;
    }

    public void setNumberOfSources(int numberOfSources) {
        this.numberOfSources = numberOfSources;
    }

    public int getNumberOfAnalyzers() {
        return numberOfAnalyzers;
    }

    public void setNumberOfAnalyzers(int numberOfAnalyzers) {
        this.numberOfAnalyzers = numberOfAnalyzers;
    }

    public String getAnalyzerConfFilePath() {
        return analyzerConfFilePath;
    }

    public void setAnalyzerConfFilePath(String analyzerConfFilePath) {
        this.analyzerConfFilePath = analyzerConfFilePath;
    }

    public int getIndexNameMaxSize() {
        return indexNameMaxSize;
    }

    public void setIndexNameMaxSize(int indexNameMaxSize) {
        this.indexNameMaxSize = indexNameMaxSize;
    }

    public int getBulkSize() {
        return bulkSize;
    }

    public void setBulkSize(int bulkSize) {
        this.bulkSize = bulkSize;
    }

    public int getNumberOfIndexers() {
        return numberOfIndexers;
    }

    public void setNumberOfIndexers(int numberOfIndexers) {
        this.numberOfIndexers = numberOfIndexers;
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
