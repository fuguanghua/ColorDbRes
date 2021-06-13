package me.fuguanghua.sync2dbc.druid.mutids.druid;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author fuguanghua wrote on 2020/07/09.
 * 数据库连接属性配置
 */
@ServletComponentScan
@Configuration
public class DruidDBConfig {
    private Logger logger = LoggerFactory.getLogger(DruidDBConfig.class);

    @Value("${spring.datasource.druid.first.url}")
    private String dbUrl1;

    @Value("${spring.datasource.druid.first.username}")
    private String username1;

    @Value("${spring.datasource.druid.first.password}")
    private String password1;

    @Value("${spring.datasource.druid.second.username}")
    private String username2;

    @Value("${spring.datasource.druid.second.password}")
    private String password2;

    @Value("${spring.datasource.druid.second.url}")
    private String dbUrl2;

    // @Value("${spring.datasource.druid.first.driverClassName}")
    // private String driverClassName1;

    @Value("oracle.jdbc.OracleDriver")
    private String driverClassName1;

    @Value("oracle.jdbc.OracleDriver")
    private String driverClassName2;

    @Value("${spring.datasource.druid.first.initial-size}")
    private int initialSize;

    @Value("${spring.datasource.druid.first.min-idle}")
    private int minIdle;

    @Value("${spring.datasource.druid.first.max-active}")
    private int maxActive;

    @Value("${spring.datasource.druid.first.max-wait}")
    private int maxWait;

    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    @Value("${spring.datasource.druid.first.time-between-eviction-runs-millis}")
    private int timeBetweenEvictionRunsMillis;
    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    @Value("${spring.datasource.druid.first.min-evictable-idle-time-millis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.druid.first.validation-query}")
    private String validationQuery;

    @Value("${spring.datasource.druid.first.test-while-idle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.druid.first.test-on-borrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.druid.first.test-on-return}")
    private boolean testOnReturn;

    /**
     * 打开PSCache，并且指定每个连接上PSCache的大小
     */
    @Value("true")
    private boolean poolPreparedStatements;

    @Value("20")
    private int maxPoolPreparedStatementPerConnectionSize;
    /**
     * 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
     */
    @Value("stat,wall,log4j")
    private String filters;
    /**
     * 通过connectProperties属性来打开mergeSql功能；慢SQL记录
     */
    @Value("druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500")
    private String connectionProperties;

    @Bean(name = "primaryDataSource")
    @Qualifier("primaryDataSource")
    @Primary
    public DataSource dataSource() {
        return getDruidDataSource(username1, password1, dbUrl1, driverClassName1);
    }

    @Bean(name = "secondaryDataSource")
    @Qualifier("secondaryDataSource")
    public DataSource secondaryDataSource() {
        return getDruidDataSource(username2, password2, dbUrl2, driverClassName2);
    }

    private DruidDataSource getDruidDataSource(String username, String password, String url, String driverClassName) {
        DruidDataSource datasource = new DruidDataSource();

        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);

        //configuration
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        try {
            datasource.setFilters(filters);
        } catch (SQLException e) {
            logger.error("druid configuration initialization filter : {0}", e);
        }
        datasource.setConnectionProperties(connectionProperties);

        return datasource;
    }
}