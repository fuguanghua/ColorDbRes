package me.fuguanghua.sync2dbc.druid.mutids;

import com.alibaba.druid.pool.DruidPooledConnection;

import java.sql.SQLException;

public class DbHelper {
    private static DbHelper instance = null;

    private DbHelper() {
    }

    /**
     * @Description: 获取实例
     */
    public synchronized static DbHelper getInstance() {
        if (instance == null) {
            instance = new DbHelper();
        }
        return instance;
    }

    /**
     * @return com.alibaba.druid.pool.DruidDataSource
     * @Description: 获取数据库连接池
     */
    public com.alibaba.druid.pool.DruidDataSource getDbPool() {
        com.alibaba.druid.pool.DruidDataSource dataSource = SpringUtils.getBean("secondaryDataSource");
        return dataSource;
    }
    // @Autowired
    // @Qualifier("secondaryDataSource")
    // DruidDataSource druidDataSource;

    public DruidPooledConnection getConnection() {
        DruidPooledConnection conn = null;
        try {
            conn = getDbPool().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
