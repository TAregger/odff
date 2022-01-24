package io.aregger.odff.application;

import io.aregger.odff.service.ConnectionDefinition;
import io.aregger.odff.service.ConnectionDefinitionUtils;
import io.aregger.odff.service.TracefileService;
import io.aregger.odff.service.TracefileServiceException;
import io.aregger.odff.service.TracefileServiceImpl;
import io.aregger.odff.service.TracefileWriter;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;

import static picocli.CommandLine.ArgGroup;
import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(
    name = "odff",
    description = "Oracle Diag File Fetcher.%nFetches alert logs and trace files from Oracle databases.",
    mixinStandardHelpOptions=true,
    sortOptions = false,
    usageHelpWidth = 125)

public class OracleDiagFileFetcher implements Callable<Integer> {

    private static final String DEFAULT_CONNECTIONS_FILES = "connections.json";

    @ArgGroup(multiplicity = "1")
    ConnectionOptions connectionOptions;

    @ArgGroup(multiplicity = "1")
    DiagFileOption diagFileOption;

    private static class ConnectionOptions {
        @Option(names = {"-u", "--url"}, description = "JDBC connection string")
        private String url;

        @ArgGroup(exclusive = false)
        ConnectionFromFileOptions connectionFromFileOptions;

        private static class ConnectionFromFileOptions {
            @Option(names = {"-n", "--name"}, description = "Name of the connection to use as defined in the connection definitions",
                required = true)
            String name;
            @Option(names = {"-c", "--connections"}, description =
                "File with connection definitions. If not specified the default is \n" + DEFAULT_CONNECTIONS_FILES + " in the the users current working " +
                "directory")
            String filepath;
            // TODO implement password
            @Option(names = {"-p", "--password"}, description = "Password used to connect", arity = "0..1")
            String password;
        }
    }

    private static class DiagFileOption {
        @Option(names = {"-t", "--tracefile"}, description = "Name of the trace file to fetch")
        private String tracefile;
        @Option(names = {"-a", "--alertlog"}, description = "Fetches the alert log instead of a trace file")
        private boolean fetchAlertlog;
    }

    public static void main(String[] args) {
        System.exit(main(args, new OracleDiagFileFetcher()));
    }

    // @VisibleForTesting
    static int main(String[] args, OracleDiagFileFetcher app) {
        return new CommandLine(app)
            .setUsageHelpLongOptionsMaxWidth(40)
            .execute(args);
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
        String url;
        if (isUrlArgumentProvided()) {
            url = connectionOptions.url;
        } else {
            Optional<ConnectionDefinition> connectionDefinition = readConnectionFromFile();
            if (connectionDefinition.isEmpty()) {
                return 1;
            }
            url = connectionDefinition.get().buildJdbcConnectionString();
        }

        this.tracefileService.initialize(new TracefileWriter(workingDir), url);
        if (this.diagFileOption.fetchAlertlog) {
            this.tracefileService.fetchAlertLog();
        } else {
            this.tracefileService.fetchTracefile(this.diagFileOption.tracefile);
        }

        return 0;
    }

    private boolean isUrlArgumentProvided() {
        return connectionOptions.url != null;
    }

    private Optional<ConnectionDefinition> readConnectionFromFile() {
        String filepath = getConnectionFilePath() != null ? getConnectionFilePath() : workingDir.resolve(DEFAULT_CONNECTIONS_FILES).toFile().toString();
        return ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(Path.of(filepath).toFile(), getConnectionName());
    }

    private String getConnectionFilePath() {
        return connectionOptions.connectionFromFileOptions.filepath;
    }

    private String getConnectionName() {
        return connectionOptions.connectionFromFileOptions.name;
    }

}
