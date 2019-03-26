package org.conqueror.bird.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import org.conqueror.common.utils.config.ConfigLoader;
import org.conqueror.common.utils.file.FileUtils;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.common.exceptions.serialize.SerializableException;

import java.io.DataInput;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UpdateConfig extends JobConfig {

    /* Updater */
    private Map<String, String> schemas = new HashMap<>(10);
    private List<String> indexSubStrings = null;
    private int numberOfUpdaters = 0;
    private String wordsFile = null;
//	private UpdateInformation updateInfo = null;

    public UpdateConfig() {
        super();
    }

    public UpdateConfig(String configFile) {
        this(ConfigLoader.load(configFile));
    }

    protected UpdateConfig(Config config) {
        super(config);
        buildConfig(config);
    }

    private void buildConfig(Config config) {
        String confDir = getStringFromConfig(config, "client.conf-path", true);

        String schemaDir = getStringFromConfig(config, "client.schema-path", true);
        Config schemaConfig = getConfig(config, "client.update.schemas");
        for (Map.Entry<String, ConfigValue> entry : schemaConfig.entrySet()) {
            addSchema(entry.getKey(), FileUtils.getFileContent(schemaDir, (String) entry.getValue().unwrapped()));
        }

        /* for updater */
        setNumberOfUpdaters(getIntegerFromConfig(config, "client.update.updater-number", true));
        setIndexSubStrings(getStringListFromConfig(config, "client.update.index-substring", false));
        setWordsFile(getStringFromConfig(config, "client.update.word-file", true));

//		UpdateInformation updateInfo = new UpdateInformation();
//		updateInfo.setBulkSize(getIntegerFromConfig(config, "client.update.bulk-size", 1000));
//		updateInfo.setAliveTimes(getIntegerFromConfig(config, "client.update.alive-times", 60000));
//		updateInfo.setKeywordAnalyzerConfFile(getStringFromConfig(config, "client.update.word-extractor.configure", true));
//		updateInfo.setAddresses(getStringListFromConfig(config, "client.update.es.addresses", true));
//		updateInfo.setCluster(getStringFromConfig(config, "client.update.es.cluster", true));
//		updateInfo.setPingTimeOutSec(getIntegerFromConfig(config, "client.update.es.ping-timeout", 60));
//		updateInfo.setNodeSamplerIntervalSec(getIntegerFromConfig(config, "client.update.es.node-sampler-interval", 60));
//		updateInfo.setScrollFieldName(getStringFromConfig(config, "client.update.field-name", true));
//		updateInfo.setAnalysisFieldName(getStringFromConfig(config, "client.update.analysis-field-name", true));
//		updateInfo.setSchemaName(getStringFromConfig(config, "client.update.schema", true));
//		setUpdateInfo(updateInfo);
    }

    public void addSchema(String schemaName, String json) {
        schemas.put(schemaName, json);
    }

    public String getSchema(String schemaName) {
        return schemas.get(schemaName);
    }

    public List<String> getIndexSubStrings() {
        return indexSubStrings;
    }

    public boolean containIndexSubString(String indexName) {
        if (indexSubStrings == null) return true;

        for (String string : indexSubStrings) {
            if (indexName.contains(string)) return true;
        }

        return false;
    }

    public void setIndexSubStrings(List<String> indexSubStrings) {
        this.indexSubStrings = indexSubStrings;
    }

    public int getNumberOfUpdaters() {
        return numberOfUpdaters;
    }

    public void setNumberOfUpdaters(int numberOfUpdaters) {
        this.numberOfUpdaters = numberOfUpdaters;
    }

    public String getWordsFile() {
        return wordsFile;
    }

    public void setWordsFile(String wordsFile) {
        this.wordsFile = wordsFile;
    }

//	public UpdateInformation getUpdateInfo() {
//		return updateInfo;
//	}

//	public void setUpdateInfo(UpdateInformation updateInfo) {
//		this.updateInfo = updateInfo;
//	}

    @Override
    public UpdateConfig readObject(DataInput input) throws SerializableException {
        try {
            return new UpdateConfig(ConfigFactory.parseString(input.readUTF()));
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }
}
