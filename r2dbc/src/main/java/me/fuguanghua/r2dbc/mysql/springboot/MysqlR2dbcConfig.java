package me.fuguanghua.r2dbc.mysql.springboot;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.pool.PoolingConnectionFactoryProvider;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Option;
import io.r2dbc.spi.ValidationDepth;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.health.CompositeReactiveHealthContributorConfiguration;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.r2dbc.ConnectionFactoryHealthIndicator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.data.r2dbc.connectionfactory.lookup.AbstractRoutingConnectionFactory;
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.binding.BindMarkersFactory;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Slf4j
@Configuration
@EnableR2dbcRepositories(basePackages = "me.fuguanghua.*", entityOperationsRef = "mysqlR2dbcEntityOperations")
public class MysqlR2dbcConfig extends AbstractR2dbcConfiguration {
    @Bean(name = "readDbPoolSettings")
    @ConfigurationProperties(prefix = "mysql.read")
    public R2dbcPoolSettings readDbPoolSettings() {
        return new R2dbcPoolSettings();
    }

    @Bean(name = "writeDbPoolSettings")
    @ConfigurationProperties(prefix = "mysql.write")
    public R2dbcPoolSettings writeDbPoolSettings() {
        return new R2dbcPoolSettings();
    }

    @Bean
    public ConnectionPool readConnectionFactory() {
        return getNewConnectionPool(readDbPoolSettings());
    }

    @Bean
    public ConnectionPool writeConnectionFactory() {
        return getNewConnectionPool(writeDbPoolSettings());
    }

    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
        RoutingConnectionFactory connectionFactory = new RoutingConnectionFactory();

        Map<String, ConnectionFactory> factories = new HashMap<>();
        factories.put(RoutingConnectionFactory.TRANSACTION_WRITE, writeConnectionFactory());
        factories.put(RoutingConnectionFactory.TRANSACTION_READ, readConnectionFactory());

        connectionFactory.setDefaultTargetConnectionFactory(writeConnectionFactory());
        connectionFactory.setTargetConnectionFactories(factories);
        connectionFactory.setLenientFallback(true);

        return connectionFactory;
    }

    @Bean
    public DatabaseClient mysqlDatabaseClient(
            @Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        return DatabaseClient.builder().connectionFactory(connectionFactory)
                .bindMarkers(BindMarkersFactory.anonymous("?")).build();
    }

    @Bean
    @Primary
    public ReactiveDataAccessStrategy mysqlDataAccessStrategy() {
        return new DefaultReactiveDataAccessStrategy(MySqlDialect.INSTANCE);
    }

    @Bean
    public R2dbcEntityOperations oracleR2dbcEntityOperations(
            @Qualifier("mysqlDatabaseClient") DatabaseClient databaseClient,
            @Qualifier("mysqlDataAccessStrategy") ReactiveDataAccessStrategy dataAccessStrategy) {
        return new R2dbcEntityTemplate(databaseClient, dataAccessStrategy);
    }

    @Bean(name = "readTransactionManager")
    public ReactiveTransactionManager readTransactionManager() {
        R2dbcTransactionManager readOnly = new R2dbcTransactionManager(readConnectionFactory());
        readOnly.setEnforceReadOnly(true);
        return readOnly;
    }

    @Bean(name = "writeTransactionManager")
    public ReactiveTransactionManager writeTransactionManager() {
        return new R2dbcTransactionManager(writeConnectionFactory());
    }

    @Configuration
    @RequiredArgsConstructor
    class R2dbcHealthContributor extends CompositeReactiveHealthContributorConfiguration<ConnectionFactoryHealthIndicator, ConnectionFactory> {

        @Bean("r2dbcHealthContributor")
        public ReactiveHealthContributor r2dbcHealthContributor() {

            Map<String, ConnectionFactory> connectionFactoryMap = new HashMap<>();
            connectionFactoryMap.put("readConnectionFactory", readConnectionFactory());
            connectionFactoryMap.put("writeConnectionFactory", writeConnectionFactory());

            return createContributor(connectionFactoryMap);
        }
    }

    @Data
    class R2dbcPoolSettings {
        private String driver;
        private String protocol;
        private String host;
        private int port;
        private String username;
        private String password;
        private String database;
        private Duration connectionTimeout;
        private String poolName;
        private int initialSize;
        private int maxSize;
        private Duration maxIdleTime;
        private Duration maxLifeTime;
        private Duration maxCreateConnectionTime;
        private Duration maxAcquireTime;
        private int acquireRetry = 20;
    }

    static class RoutingConnectionFactory extends AbstractRoutingConnectionFactory {

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

    }

    // ============================= private helper methods  =============================

    private ConnectionPool getNewConnectionPool(R2dbcPoolSettings settings) {
        ConnectionFactory connectionFactory = ConnectionFactories.get(builder()
                        .option(DRIVER, StringUtils.defaultIfEmpty(settings.getDriver(), "pool"))
                        .option(PROTOCOL, StringUtils.defaultIfEmpty(settings.getProtocol(), "mysql"))
                        .option(HOST, settings.getHost())
                        .option(PORT, settings.getPort())
                        .option(USER, settings.getUsername())
                        .option(PASSWORD, settings.getPassword())
                        .option(DATABASE, settings.getDatabase())
                        .option(CONNECT_TIMEOUT, settings.getConnectionTimeout())
                        .option(SSL, false)
                        .option(Option.valueOf("zeroDate"), "use_null")
                        .option(PoolingConnectionFactoryProvider.MAX_SIZE, settings.getMaxSize())
//                .option(PoolingConnectionFactoryProvider.VALIDATION_QUERY, "select 1")
                        .option(PoolingConnectionFactoryProvider.VALIDATION_DEPTH, ValidationDepth.LOCAL)
                        .build()
        );

        ConnectionPoolConfiguration configuration = getNewConnectionPoolBuilder(connectionFactory, settings).build();
        return new ConnectionPool(configuration);
    }

    private ConnectionPoolConfiguration.Builder getNewConnectionPoolBuilder(ConnectionFactory connectionFactory, R2dbcPoolSettings settings) {

        return ConnectionPoolConfiguration.builder(connectionFactory)
                .name(settings.getPoolName())
                .initialSize(settings.getInitialSize())
                .maxSize(settings.getMaxSize())
                .maxIdleTime(settings.getMaxIdleTime())
                .maxLifeTime(settings.getMaxLifeTime())
                .maxAcquireTime(settings.getMaxAcquireTime())
                .acquireRetry(settings.getAcquireRetry())
                .maxCreateConnectionTime(settings.getMaxCreateConnectionTime())
//                .validationQuery("select 1")
                .validationDepth(ValidationDepth.LOCAL)
                .registerJmx(true);
    }
}
