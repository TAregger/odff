package io.aregger.otf.service;

import oracle.jdbc.datasource.impl.OracleDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class TracefileService {

    private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private TracefileDao tracefileDao;


    public void connect(ConnectionIdentifier connectionIdentifier) throws SQLException {
        JdbcTemplate jdbcTemplate = createJdbcTemplate(connectionIdentifier);
        this.tracefileDao = new TracefileDao(jdbcTemplate);
    }

    public void fetchTracefile(String tracefileName) throws IOException {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected");
        }
        File file = createFile(tracefileName);
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            this.tracefileDao.fetchTracefile(tracefileName, line -> {
                try {
                    writeLine(outputStream, line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    private File createFile(String tracefileName) throws IOException {
        File tmpFile = File.createTempFile("otf_", "_" + tracefileName);
        log.info("Created file " + tmpFile.getAbsolutePath());
        return tmpFile;
    }

    public void writeLine(OutputStream outputStream, String line) throws IOException {
        if (line == null) {
            return;
        }
        outputStream.write(line.getBytes(StandardCharsets.UTF_8));
    }

    private boolean isConnected() {
        return this.tracefileDao != null;
    }

    private JdbcTemplate createJdbcTemplate(ConnectionIdentifier connectionIdentifier) throws SQLException {
        return new JdbcTemplate(createDataSource(connectionIdentifier));
    }

    private OracleDataSource createDataSource(ConnectionIdentifier connectionIdentifier) throws SQLException {
        OracleDataSource ds = new OracleDataSource();
        ds.setURL(connectionIdentifier.buildJbcConnectionString());
        ds.setConnectionProperty("defaultRowPrefetch", "1000");
        return ds;
    }
}
