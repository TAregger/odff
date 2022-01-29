package io.aregger.odff.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;

import static java.util.Objects.requireNonNull;

public class TracefileServiceImpl implements TracefileService {

    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

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
        logger.info("Start fetching tracefile {}", tracefileName);
        fetchAndHandleExceptions(() -> {
            this.tracefileWriter.writeFile(tracefileName, new TracefileFetcher(tracefileName, this.tracefileDao));
            logger.info("Finished fetching tracefile {}", tracefileName);
        });
    }

    @Override
    public void fetchAlertLog() {
        validateIsInitialized();
        String filename = "alert.log";
        logger.info("Start fetching alertlog {}", filename);
        fetchAndHandleExceptions(() -> {
            this.tracefileWriter.writeFile(filename, new AlertlogFetcher(this.tracefileDao));
            logger.info("Finished fetching alertlog {}", filename);
        });
    }

    private void validateIsInitialized() {
        if (this.tracefileWriter == null || this.tracefileDao == null) {
            throw new IllegalStateException("TracefileService is not initialized.");
        }
    }

    private interface Runnable {
        void run() throws Exception;
    }

    private static void fetchAndHandleExceptions(Runnable runnable) {
        try {
            runnable.run();
        } catch(Exception e) {
            throw new TracefileServiceException(e.getMessage(), e);
        }
    }
}
