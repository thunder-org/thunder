package org.conqueror.common.utils.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import jodd.util.ClassLoaderUtil;
import org.conqueror.common.exceptions.ThunderConfigException;
import org.conqueror.common.utils.file.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Objects;


@SuppressWarnings("unused")
public abstract class Configuration implements Serializable {

    private enum ValueType {STRING, INTEGER, LONG, DOUBLE, STRING_LIST, DATETIME, BOOLEAN}

    protected void loadClassPath(List<String> resourcePaths) {
        for (String path : resourcePaths) {
            loadClassPath(path);
        }
    }

    protected void loadClassPath(String resourcePath) {
        ClassLoaderUtil.addFileToClassPath(new File(resourcePath), Thread.currentThread().getContextClassLoader());
    }

    public static String toListString(List<String> list) {
        StringBuilder listString = new StringBuilder();
        list.forEach(str -> {
            if (listString.length() > 0) listString.append(',');
            listString.append(str);
        });

        return listString.toString();
    }

    protected static Config getConfig(Config config, String path) {
        return config.getConfig(path);
    }

    protected static String getStringFromConfig(Config config, String path, String defaultValue) {
        return getValueFromConfig(config, path, ValueType.STRING, defaultValue);
    }

    protected static String getStringFromConfig(Config config, String path, boolean notNull) {
        return getValueFromConfig(config, path, ValueType.STRING, notNull);
    }

    protected static Integer getIntegerFromConfig(Config config, String path, Integer defaultValue) {
        return getValueFromConfig(config, path, ValueType.INTEGER, defaultValue);
    }

    protected static Integer getIntegerFromConfig(Config config, String path, boolean notNull) {
        return getValueFromConfig(config, path, ValueType.INTEGER, notNull);
    }

    protected static Long getLongFromConfig(Config config, String path, Long defaultValue) {
        return getValueFromConfig(config, path, ValueType.LONG, defaultValue);
    }

    protected static Long getLongFromConfig(Config config, String path, boolean notNull) {
        return getValueFromConfig(config, path, ValueType.LONG, notNull);
    }

    protected static Double getDoubleFromConfig(Config config, String path, Double defaultValue) {
        return getValueFromConfig(config, path, ValueType.DOUBLE, defaultValue);
    }

    protected static Double getDoubleFromConfig(Config config, String path, boolean notNull) {
        return getValueFromConfig(config, path, ValueType.DOUBLE, notNull);
    }

    protected static List<String> getStringListFromConfig(Config config, String path, List<String> defaultValue) {
        return getValueFromConfig(config, path, ValueType.STRING_LIST, defaultValue);
    }

    protected static List<String> getStringListFromConfig(Config config, String path, boolean notNull) {
        return getValueFromConfig(config, path, ValueType.STRING_LIST, notNull);
    }

    protected static DateTime getDateTimeFromConfig(Config config, String path, DateTimeFormatter dateFormatter, DateTime defaultValue) {
        DateTime value = getDateTimeFromConfig(config, path, dateFormatter, false);
        return (value == null) ? defaultValue : value;
    }

    protected static DateTime getDateTimeFromConfig(Config config, String path, DateTimeFormatter dateFormatter, boolean notNull) {
        DateTime value = null;
        try {
            if (config.hasPath(path)) {
                String stringValue;
                stringValue = config.getString(path).trim();
                value = (stringValue.length() == 0) ? null : dateFormatter.parseDateTime(stringValue);
            }
        } catch (ConfigException.WrongType | IllegalArgumentException e) {
            throw new ThunderConfigException.WrongTypeValue(path);
        } catch (ConfigException.BadPath e) {
            throw new ThunderConfigException.WrongPathOrNullValue(path);
        }

        if (notNull && value == null) throw new ThunderConfigException.WrongPathOrNullValue(path);

        return value;
    }

    protected static Boolean getBooleanFromConfig(Config config, String path, Boolean defaultValue) {
        return getValueFromConfig(config, path, ValueType.BOOLEAN, defaultValue);
    }

    protected static Boolean getBooleanFromConfig(Config config, String path, boolean notNull) {
        return getValueFromConfig(config, path, ValueType.BOOLEAN, notNull);
    }

    protected static URL getFileURLFromConfig(Config config, String path, boolean notNull) {
        String file = getStringFromConfig(config, path, notNull);
        if (FileUtils.getDirectoryPath(file) != null) {
            return FileUtils.toURL(file);
        }
        return Loader.getResource(file);
    }

    protected static boolean hasStringValue(Config config, String path) {
        return Objects.nonNull(getStringFromConfig(config, path, false));
    }

    protected static boolean hasIntegerValue(Config config, String path) {
        return Objects.nonNull(getIntegerFromConfig(config, path, false));
    }

    protected static boolean hasStringListValue(Config config, String path) {
        return Objects.nonNull(getStringListFromConfig(config, path, false));
    }

    private static <T> T getValueFromConfig(Config config, String path, ValueType type, T defaultValue) {
        T value = getValueFromConfig(config, path, type, false);
        return value == null ? defaultValue : value;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getValueFromConfig(Config config, String path, ValueType type, boolean notNull) {
        T value = null;

        try {
            if (config.hasPath(path)) {
                switch (type) {
                    case STRING:
                        value = (T) config.getString(path);
                        break;
                    case INTEGER:
                        value = (T) Integer.valueOf(config.getInt(path));
                        break;
                    case LONG:
                        value = (T) Long.valueOf(config.getLong(path));
                        break;
                    case DOUBLE:
                        value = (T) Double.valueOf(config.getDouble(path));
                        break;
                    case STRING_LIST:
                        value = (T) config.getStringList(path);
                        break;
                    case BOOLEAN:
                        value = (T) Boolean.valueOf(config.getBoolean(path));
                        break;
                    default:
                }
            }
        } catch (ConfigException.BadPath e) {
            throw new ThunderConfigException.WrongPathOrNullValue(path);
        } catch (ConfigException.WrongType e) {
            throw new ThunderConfigException.WrongTypeValue(path);
        }

        if (notNull && value == null) throw new ThunderConfigException.WrongPathOrNullValue(path);

        return value;
    }

}
