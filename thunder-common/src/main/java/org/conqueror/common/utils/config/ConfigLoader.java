package org.conqueror.common.utils.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import jodd.util.ClassLoaderUtil;
import org.conqueror.common.exceptions.ThunderConfigException;
import org.conqueror.common.utils.file.FileUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;


public class ConfigLoader {

    public static Config load(String confFilePath) {
        String confDir = FileUtils.getDirectoryPath(confFilePath);
        String baseNameOfConfigFile;
        if (confDir != null) {
            baseNameOfConfigFile = FileUtils.getFileName(confFilePath);
        } else {
            confDir = Paths.get("../conf").toAbsolutePath().normalize().toString();
            baseNameOfConfigFile = confFilePath;
        }

        int idx = baseNameOfConfigFile.lastIndexOf('.');
        if (idx != -1) {
            baseNameOfConfigFile = baseNameOfConfigFile.substring(0, idx);
        }

        ClassLoaderUtil.addFileToClassPath(new File(confDir), Thread.currentThread().getContextClassLoader());

        return ConfigFactory.load(baseNameOfConfigFile);
    }

    public static Configuration build(Class<? extends Configuration> configurationClass, String configFilePath) {
        try {
            return configurationClass.getConstructor(Config.class).newInstance(load(configFilePath));
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
