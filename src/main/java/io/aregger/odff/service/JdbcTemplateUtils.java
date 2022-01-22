package io.aregger.odff.service;

import oracle.jdbc.datasource.impl.OracleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;

class JdbcTemplateUtils {

    private JdbcTemplateUtils() {
        throw new AssertionError("non-instantiable class");
    }

    public static JdbcTemplate createJdbcTemplate(String jdbcConnectionString) {
        return new JdbcTemplate(createDataSource(jdbcConnectionString));
    }

    private static OracleDataSource createDataSource(String jdbcConnectionString) {
        try {
            OracleDataSource ds = new OracleDataSource();
            ds.setURL(jdbcConnectionString);
            ds.setConnectionProperty("defaultRowPrefetch", "1000");
            return ds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
