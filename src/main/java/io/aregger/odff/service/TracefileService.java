package io.aregger.odff.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

public class TracefileService {
    private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private final TracefileWriter tracefileWriter;
    private final TracefileDao tracefileDao;

    public TracefileService(TracefileWriter tracefileWriter, ConnectionIdentifier connectionIdentifier) throws SQLException {
        this.tracefileWriter = tracefileWriter;
        this.tracefileDao = new TracefileDao(JdbcTemplateUtils.createJdbcTemplate(connectionIdentifier));
    }

    public void fetchTracefile(String tracefileName) throws IOException {
        log.info("Start fetching tracefile {}", tracefileName);
        tracefileWriter.writeFile(tracefileName, new TracefileFetcher(tracefileName, this.tracefileDao));
        log.info("Finished fetching tracefile {}", tracefileName);

    }

    public void fetchAlertLog() throws IOException {
        String filename = "alert.log";
        log.info("Start fetching alertlog {}", filename);
        tracefileWriter.writeFile(filename, new AlertlogFetcher(this.tracefileDao));
        log.info("Finished fetching alertlog {}", filename);
    }
}
