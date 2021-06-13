package me.fuguanghua.sync2dbc.hikaricp.sqlserver;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class SqlserverObject {
    public static String dataSource;
    public static String dataSourceClassName;
    public static String jdbcurl;
    public static String username;
    public static String password;
    public static String autoCommit;
    public static String connectionTimeout;
    public static String idleTimeout;
    public static String maxLifetime;
    public static String connectionTestQuery;
    public static String minimumIdle;
    public static String maximumPoolSize;
    public static String metricRegistry;
    public static String healthCheckRegistry;
    public static String poolName;
    public static String initializationFailTimeout;
    public static String isolateInternalQueries;
    public static String allowPoolSuspension;
    public static String readOnly;
    public static String registerMbeans;
    public static String catalog;
    public static String connectionInitSql;
    public static String driverClassName;
    public static String transactionIsolation;
    public static String validationTimeout;
    public static String leakDetectionThreshold;

    public static String schema;
    public static String threadFactory;
    public static String scheduledExecutor;
    public static String cachePrepStmts;
    public static String prepStmtCacheSize;
    public static String prepStmtCacheSqlLimit;

    private static BufferedInputStream inputStream;
    private static ResourceBundle bundle;

    private SqlserverObject(){}

    static {
        getsqlserverObject();
    }

    private static void getsqlserverObject(){
        String proFilePath = System.getProperty("user.dir") + "/config/db.properties";
        Locale en_US = new Locale("en", "US");
        // ResourceBundle bundle = ResourceBundle.getBundle("db", en_US);
        try{
            inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
            bundle = new PropertyResourceBundle(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        dataSource = bundle.getString("sqlserver.dataSourceClassName");
        dataSourceClassName = bundle.getString("sqlserver.dataSourceClassName");
        jdbcurl =  bundle.getString("sqlserver.jdbcurl");
        username =  bundle.getString("sqlserver.username");
        password =  bundle.getString("sqlserver.password");
        autoCommit =  bundle.getString("sqlserver.autoCommit");
        connectionTimeout =  bundle.getString("sqlserver.connectionTimeout");
        idleTimeout =  bundle.getString("sqlserver.idleTimeout");
        maxLifetime =  bundle.getString("sqlserver.maxLifetime");
        connectionTestQuery =  bundle.getString("sqlserver.connectionTestQuery");
        minimumIdle =  bundle.getString("sqlserver.minimumIdle");
        maximumPoolSize =  bundle.getString("sqlserver.maximumPoolSize");
        metricRegistry =  bundle.getString("sqlserver.metricRegistry");
        healthCheckRegistry =  bundle.getString("sqlserver.healthCheckRegistry");
        poolName =  bundle.getString("sqlserver.poolName");
        initializationFailTimeout =  bundle.getString("sqlserver.initializationFailTimeout");
        isolateInternalQueries =  bundle.getString("sqlserver.isolateInternalQueries");
        allowPoolSuspension =  bundle.getString("sqlserver.allowPoolSuspension");
        readOnly =  bundle.getString("sqlserver.readOnly");
        registerMbeans =  bundle.getString("sqlserver.registerMbeans");
        catalog =  bundle.getString("sqlserver.catalog");
        connectionInitSql =  bundle.getString("sqlserver.connectionInitSql");
        driverClassName =  bundle.getString("sqlserver.driverClassName");
        transactionIsolation =  bundle.getString("sqlserver.transactionIsolation");
        validationTimeout =  bundle.getString("sqlserver.validationTimeout");
        leakDetectionThreshold =  bundle.getString("sqlserver.leakDetectionThreshold");
        dataSource =  bundle.getString("sqlserver.dataSource");
        schema =  bundle.getString("sqlserver.schema");
        threadFactory =  bundle.getString("sqlserver.threadFactory");
        scheduledExecutor =  bundle.getString("sqlserver.scheduledExecutor");
        cachePrepStmts =  bundle.getString("sqlserver.cachePrepStmts");
        prepStmtCacheSize =  bundle.getString("sqlserver.prepStmtCacheSize");
        prepStmtCacheSqlLimit =  bundle.getString("sqlserver.prepStmtCacheSqlLimit");
    }
}
