package io.aregger.otf.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

public class TracefileService {

    private final TracefileWriter tracefileWriter;
    private final TracefileDao tracefileDao;

    public TracefileService(TracefileWriter tracefileWriter, ConnectionIdentifier connectionIdentifier) throws SQLException {
        this.tracefileWriter = tracefileWriter;
        this.tracefileDao = new TracefileDao(JdbcTemplateUtils.createJdbcTemplate(connectionIdentifier));
    }

    public void fetchTracefile(String tracefileName) throws IOException {
        tracefileWriter.writeFile(tracefileName, new TracefileFetcher(tracefileName, this.tracefileDao));
    }

    public void fetchAlertLog() throws IOException {
        tracefileWriter.writeFile("alert.log", new AlertlogFetcher(this.tracefileDao));
    }
}
