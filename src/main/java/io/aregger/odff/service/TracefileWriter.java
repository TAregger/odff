package io.aregger.odff.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class TracefileWriter {

    private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private final Path currentDir;

    public TracefileWriter(Path currentDir) {
        this.currentDir = requireNonNull(currentDir, "'currentDir' must not be null");
    }

    void writeFile(String tracefileName, DatabaseFileFetcher fetcher) throws IOException {
        File file = FileUtils.createFile(currentDir, tracefileName);
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            fetcher.fetchTracefile(writeLine(outputStream));
        }
    }

    private static Consumer<String> writeLine(OutputStream outputStream) {
        return line -> {
            try {
                if (line == null) {
                    return;
                }
                outputStream.write(line.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}
