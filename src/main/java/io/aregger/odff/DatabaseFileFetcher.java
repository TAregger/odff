package io.aregger.odff;

import java.util.function.Consumer;

interface DatabaseFileFetcher {

    void fetchTracefile(Consumer<String> tracefileLineConsumer);

}
