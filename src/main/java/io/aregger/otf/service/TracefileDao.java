package io.aregger.otf.service;

import org.springframework.jdbc.core.JdbcOperations;

import java.util.function.Consumer;

class TracefileDao {

    private static final String SELECT_TRACEFILE_PAYLOAD = "select t.payload from GV$DIAG_TRACE_FILE_CONTENTS t where t.TRACE_FILENAME = ?";
    private static final String SELECT_ALERTLOG_PAYLOAD = "select message_text from v$diag_alert_ext";

    private final JdbcOperations jdbc;

    TracefileDao(JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    void fetchTracefile(String tracefile, Consumer<String> tracefileLineConsumer) {
        jdbc.query(SELECT_TRACEFILE_PAYLOAD, rs -> {
            tracefileLineConsumer.accept(rs.getString("payload"));
        }, tracefile);
    }

    void fetchAlertlog(Consumer<String> tracefileLineConsumer) {
        jdbc.query(SELECT_ALERTLOG_PAYLOAD, rs -> {
            tracefileLineConsumer.accept(rs.getString("message_text"));
        });
    }

}
