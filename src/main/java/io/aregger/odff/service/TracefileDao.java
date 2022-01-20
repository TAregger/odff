package io.aregger.odff.service;

import org.springframework.jdbc.core.JdbcOperations;

import java.util.function.Consumer;

class TracefileDao {

    private static final String SELECT_TRACEFILE_PAYLOAD = """
                                                           SELECT t.payload
                                                             FROM gv$diag_trace_file_contents t
                                                            WHERE t.trace_filename = ?
                                                           """;

    private static final String SELECT_ALERTLOG_PAYLOAD = """
                                                          SELECT message_text
                                                            FROM v$diag_alert_ext
                                                          """;

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
