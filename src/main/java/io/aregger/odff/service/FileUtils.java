package io.aregger.odff.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

public final class FileUtils {

    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private FileUtils() {
        throw new AssertionError("Non-instantiable class");
    }

    public static File createFile(Path directory, String fileName) throws IOException {
        File file = resolveFile(directory, fileName);
        boolean fileExists = !file.createNewFile();
        if (fileExists) {
            String msg = String.format("File %s already exists. Please rename it and try again.", file);
            logger.error(msg);
            throw new FileAlreadyExistsException(msg);
        }
        return file;
    }

    private static File resolveFile(Path directory, String fileName) {
        return directory.resolve(fileName).toFile();
    }
}
