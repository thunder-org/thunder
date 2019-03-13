package org.conqueror.common.utils.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.conqueror.common.exceptions.ThunderConfigException;
import org.conqueror.common.utils.file.FileUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;


/*
    load config of typesafe style and trasform to Configuration class
    utility class
 */
public class ConfigLoader {

    private ConfigLoader() {
    }

    public static Config load(File confFile) {
        return ConfigFactory.parseFile(confFile).resolve();
    }

    public static Config load(String confFilePath) {
        String confDir = FileUtils.getDirectoryPath(confFilePath);
        if (confDir != null) return load(new File(confFilePath));

        String baseNameOfConfigFile = confFilePath;

        int idx = baseNameOfConfigFile.lastIndexOf('.');
        if (idx != -1) {
            baseNameOfConfigFile = baseNameOfConfigFile.substring(0, idx);
        }

        return ConfigFactory.load(baseNameOfConfigFile);
    }

    public static Config parse(String config) {
        return ConfigFactory.parseString(config);
    }

    public static Configuration build(Class<? extends Configuration> configurationClass, String configFilePath) {
        try {
            return configurationClass.getConstructor(Config.class).newInstance(load(configFilePath));
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ThunderConfigException(e);
        }
    }

    public static Configuration build(Class<? extends Configuration> configurationClass, File configFile) {
        try {
            return configurationClass.getConstructor(Config.class).newInstance(load(configFile));
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ThunderConfigException(e);
        }
    }

    public static Configuration build(Class<? extends Configuration> configurationClass, Config config) {
        try {
            return configurationClass.getConstructor(Config.class).newInstance(config);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ThunderConfigException(e);
        }
    }

    public static Configuration build(Class<? extends Configuration> configurationClass, Object... args) {
        try {
            return configurationClass.getConstructor(args.getClass()).newInstance(args);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ThunderConfigException(e);
        }
    }

}
