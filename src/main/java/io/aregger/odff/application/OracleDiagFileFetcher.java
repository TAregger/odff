package io.aregger.odff.application;

import io.aregger.odff.service.ConnectionIdentifier;
import io.aregger.odff.service.TracefileService;
import io.aregger.odff.service.TracefileServiceException;
import io.aregger.odff.service.TracefileServiceImpl;
import io.aregger.odff.service.TracefileWriter;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import static picocli.CommandLine.ArgGroup;
import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(
    name = "odff",
    description = "Oracle Diag File Fetcher.%nFetches alert logs and trace files from Oracle databases.",
    mixinStandardHelpOptions=true,
    sortOptions = false)
public class OracleDiagFileFetcher implements Callable<Integer> {

    @Option(names = {"-u", "--url"}, description = "JDBC connection string", required = true)
    private String url;

    @ArgGroup(multiplicity = "1")
    DiagFileOption diagFileOption;

    private static class DiagFileOption {
        @Option(names = {"-t", "--tracefileName"}, description = "Name of the trace file to fetch")
        private String tracefileName;
        @Option(names = {"-a", "--alertlog"}, description = "Fetches the alert log instead of a trace file")
        private boolean fetchAlertlog;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new OracleDiagFileFetcher()).execute(args);
        System.exit(exitCode);
    }

    private final TracefileService tracefileService;
    private final Path workingDir;

    private OracleDiagFileFetcher() {
        this.tracefileService = new TracefileServiceImpl();
        this.workingDir = Path.of(System.getProperty("user.dir"));
    }

    // @VisibleForTesting
    OracleDiagFileFetcher(TracefileService tracefileService, Path workingDir) {
        this.tracefileService = tracefileService;
        this.workingDir = workingDir;
    }

    @Override
    public Integer call() {
        try {
            return doCall();
        } catch (TracefileServiceException e) {
            return 1;
        }
    }

    private int doCall() {
        this.tracefileService.initialize(new TracefileWriter(workingDir), new ConnectionIdentifier(this.url));
        if (this.diagFileOption.fetchAlertlog) {
            this.tracefileService.fetchAlertLog();
        } else {
            this.tracefileService.fetchTracefile(this.diagFileOption.tracefileName);
        }
        return 0;
    }

}
