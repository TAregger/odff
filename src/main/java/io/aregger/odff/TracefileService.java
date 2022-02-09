package io.aregger.odff;

interface TracefileService {

    void initialize(TracefileWriter tracefileWriter, String jdbcConnectionString);

    void fetchTracefile(String tracefileName);

    void fetchAlertLog();
}
