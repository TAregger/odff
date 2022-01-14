package io.aregger.otf.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class TracefileWriter {

    private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    TracefileWriter() {
    }

    void writeFile(String tracefileName, DatabaseFileFetcher fetcher) throws IOException {
        File file = createFile(tracefileName);
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            fetcher.fetchTracefile(writeLine(outputStream));
        }
    }

    private File createFile(String tracefileName) throws IOException {
        File tmpFile = File.createTempFile("otf_", "_" + tracefileName);
        log.info("Created file " + tmpFile.getAbsolutePath());
        return tmpFile;
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
