package io.aregger.odff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TracefileWriterTest {

    private TracefileWriter writer;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
        this.writer = new TracefileWriter(this.tempDir);
    }

    @Test
    void writeFile() throws IOException {
        // Act
        this.writer.writeFile("myFile.trc", tracefileLineConsumer -> {
            tracefileLineConsumer.accept("first line\n");
            tracefileLineConsumer.accept("second line");
            tracefileLineConsumer.accept(null);
        });

        // Assert
        File tracefile = this.tempDir.resolve("myFile.trc").toFile();
        assertThat(tracefile).exists();
        assertThat(tracefile).content().isEqualTo("first line\nsecond line");
    }

    @Test
    void writeFileEmptyFetch() throws IOException {
        // Act
        this.writer.writeFile("myFile.trc", tracefileLineConsumer -> {});

        // Assert
        File tracefile = this.tempDir.resolve("myFile.trc").toFile();
        assertThat(tracefile).doesNotExist();
    }

    @Test
    void writeFileExists() throws IOException {
        // Arrange
        this.tempDir.resolve("myFile.trc").toFile().createNewFile();

        // Act, Assert
        assertThrows(FileAlreadyExistsException.class, () -> this.writer.writeFile("myFile.trc", tracefileLineConsumer -> {}));
    }



}