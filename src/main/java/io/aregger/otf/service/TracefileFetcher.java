package io.aregger.otf.service;

import java.util.function.Consumer;

class TracefileFetcher implements DatabaseFileFetcher {

    private final String tracefileName;
    private final TracefileDao dao;

    public TracefileFetcher(String tracefileName, TracefileDao dao) {
        this.tracefileName = tracefileName;
        this.dao = dao;
    }

    @Override
    public void fetchTracefile(Consumer<String> tracefileLineConsumer) {
        this.dao.fetchTracefile(this.tracefileName, tracefileLineConsumer);
    }
}
