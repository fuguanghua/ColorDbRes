package me.fuguanghua.sync2dbc.hikaricp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.fuguanghua.sync2dbc.hikaricp.oracle.OracleObject;
import me.fuguanghua.sync2dbc.hikaricp.sqlserver.SqlserverObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class FastJdbc {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastJdbc.class);
    private static HikariConfig config_oracle = new HikariConfig();
    private static HikariConfig config_sqlserver = new HikariConfig();
    private static HikariDataSource ds_oracle;
    private static HikariDataSource ds_sqlserver;
    private static Map<String, HikariDataSource> jdbcMaps = new HashMap<>();
    static {
        config_oracle.setDriverClassName(OracleObject.driverClassName);
        config_oracle.setJdbcUrl(OracleObject.jdbcurl);
        config_oracle.setUsername(OracleObject.username);
        config_oracle.setPassword(OracleObject.password);
        config_oracle.setMinimumIdle(Integer.valueOf(OracleObject.minimumIdle));
        config_oracle.setMaximumPoolSize(Integer.valueOf(OracleObject.maximumPoolSize));
        config_oracle.setConnectionTimeout(Integer.valueOf(OracleObject.connectionTimeout));
        config_oracle.setIdleTimeout(Integer.valueOf(OracleObject.idleTimeout));
        ds_oracle = new HikariDataSource(config_oracle);

        config_sqlserver.setDriverClassName(SqlserverObject.driverClassName);
        config_sqlserver.setJdbcUrl(SqlserverObject.jdbcurl);
        config_sqlserver.setUsername(SqlserverObject.username);
        config_sqlserver.setPassword(SqlserverObject.password);
        config_sqlserver.setMinimumIdle(Integer.valueOf(SqlserverObject.minimumIdle));
        config_sqlserver.setMaximumPoolSize(Integer.valueOf(SqlserverObject.maximumPoolSize));
        config_sqlserver.setConnectionTimeout(Integer.valueOf(SqlserverObject.connectionTimeout));
        config_sqlserver.setIdleTimeout(Integer.valueOf(SqlserverObject.idleTimeout));
        ds_sqlserver = new HikariDataSource(config_sqlserver);

        jdbcMaps.put("oracle", ds_oracle);
        jdbcMaps.put("sqlserver", ds_sqlserver);
        /*HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(OracleObject.jdbcurl);
        ds.setUsername(OracleObject.username);
        ds.setPassword(OracleObject.password);
        ds.addDataSourceProperty("cachePrepStmts", "true");
        ds.addDataSourceProperty("prepStmtCacheSize", "500");
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        // QueryRunner queryRunner1 = new QueryRunner(ds);
        jdbcMaps.put("oracle", ds);
        HikariDataSource dt = new HikariDataSource();
        dt.setJdbcUrl(SqlserverObject.jdbcurl);
        dt.setUsername(SqlserverObject.username);
        dt.setPassword(SqlserverObject.password);
        dt.addDataSourceProperty("cachePrepStmts", "true");
        dt.addDataSourceProperty("prepStmtCacheSize", "500");
        dt.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        // QueryRunner queryRunner2 = new QueryRunner(dt);
        jdbcMaps.put("sqlserver", dt);*/
    }

    public static Connection getConnection(String dbType) {
        // LOGGER.info("getConnection() invoked");
        try {
            return jdbcMaps.get(dbType).getConnection();
        } catch (SQLException e) {
            LOGGER.error("Can't get connection from DataSource: " + dbType);
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    FastJdbc(){}
}
