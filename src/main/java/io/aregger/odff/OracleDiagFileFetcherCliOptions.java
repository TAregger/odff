package io.aregger.odff;

import static picocli.CommandLine.ArgGroup;
import static picocli.CommandLine.Option;

class OracleDiagFileFetcherCliOptions  {

    static final String DEFAULT_CONNECTIONS_FILE = "connections.json";

    @ArgGroup(multiplicity = "1")
    private ConnectionOptions connectionOptions;

    @ArgGroup(multiplicity = "1")
    private DiagFileOption diagFileOption;

    private static class ConnectionOptions {
        @Option(names = {"-u", "--url"}, description = "JDBC connection string without sub-protocol (e.g. \"scott/tiger@localhost:1521/ORCLCDB\")")
        private String url;

        @ArgGroup(exclusive = false)
        private ConnectionFromFileOptions connectionFromFileOptions;

        private static class ConnectionFromFileOptions {
            @Option(names = {"-n", "--name"}, description = "Name of the connection to use as defined in the connection definitions",
                required = true)
            String name;

            @Option(names = {"-c", "--connections"}, description =
                "File with connection definitions. If not specified the default is \n" + DEFAULT_CONNECTIONS_FILE + " in the the users current working " +
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

    boolean isUrlArgumentProvided() {
        return getUrl() != null;
    }

    String getUrl() {
        return this.connectionOptions.url;
    }

    String getConnectionName() {
        return this.connectionOptions.connectionFromFileOptions.name;
    }

    String getConnectionFilePath() {
        return this.connectionOptions.connectionFromFileOptions.filepath;
    }

    boolean isPasswordOptionProvided() {
        return getPassword() != null;
    }

    boolean isPasswordProvidedAsArgument() {
        return getPassword().length() != 0;
    }

    String getPassword() {
        return this.connectionOptions.connectionFromFileOptions.password;
    }

    boolean getFetchAlertlog() {
        return this.diagFileOption.fetchAlertlog;
    }

    String getTracefile() {
        return this.diagFileOption.tracefile;
    }

}
