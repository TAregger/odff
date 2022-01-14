package io.aregger.otf.service;

import oracle.jdbc.datasource.impl.OracleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;

public class JdbcTemplateUtils {

    private JdbcTemplateUtils() {
        throw new AssertionError("non-instantiable class");
    }

    public static JdbcTemplate createJdbcTemplate(ConnectionIdentifier connectionIdentifier) throws SQLException {
        return new JdbcTemplate(createDataSource(connectionIdentifier));
    }

    private static OracleDataSource createDataSource(ConnectionIdentifier connectionIdentifier) throws SQLException {
        OracleDataSource ds = new OracleDataSource();
        ds.setURL(connectionIdentifier.buildJbcConnectionString());
        ds.setConnectionProperty("defaultRowPrefetch", "1000");
        return ds;
    }
}
