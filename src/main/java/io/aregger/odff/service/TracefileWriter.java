package io.aregger.odff.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class TracefileWriter {

    private final Path currentDir;

    public TracefileWriter(Path currentDir) {
        this.currentDir = requireNonNull(currentDir, "'currentDir' must not be null");
    }

    void writeFile(String tracefileName, DatabaseFileFetcher fetcher) throws UncheckedIOException, IOException {
        File file = FileUtils.createFile(currentDir, tracefileName);
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            fetcher.fetchTracefile(writeLine(outputStream));
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
