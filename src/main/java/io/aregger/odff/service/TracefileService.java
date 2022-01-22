package io.aregger.odff.service;

public interface TracefileService {

    void initialize(TracefileWriter tracefileWriter, String jdbcConnectionString);

    void fetchTracefile(String tracefileName);

    void fetchAlertLog();
}
