package io.aregger.otf.application;

import io.aregger.otf.service.ConnectionIdentifier;
import io.aregger.otf.service.TracefileService;
import io.aregger.otf.service.TracefileWriter;
import picocli.CommandLine;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

public class OracleTracefileFetcherCli implements Callable<Integer> {

    @Option(names = {"-u", "--url"}, description = "JDBC connection string", required = true)
    private String url;

    @ArgGroup(exclusive = true, multiplicity = "1")
    DatabaseFileType databaseFileType;

    static class DatabaseFileType {
        @Option(names = {"-t", "--tracefileName"}, description = "Name of the trace file")
        private String tracefile;
        @Option(names = {"-a", "--alertlog"}, description = "Whether to fetch the alert log or not")
        private Boolean fetchAlertlog;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new OracleTracefileFetcherCli()).execute(args);
        System.exit(exitCode);
    }


    @Override
    public Integer call() throws Exception {
        TracefileService tracefileService = new TracefileService(new TracefileWriter(), new ConnectionIdentifier(url));
        if (databaseFileType.fetchAlertlog) {
            tracefileService.fetchAlertLog();
        } else {
            tracefileService.fetchTracefile(databaseFileType.tracefile);

        }
        return 0;
    }
}
