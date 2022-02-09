package io.aregger.odff;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;

import static io.aregger.odff.OracleDiagFileFetcherCliOptions.DEFAULT_CONNECTIONS_FILE;
import static io.aregger.odff.ConnectionDefinition.ORACLE_THIN_SUBPROTOCOL;
import static picocli.CommandLine.Mixin;

@CommandLine.Command(
    name = "odff",
    description = "Oracle Diag File Fetcher.%nFetches alert logs and trace files from Oracle databases.",
    version = "0.1.1-SNAPSHOT",
    mixinStandardHelpOptions=true,
    sortOptions = false,
    usageHelpWidth = 125)
public class OracleDiagFileFetcher implements Callable<Integer> {

    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    @Mixin
    private OracleDiagFileFetcherCliOptions options;

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

    public static void main(String[] args) {
        System.exit(main(args, new OracleDiagFileFetcher()));
    }

    // @VisibleForTesting
    static int main(String[] args, OracleDiagFileFetcher app) {
        return new CommandLine(app)
            .setUsageHelpLongOptionsMaxWidth(40)
            .execute(args);
    }

    @Override
    public Integer call() {
        try {
            doCall();
            return 0;
        } catch (Exception e ) {
            logger.error(e.getMessage(), e);
            return 1;
        }
    }

    private void doCall() {
        String url;
        if (this.options.isUrlArgumentProvided()) {
            url = this.options.getUrl().startsWith(ORACLE_THIN_SUBPROTOCOL) ? this.options.getUrl() : ORACLE_THIN_SUBPROTOCOL + this.options.getUrl();
        } else {
            url = buildUrlFromConnectionDefinitions().orElseThrow(() -> new IllegalArgumentException("No connection definition found for name " + this.options.getConnectionName()));
        }

        this.tracefileService.initialize(new TracefileWriter(this.workingDir), url);

        if (this.options.getFetchAlertlog()) {
            this.tracefileService.fetchAlertLog();
        } else {
            this.tracefileService.fetchTracefile(this.options.getTracefile());
        }
    }

    private Optional<String> buildUrlFromConnectionDefinitions() {
        return getConnectionDefinition().map(ConnectionDefinition::buildJdbcConnectionString);
    }

    private Optional<ConnectionDefinition> getConnectionDefinition() {
        if (this.options.isPasswordOptionProvided()) {
            String password = this.options.isPasswordProvidedAsArgument() ? this.options.getPassword() : this.passwordReader.readPassword();
            if (password == null) {
                throw new IllegalArgumentException("No password given");
            }
            return readConnectionFromFile(password);
        } else {
            return readConnectionFromFile();
        }
    }

    private Optional<ConnectionDefinition> readConnectionFromFile() {
        String filepath = this.options.getConnectionFilePath() != null ?
            this.options.getConnectionFilePath() :
            this.workingDir.resolve(DEFAULT_CONNECTIONS_FILE).toFile().toString();
        return ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(Path.of(filepath).toFile(), this.options.getConnectionName());
    }

    private Optional<ConnectionDefinition> readConnectionFromFile(String password) {
        String filepath = this.options.getConnectionFilePath() != null ?
            this.options.getConnectionFilePath() :
            this.workingDir.resolve(DEFAULT_CONNECTIONS_FILE).toFile().toString();
        return ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(Path.of(filepath).toFile(), this.options.getConnectionName(), password);
    }

}
