package io.aregger.odff;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

final class FileUtils {

    private FileUtils() {
        throw new AssertionError("Non-instantiable class");
    }

    public static File createFile(Path directory, String fileName) throws IOException {
        File file = resolveFile(directory, fileName);
        boolean fileExists = !file.createNewFile();
        if (fileExists) {
            throw new FileAlreadyExistsException(String.format("File %s already exists. Please rename it and try again.", file));
        }
        return file;
    }

    private static File resolveFile(Path directory, String fileName) {
        return directory.resolve(fileName).toFile();
    }
}
