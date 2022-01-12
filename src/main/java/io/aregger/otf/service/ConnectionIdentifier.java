package io.aregger.otf.service;

public class ConnectionIdentifier {

    private String providedJdbcConnectionString;

    public ConnectionIdentifier(String providedJdbcConnectionString) {
        this.providedJdbcConnectionString = providedJdbcConnectionString;
    }

    public String buildJbcConnectionString() {
        if (providedJdbcConnectionString != null) {
            return providedJdbcConnectionString;
        } else {
            throw new UnsupportedOperationException("JDBC Connection string must be provided");
        }
    }
}
