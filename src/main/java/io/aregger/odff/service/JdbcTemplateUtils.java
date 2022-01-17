package io.aregger.odff.service;

import oracle.jdbc.datasource.impl.OracleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;

class JdbcTemplateUtils {

    private JdbcTemplateUtils() {
        throw new AssertionError("non-instantiable class");
    }

    public static JdbcTemplate createJdbcTemplate(ConnectionIdentifier connectionIdentifier) {
        return new JdbcTemplate(createDataSource(connectionIdentifier));
    }

    private static OracleDataSource createDataSource(ConnectionIdentifier connectionIdentifier) {
        try {
            OracleDataSource ds = new OracleDataSource();
            ds.setURL(connectionIdentifier.buildJbcConnectionString());
            ds.setConnectionProperty("defaultRowPrefetch", "1000");
            return ds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
