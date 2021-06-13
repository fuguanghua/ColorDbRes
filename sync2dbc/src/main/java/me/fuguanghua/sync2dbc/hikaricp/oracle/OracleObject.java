package me.fuguanghua.sync2dbc.hikaricp.oracle;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public final class OracleObject {
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

    private OracleObject(){}

    static {
        getOracleObject();
    }

    private static void getOracleObject(){
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
        dataSource = bundle.getString("oracle.dataSourceClassName");
        dataSourceClassName = bundle.getString("oracle.dataSourceClassName");
        jdbcurl =  bundle.getString("oracle.jdbcurl");
        username =  bundle.getString("oracle.username");
        password =  bundle.getString("oracle.password");
        autoCommit =  bundle.getString("oracle.autoCommit");
        connectionTimeout =  bundle.getString("oracle.connectionTimeout");
        idleTimeout =  bundle.getString("oracle.idleTimeout");
        maxLifetime =  bundle.getString("oracle.maxLifetime");
        connectionTestQuery =  bundle.getString("oracle.connectionTestQuery");
        minimumIdle =  bundle.getString("oracle.minimumIdle");
        maximumPoolSize =  bundle.getString("oracle.maximumPoolSize");
        metricRegistry =  bundle.getString("oracle.metricRegistry");
        healthCheckRegistry =  bundle.getString("oracle.healthCheckRegistry");
        poolName =  bundle.getString("oracle.poolName");
        initializationFailTimeout =  bundle.getString("oracle.initializationFailTimeout");
        isolateInternalQueries =  bundle.getString("oracle.isolateInternalQueries");
        allowPoolSuspension =  bundle.getString("oracle.allowPoolSuspension");
        readOnly =  bundle.getString("oracle.readOnly");
        registerMbeans =  bundle.getString("oracle.registerMbeans");
        catalog =  bundle.getString("oracle.catalog");
        connectionInitSql =  bundle.getString("oracle.connectionInitSql");
        driverClassName =  bundle.getString("oracle.driverClassName");
        transactionIsolation =  bundle.getString("oracle.transactionIsolation");
        validationTimeout =  bundle.getString("oracle.validationTimeout");
        leakDetectionThreshold =  bundle.getString("oracle.leakDetectionThreshold");
        dataSource =  bundle.getString("oracle.dataSource");
        schema =  bundle.getString("oracle.schema");
        threadFactory =  bundle.getString("oracle.threadFactory");
        scheduledExecutor =  bundle.getString("oracle.scheduledExecutor");
        cachePrepStmts =  bundle.getString("oracle.cachePrepStmts");
        prepStmtCacheSize =  bundle.getString("oracle.prepStmtCacheSize");
        prepStmtCacheSqlLimit =  bundle.getString("oracle.prepStmtCacheSqlLimit");
    }
}
