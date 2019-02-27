package org.conqueror.cat.config;

import com.typesafe.config.Config;
import org.conqueror.common.utils.config.ConfigLoader;
import org.conqueror.common.utils.config.Configuration;
import org.conqueror.common.utils.config.Loader;
import org.conqueror.common.utils.file.FileUtils;

import java.util.Objects;


public class KomoranConfig extends Configuration {

    private String fwDicPath = null;
    private String userDicPath = null;
    private String modelDirPath = null;

    public KomoranConfig(String configFile) {
        this(ConfigLoader.load(configFile));
    }

    public KomoranConfig(Config config) {
        buildConfig(config);
    }

    private void buildConfig(Config config) {
        setFwDicPath(Objects.requireNonNull(getFileURLFromConfig(config, "dic.fw.path", true)).getPath());
        setUserDicPath(Objects.requireNonNull(getFileURLFromConfig(config, "dic.user.path", true)).getPath());
        setModelDirPath(Objects.requireNonNull(getFileURLFromConfig(config, "model.dir.path", true)).getPath());
    }

    public String getFwDicPath() {
        return fwDicPath;
    }

    public void setFwDicPath(String fwDicPath) {
        this.fwDicPath = fwDicPath;
    }

    public String getUserDicPath() {
        return userDicPath;
    }

    public void setUserDicPath(String userDicPath) {
        this.userDicPath = userDicPath;
    }

    public String getModelDirPath() {
        return modelDirPath;
    }

    public void setModelDirPath(String modelDirPath) {
        this.modelDirPath = modelDirPath;
    }

}
