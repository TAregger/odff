package io.aregger.odff.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.FileAlreadyExistsException;

import static java.util.Objects.requireNonNull;

public class TracefileServiceImpl implements TracefileService {
    private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private TracefileWriter tracefileWriter;
    private TracefileDao tracefileDao;

    @Override
    public void initialize(TracefileWriter tracefileWriter, String jdbcConnectionString) {
        this.tracefileWriter = tracefileWriter;
        this.tracefileDao = new TracefileDao(JdbcTemplateUtils.createJdbcTemplate(jdbcConnectionString));
    }

    @Override
    public void fetchTracefile(String tracefileName) {
        validateIsInitialized();
        requireNonNull(tracefileName, "'tracefileName' must not be null");
        log.info("Start fetching tracefile {}", tracefileName);
        fetchAndHandleExceptions(() -> {
            tracefileWriter.writeFile(tracefileName, new TracefileFetcher(tracefileName, this.tracefileDao));
            log.info("Finished fetching tracefile {}", tracefileName);
        });
    }

    @Override
    public void fetchAlertLog() {
        validateIsInitialized();
        String filename = "alert.log";
        log.info("Start fetching alertlog {}", filename);
        fetchAndHandleExceptions(() -> {
            tracefileWriter.writeFile(filename, new AlertlogFetcher(this.tracefileDao));
            log.info("Finished fetching alertlog {}", filename);
        });
    }

    private void validateIsInitialized() {
        if (this.tracefileWriter == null || this.tracefileDao == null) {
            throw new IllegalStateException("TracefileService is not initialized.");
        }
    }

    private interface Runnable {
        void run() throws IOException, CannotGetJdbcConnectionException;
    }

    private void fetchAndHandleExceptions(Runnable runnable) {
        try {
            runnable.run();
        } catch (FileAlreadyExistsException e) {
            throw new TracefileServiceException(e);
        } catch(UncheckedIOException | IOException e) {
            String message = "Exception writing file.";
            log.error(message, e);
            throw new TracefileServiceException(message, e);
        } catch (CannotGetJdbcConnectionException e) {
            log.error(e.getMessage());
            log.debug("Stacktrace", e);
        }

    }
}
