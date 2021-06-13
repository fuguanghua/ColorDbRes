package me.fuguanghua.r2dbc.oracle.notspringboot;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.reactivestreams.Publisher;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Properties;

public final class DatabaseConfig {

  private DatabaseConfig() {}

  public static String host() {
    return HOST;
  }
  public static int port() {
    return PORT;
  }
  public static String serviceName() {
    return SERVICE_NAME;
  }
  public static String user() {
    return USER;
  }
  public static String password() {
    return PASSWORD;
  }
  public static Duration connectTimeout() {
    return CONNECT_TIMEOUT;
  }
  public static Duration sqlTimeout() {
    return SQL_TIMEOUT;
  }

  public static Publisher<? extends Connection> newConnection() {
    return CONNECTION_FACTORY.create();
  }

  public static Publisher<? extends Connection> sharedConnection() {
    return SHARED_CONNECTION_FACTORY.create();
  }

  public static Publisher<? extends Connection> getPooledConnection() {return pool.create();}

  public static int databaseVersion() {
    try (var jdbcConnection = DriverManager.getConnection(String.format(
      "jdbc:oracle:thin:@%s:%s/%s", host(), port(), serviceName()),
      user(), password())) {
      return jdbcConnection.getMetaData().getDatabaseMajorVersion();
    }
    catch (SQLException sqlException) {
      throw new AssertionError(sqlException);
    }
  }

  private static final String HOST;
  private static final int PORT;
  private static final String SERVICE_NAME;
  private static final String USER;
  private static final String PASSWORD;
  private static final Duration CONNECT_TIMEOUT;
  private static final Duration SQL_TIMEOUT;

  private static final String validationQuery;
  private static final Duration maxIdleTime;
  private static final Duration maxCreateConnectionTime;
  private static final Duration maxAcquireTime;
  private static final int initialSize;
  private static final int maxSize;
  private static final String name;
  private static final Boolean registerJmx;

  private static final ConnectionFactory CONNECTION_FACTORY;
  private static final ConnectionFactory SHARED_CONNECTION_FACTORY;
  private static final ConnectionPoolConfiguration configuration;
  private static final ConnectionPool pool;

  private static final String CONFIG_FILE_NAME = "config.properties";
  static {
    try (InputStream inputStream =
           DatabaseConfig.class.getClassLoader()
             .getResourceAsStream(CONFIG_FILE_NAME)) {

      System.out.println("Got here");
      if (inputStream == null) {
        throw new FileNotFoundException(
          CONFIG_FILE_NAME + " resource not found. " +
          "Check if it exists under target/conf");
      }

      Properties prop = new Properties();
      prop.load(inputStream);

      HOST = prop.getProperty("HOST");
      PORT = Integer.parseInt(prop.getProperty("PORT"));
      SERVICE_NAME = prop.getProperty("DATABASE");
      USER = prop.getProperty("USER");
      PASSWORD = prop.getProperty("PASSWORD");
      CONNECT_TIMEOUT = Duration.ofSeconds(
        Long.parseLong(prop.getProperty("CONNECT_TIMEOUT")));
      SQL_TIMEOUT = Duration.ofSeconds(
        Long.parseLong(prop.getProperty("SQL_TIMEOUT")));

      validationQuery = prop.getProperty("validationQuery");
      maxIdleTime = Duration.ofSeconds(Long.parseLong(prop.getProperty("maxIdleTime")));
      maxCreateConnectionTime = Duration.ofMinutes(Long.parseLong(prop.getProperty("maxCreateConnectionTime")));
      maxAcquireTime = Duration.ofMinutes(Long.parseLong(prop.getProperty("maxAcquireTime")));
      initialSize = Integer.parseInt(prop.getProperty("initialSize"));
      maxSize = Integer.parseInt(prop.getProperty("maxSize"));
      name = prop.getProperty("name");
      registerJmx = Boolean.parseBoolean(prop.getProperty("registerJmx"));

      CONNECTION_FACTORY = ConnectionFactories.get(
        ConnectionFactoryOptions.builder()
          .option(ConnectionFactoryOptions.DRIVER, "oracle")
          .option(ConnectionFactoryOptions.HOST, HOST)
          .option(ConnectionFactoryOptions.PORT, PORT)
          .option(ConnectionFactoryOptions.DATABASE, SERVICE_NAME)
          .option(ConnectionFactoryOptions.USER, USER)
          .option(ConnectionFactoryOptions.PASSWORD, PASSWORD)
          .build());

      SHARED_CONNECTION_FACTORY = new SharedConnectionFactory(
        CONNECTION_FACTORY.create(),
        CONNECTION_FACTORY.getMetadata());

      configuration = ConnectionPoolConfiguration.builder(CONNECTION_FACTORY)
              .validationQuery(validationQuery)
              .maxIdleTime(maxIdleTime)
              .maxCreateConnectionTime(maxCreateConnectionTime)
              .maxAcquireTime(maxAcquireTime)
              .initialSize(initialSize)
              .maxSize(maxSize)
              .name(name)
              .registerJmx(registerJmx)
              .build();

      pool = new ConnectionPool(configuration);
    }
    catch (Throwable initializationFailure) {
      initializationFailure.printStackTrace();
      System.exit(-1);
      throw new RuntimeException(initializationFailure);
    }
  }
}