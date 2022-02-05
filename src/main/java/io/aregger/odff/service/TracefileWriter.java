package io.aregger.odff.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.function.Consumer;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.util.Objects.requireNonNull;

public class TracefileWriter {

    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    private final Path currentDir;

    public TracefileWriter(Path currentDir) {
        this.currentDir = requireNonNull(currentDir, "'currentDir' must not be null");
    }

    void writeFile(String tracefileName, DatabaseFileFetcher fetcher) throws IOException {
        File file = this.currentDir.resolve(tracefileName).toFile();
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(file.toPath(), CREATE_NEW)) {
            fetcher.fetchTracefile(writeLine(bufferedWriter));
        } finally {
            if (file.length() == 0) {
                // If no rows are returned by the database, e.g. because a non-existent tracefile was specified by the user,
                // the file is empty and should be deleted.
                file.delete();
            }
        }

        if (file.length() != 0) {
            logger.info("{} bytes written to file {}", NUMBER_FORMAT.format(file.length()), file.getAbsolutePath());
        }
    }

    private static Consumer<String> writeLine(BufferedWriter bufferedWriter) {
        return line -> {
            try {
                // gv$diag_trace_file_contents.payload can be null.
                if (line == null) {
                    return;
                }
                // Lines in gv$diag_trace_file_contents.payload and v$diag_alert_ext.message_text already contain a trailing newline character.
                bufferedWriter.write(line);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }
}
