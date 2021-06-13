package me.fuguanghua.r2dbc.oracle.springboot;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.dialect.OracleDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.binding.BindMarkersFactory;
import org.springframework.transaction.ReactiveTransactionManager;

import java.time.Duration;

@Slf4j
@Configuration
@EnableR2dbcRepositories(basePackages = "me.fuguanghua.*", entityOperationsRef = "oracleR2dbcEntityOperations")
public class OracleR2dbcConfig extends AbstractR2dbcConfiguration {

    @Bean(name = "readDbPoolSettings")
    @ConfigurationProperties(prefix = "datasource.oracle")
    public R2dbcPoolSettings readDbPoolSettings() {
        return new R2dbcPoolSettings();
    }

    @Bean
    public ConnectionPool readConnectionFactory() {
        /*Mono.from(getNewConnectionPool(readDbPoolSettings()).create())
            .flatMapMany(connection ->
                Mono.from(connection.createStatement(
                    "SELECT * FROM SWS_BUSINESS.MENU")
                    .execute())
                    .flatMapMany(result ->
                        result.map((row, metadata) -> row.get(0, String.class)))
                    .concatWith(Mono.from(connection.setTransactionIsolationLevel(IsolationLevel.READ_COMMITTED)).cast(String.class))
                    .concatWith(Mono.from(connection.close()).cast(String.class)))
            .toStream()
            .forEach(System.out::println);*/
        return getNewConnectionPool(readDbPoolSettings());
    }


    @Bean
    public DatabaseClient oracleDatabaseClient(
            @Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        return DatabaseClient.builder().connectionFactory(connectionFactory)
                .bindMarkers(BindMarkersFactory.anonymous("?")).build();
    }

    @Bean
    public R2dbcEntityOperations oracleR2dbcEntityOperations(
            @Qualifier("oracleDatabaseClient") DatabaseClient databaseClient,
            @Qualifier("oracleDataAccessStrategy") ReactiveDataAccessStrategy dataAccessStrategy) {
        return new R2dbcEntityTemplate(databaseClient, dataAccessStrategy);
    }

    @Bean
    @Primary
    public ReactiveDataAccessStrategy oracleDataAccessStrategy() {
        return new DefaultReactiveDataAccessStrategy(OracleDialect.INSTANCE);
    }

    @Bean(name = "readTransactionManager")
    public ReactiveTransactionManager readTransactionManager() {
        R2dbcTransactionManager readOnly = new R2dbcTransactionManager(readConnectionFactory().unwrap());
        readOnly.setEnforceReadOnly(true);
        return readOnly;
    }


    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        /*RoutingConnectionFactory connectionFactory = new RoutingConnectionFactory();

        Map<String, ConnectionFactory> factories = new HashMap<>();
        factories.put(RoutingConnectionFactory.TRANSACTION_WRITE, writeConnectionFactory());
        factories.put(RoutingConnectionFactory.TRANSACTION_READ, readConnectionFactory());

        connectionFactory.setDefaultTargetConnectionFactory(writeConnectionFactory());
        connectionFactory.setTargetConnectionFactories(factories);
        connectionFactory.setLenientFallback(true);

        return connectionFactory;*/
        return getNewConnectionPool(readDbPoolSettings()).unwrap();
    }

    /*static class RoutingConnectionFactory extends AbstractRoutingConnectionFactory {

        public static final String TRANSACTION_READ = "read";
        public static final String TRANSACTION_WRITE = "write";

        @Override
        protected Mono<Object> determineCurrentLookupKey() {
            return TransactionSynchronizationManager.forCurrentTransaction()
                .map(it -> {

                    log.info("it.getCurrentTransactionName() : {}", it.getCurrentTransactionName());
                    log.info("it.isActualTransactionActive() : {}", it.isActualTransactionActive());
                    log.info("it.isCurrentTransactionReadOnly() : {}", it.isCurrentTransactionReadOnly());

                    if (it.isActualTransactionActive() && it.isCurrentTransactionReadOnly()) {
                        return TRANSACTION_READ;
                    }
                    return TRANSACTION_WRITE;
                });
        }

    }*/


    @Data
    public class R2dbcPoolSettings {
        private String driver;
        private String host;
        private int port;
        private String databaseName;
        private String username;
        private String password;
        private Duration CONNECT_TIMEOUT;
        private Duration SQL_TIMEOUT;
        private String validationQuery;
        private Duration maxIdleTime;
        private Duration maxCreateConnectionTime;
        private Duration maxAcquireTime;
        private int initialSize;
        private int maxSize;
        private String name;
        private Boolean registerJmx;
    }

    private ConnectionPool getNewConnectionPool(R2dbcPoolSettings settings) {
        ConnectionFactory CONNECTION_FACTORY = ConnectionFactories.get(
                ConnectionFactoryOptions.builder()
                        .option(ConnectionFactoryOptions.DRIVER, StringUtils.defaultIfEmpty(settings.getDriver(), "oracle"))
                        .option(ConnectionFactoryOptions.HOST, settings.getHost())
                        .option(ConnectionFactoryOptions.PORT, settings.getPort())
                        .option(ConnectionFactoryOptions.DATABASE, settings.getDatabaseName())
                        .option(ConnectionFactoryOptions.USER, settings.getUsername())
                        .option(ConnectionFactoryOptions.PASSWORD, settings.getPassword())
                        .build());
        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(CONNECTION_FACTORY)
                .validationQuery(settings.getValidationQuery())
                .maxIdleTime(settings.getMaxIdleTime())
                .maxCreateConnectionTime(settings.getMaxCreateConnectionTime())
                .maxAcquireTime(settings.getMaxAcquireTime())
                .initialSize(settings.getInitialSize())
                .maxSize(settings.getMaxSize())
                .name(settings.getName())
                .registerJmx(settings.getRegisterJmx())
                .build();
        return new ConnectionPool(configuration);
    }
}
