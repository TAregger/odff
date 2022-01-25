package io.aregger.odff.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    void createFile(@TempDir Path tempDir) throws IOException {
        // Act
        File file = FileUtils.createFile(tempDir, "myFile.trc");

        // Assert
        assertThat(file).exists();
    }

    @Test
    void createFileAlreadyExists(@TempDir Path tempDir) throws IOException {
        // Arrange
        FileUtils.createFile(tempDir, "myFile.trc");

        // Act, Assert
        assertThrows(FileAlreadyExistsException.class, () -> FileUtils.createFile(tempDir, "myFile.trc"));
    }
}