package org.conqueror.common.utils.config;

import java.net.URL;


public class Loader {

    public static URL getResource(String resource) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (classLoader == null) classLoader = Loader.class.getClassLoader();

        URL url = classLoader.getResource(resource);
        if (url != null) return url;

        return ClassLoader.getSystemResource(resource);
    }

}
