package io.aregger.odff.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class TracefileWriter {

    private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    private final Path currentDir;

    public TracefileWriter(Path currentDir) {
        this.currentDir = requireNonNull(currentDir, "'currentDir' must not be null");
    }

    void writeFile(String tracefileName, DatabaseFileFetcher fetcher) throws UncheckedIOException, IOException {
        File file = FileUtils.createFile(currentDir, tracefileName);
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            fetcher.fetchTracefile(writeLine(outputStream));
        } finally {
            if (file.length() == 0) {
                // If no rows are returned by the database, e.g. because an non-existent tracefile was specified by the user,
                // the file is empty and should be deleted.
                file.delete();
            }
        }

        if (file.length() != 0) {
            log.info("{} bytes written to file {}", NUMBER_FORMAT.format(file.length()), file.getAbsolutePath());
        }
    }

    private static Consumer<String> writeLine(OutputStream outputStream) {
        return line -> {
            try {
                // gv$diag_trace_file_contents.payload can be null.
                if (line == null) {
                    return;
                }
                // Lines in gv$diag_trace_file_contents.payload and v$diag_alert_ext.message_text already contain a trailing newline character.
                outputStream.write(line.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }
}
