package me.fuguanghua.sync2dbc.druid.mutids.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fuguanghua on 2020/07/09.
 * <p>
 * 数据源一
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        // 实体管理bean名称
        entityManagerFactoryRef = "entityManagerFactoryPrimary",
        // 事务管理bean名称
        transactionManagerRef = "TransactionManagerPrimary",
        basePackages = {"me.fuguanghua.*", "me.fuguanghua.*"})
public class OneConfig {

    private final JpaProperties jpaProperties;

    @Qualifier("primaryDataSource")
    private final DataSource primaryDataSource;// 自动注入配置好的数据源

    @Value("${spring.jpa.hibernate.primary-dialect}")
    private String primaryDialect;// 获取对应的数据库方言

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String HIBERNATE_DEFAULT_SCHEMA;

    public OneConfig(JpaProperties jpaProperties, DataSource primaryDataSource) {
        this.jpaProperties = jpaProperties;
        this.primaryDataSource = primaryDataSource;
    }

    @Bean(name = "entityManagerPrimary")
    @Primary
    public EntityManager primaryEntityManager(@Qualifier("entityManagerFactoryPrimary") EntityManagerFactory
                                                      entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    /**
     *
     * @param builder
     * @return
     */
    @Bean(name = "entityManagerFactoryPrimary")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder) {

        return builder
                //设置数据源
                .dataSource(primaryDataSource)
                //设置数据源属性
                .properties(getVendorProperties(primaryDataSource))
                //设置实体类所在位置.扫描所有带有 @Entity 注解的类
                .packages("me.fuguanghua.modules.mnt.domain", "me.fuguanghua.modules.monitor.domain", "me.fuguanghua.modules.quartz.domain", "me.fuguanghua.modules.system.domain", "me.fuguanghua.domain", "me.fuguanghua.service.dto")
                // 设置持久化单元名，用于@PersistenceContext注解获取EntityManager时指定数据源
                .persistenceUnit("primaryPersistenceUnit")
                .build();
    }

    private Map<String, Object> getVendorProperties(DataSource dataSource) {
        Map<String,String> map = new HashMap<>();
        map.put("hibernate.dialect",primaryDialect);// 设置对应的数据库方言
        map.put("hibernate.default_schema",HIBERNATE_DEFAULT_SCHEMA);
        jpaProperties.setProperties(map);
        HibernateProperties hibernateProperties = new HibernateProperties();
        return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
    }

    /**
     * 配置事物管理器
     *
     * @param
     * @return
     */
    @Bean(name = "TransactionManagerPrimary")
    @Primary
    PlatformTransactionManager transactionManagerPrimary(@Qualifier("entityManagerFactoryPrimary") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
    /*PlatformTransactionManager transactionManagerPrimary(@Qualifier("entityManagerFactoryPrimary") EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryPrimary(builder).getObject());
    }*/
}
