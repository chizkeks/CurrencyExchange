package utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dao.ExchangeRateDAOImplSQLite;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionManager {
    private static final String URL_KEY;
    private static HikariConfig config;
    private static HikariDataSource dataSource;

    static {
        URL_KEY = "jdbc:sqlite:" + DBConnectionManager.class.getResource("/db/currency_exchange_db");

        config = new HikariConfig();
        config.setJdbcUrl(URL_KEY);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource( config );

        //Upload sqlite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private DBConnectionManager() {}

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
