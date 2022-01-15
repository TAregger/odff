package io.aregger.odff.service;

import java.util.function.Consumer;

interface DatabaseFileFetcher {

    void fetchTracefile(Consumer<String> tracefileLineConsumer);

}
