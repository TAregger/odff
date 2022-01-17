package io.aregger.odff.service;

public interface TracefileService {

    void initialize(TracefileWriter tracefileWriter, ConnectionIdentifier connectionIdentifier);

    void fetchTracefile(String tracefileName);

    void fetchAlertLog();
}
