package org.conqueror.common.utils.test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


public abstract class TestClass {

    protected Path resourceDirectory = Paths.get("src", "test", "resources");

    protected Path targetDirectory = Paths.get("target");

    protected File getResourceFile(String fileName) {
        return new File(resourceDirectory.toFile(), fileName);
    }

    protected File getTargetFile(String fileName) {
        return new File(targetDirectory.toFile(), fileName);
    }

    protected File getResourceDirectory() {
        return resourceDirectory.toFile();
    }

    protected File getTargetDirectory() {
        return targetDirectory.toFile();
    }

}
