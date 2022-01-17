package io.aregger.odff.application;

import io.aregger.odff.service.ConnectionIdentifier;
import io.aregger.odff.service.TracefileService;
import io.aregger.odff.service.TracefileWriter;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(
    name = "odff",
    description = "Oracle Diag File Fetcher. Tool to fetch tracefiles and alertlog.",
    mixinStandardHelpOptions=true,
    version = "1.0-SNAPSHOT")
public class OracleDiagFileFetcher implements Callable<Integer> {

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
        int exitCode = new CommandLine(new OracleDiagFileFetcher()).execute(args);
        System.exit(exitCode);
    }


    @Override
    public Integer call() throws Exception {
        try {
            return doCall();
        } catch (FileAlreadyExistsException e) {
            return 1;
        }
    }

    private int doCall() throws SQLException, IOException {
        TracefileService tracefileService = new TracefileService(new TracefileWriter(getCurrentDir()), new ConnectionIdentifier(url));
        if (databaseFileType.fetchAlertlog) {
            tracefileService.fetchAlertLog();
        } else {
            tracefileService.fetchTracefile(databaseFileType.tracefile);
        }
        return 0;
    }

    private Path getCurrentDir() {
        return Path.of(System.getProperty("user.dir"));
    }
}
