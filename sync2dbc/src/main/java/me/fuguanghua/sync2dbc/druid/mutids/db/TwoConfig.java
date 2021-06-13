package me.fuguanghua.sync2dbc.druid.mutids.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 * 数据源二
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactorySecondary",//配置连接工厂 entityManagerFactory
        transactionManagerRef = "transactionManagerSecondary", //配置 事物管理器  transactionManager
        basePackages = {"me.fuguanghua.*", "me.fuguanghua.*"}) //设置Repository所在位置
public class TwoConfig {

    private final JpaProperties jpaProperties;

    @Qualifier("secondaryDataSource")
    private final DataSource secondaryDataSource;// 自动注入配置好的数据源

    @Value("${spring.jpa.hibernate.secondary-dialect}")
    private String secondaryDialect;// 获取对应的数据库方言

    public TwoConfig(JpaProperties jpaProperties, DataSource secondaryDataSource) {
        this.jpaProperties = jpaProperties;
        this.secondaryDataSource = secondaryDataSource;
    }

    @Bean(name = "entityManagerSecondary")
    public EntityManager primaryEntityManager(@Qualifier("entityManagerFactorySecondary") EntityManagerFactory
                                                      entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    /**
     *
     * @param builder
     * @return
     */
    @Bean(name = "entityManagerFactorySecondary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySecondary(EntityManagerFactoryBuilder builder) {

        return builder
                //设置数据源
                .dataSource(secondaryDataSource)
                //设置数据源属性
                .properties(getVendorProperties(secondaryDataSource))
                //设置实体类所在位置.扫描所有带有 @Entity 注解的类
                .packages("me.fuguanghua.shineacc.entity", "me.fuguanghua.shineacc.model", "me.fuguanghua.shineacc.dao")//设置实体类所在位置
                // Spring会将EntityManagerFactory注入到Repository之中.有了 EntityManagerFactory之后,
                // Repository就能用它来创建 EntityManager 了,然后 EntityManager 就可以针对数据库执行操作
                .persistenceUnit("secondaryPersistenceUnit")
                .build();

    }

    private Map<String, Object> getVendorProperties(DataSource dataSource) {
        Map<String,String> map = new HashMap<>();
        map.put("hibernate.dialect", secondaryDialect);// 设置对应的数据库方言
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
    @Bean(name = "transactionManagerSecondary")
    PlatformTransactionManager transactionManagerPrimary(@Qualifier("entityManagerFactorySecondary") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
