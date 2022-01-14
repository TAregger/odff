package io.aregger.otf.service;

import java.util.function.Consumer;

public class AlertlogFetcher implements DatabaseFileFetcher {

    private final TracefileDao dao;

    AlertlogFetcher(TracefileDao dao) {
        this.dao = dao;
    }

    @Override
    public void fetchTracefile(Consumer<String> tracefileLineConsumer) {
        this.dao.fetchAlertlog(tracefileLineConsumer);
    }
}
