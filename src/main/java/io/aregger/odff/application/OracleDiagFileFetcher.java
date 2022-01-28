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

import static io.aregger.odff.application.OracleDiagFileFetcherCliOptions.DEFAULT_CONNECTIONS_FILE;
import static io.aregger.odff.service.ConnectionDefinition.ORACLE_THIN_SUBPROTOCOL;
import static picocli.CommandLine.Mixin;

@CommandLine.Command(
    name = "odff",
    description = "Oracle Diag File Fetcher.%nFetches alert logs and trace files from Oracle databases.",
    version = "0.1.0",
    mixinStandardHelpOptions=true,
    sortOptions = false,
    usageHelpWidth = 125)
public class OracleDiagFileFetcher implements Callable<Integer> {

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
            return doCall();
        } catch (TracefileServiceException e) {
            return 1;
        }
    }

    private int doCall() {
        String url;
        if (this.options.isUrlArgumentProvided()) {
            url = this.options.getUrl().startsWith(ORACLE_THIN_SUBPROTOCOL) ? this.options.getUrl() : ORACLE_THIN_SUBPROTOCOL + this.options.getUrl();
        } else {
            url = buildUrlFromConnectionDefinitions();
            if (url == null) {
                return 1;
            }
        }

        this.tracefileService.initialize(new TracefileWriter(this.workingDir), url);

        if (this.options.getFetchAlertlog()) {
            this.tracefileService.fetchAlertLog();
        } else {
            this.tracefileService.fetchTracefile(this.options.getTracefile());
        }

        return 0;
    }

    private String buildUrlFromConnectionDefinitions() {
        return getConnectionDefinition().map(ConnectionDefinition::buildJdbcConnectionString).orElse(null);
    }

    private Optional<ConnectionDefinition> getConnectionDefinition() {
        if (this.options.isPasswordOptionProvided()) {
            String password = this.options.isPasswordProvidedAsArgument() ? this.options.getPassword() : this.passwordReader.readPassword();
            return password == null ? Optional.empty() : readConnectionFromFile(password);
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
