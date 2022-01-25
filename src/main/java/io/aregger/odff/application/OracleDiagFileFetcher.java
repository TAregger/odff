package io.aregger.odff.application;

import io.aregger.odff.service.ConnectionDefinition;
import io.aregger.odff.service.ConnectionDefinitionUtils;
import io.aregger.odff.service.PasswordReader;
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
    private final PasswordReader passwordReader;

    private OracleDiagFileFetcher() {
        this.tracefileService = new TracefileServiceImpl();
        this.workingDir = Path.of(System.getProperty("user.dir"));
        this.passwordReader = new PasswordReader();
    }

    // @VisibleForTesting
    OracleDiagFileFetcher(TracefileService tracefileService, PasswordReader passwordReader, Path workingDir) {
        this.tracefileService = tracefileService;
        this.passwordReader = passwordReader;
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
            url = buildUrlFromConnectionDefinitions();
            if (url == null) {
                return 1;
            }
        }
        this.tracefileService.initialize(new TracefileWriter(workingDir), url);
        if (this.diagFileOption.fetchAlertlog) {
            this.tracefileService.fetchAlertLog();
        } else {
            this.tracefileService.fetchTracefile(this.diagFileOption.tracefile);
        }

        return 0;
    }

    private String buildUrlFromConnectionDefinitions() {
        return getConnectionDefinition().map(ConnectionDefinition::buildJdbcConnectionString).orElse(null);
    }

    private Optional<ConnectionDefinition> getConnectionDefinition() {
        if (isPasswordOptionProvided()) {
            String password = isPasswordProvidedAsArgument() ? getPassword() : passwordReader.readPassword();
            return password == null ? Optional.empty() : readConnectionFromFile(password);
        } else {
            return readConnectionFromFile();
        }
    }

    private boolean isPasswordOptionProvided() {
        return getPassword() != null;
    }

    private boolean isPasswordProvidedAsArgument() {
        return getPassword().length() != 0;
    }

    private Optional<ConnectionDefinition> readConnectionFromFile() {
        String filepath = getConnectionFilePath() != null ? getConnectionFilePath() : workingDir.resolve(DEFAULT_CONNECTIONS_FILES).toFile().toString();
        return ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(Path.of(filepath).toFile(), getConnectionName());
    }

    private Optional<ConnectionDefinition> readConnectionFromFile(String password) {
        String filepath = getConnectionFilePath() != null ? getConnectionFilePath() : workingDir.resolve(DEFAULT_CONNECTIONS_FILES).toFile().toString();
        return ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(Path.of(filepath).toFile(), getConnectionName(), password);
    }

    private boolean isUrlArgumentProvided() {
        return connectionOptions.url != null;
    }

    private String getPassword() {
        return connectionOptions.connectionFromFileOptions.password;
    }

    private String getConnectionFilePath() {
        return connectionOptions.connectionFromFileOptions.filepath;
    }

    private String getConnectionName() {
        return connectionOptions.connectionFromFileOptions.name;
    }

}
