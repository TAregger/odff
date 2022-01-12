package io.aregger.otf.service;

import org.springframework.jdbc.core.JdbcOperations;

import java.util.function.Consumer;

public class TracefileDao {

    private static final String SELECT_PAYLOAD = "select t.payload from GV$DIAG_TRACE_FILE_CONTENTS t where t.TRACE_FILENAME = ?";

    private final JdbcOperations jdbc;

    public TracefileDao(JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    public void fetchTracefile(String tracefile, Consumer<String> tracefileLineConsumer) {
        jdbc.query(SELECT_PAYLOAD, rs -> {
            tracefileLineConsumer.accept(rs.getString("payload"));
        }, tracefile);
    }

}
