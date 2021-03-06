package com.atguigu.gulimall.order.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Configuration
public class MySeataConfig {

    @Autowired
    DataSourceProperties dataSourceProperties;

    /**
     * 注入 DataSourceProxy
     * 因为 Seata 通过代理数据源实现分支事务，如果没有注入，事务无法成功回滚
     *
     * 配了数据源后SpringBoot不会使用默认的数据源
     * protected static <T> T createDataSource(DataSourceProperties properties, Class<? extends DataSource> type) {
     * 		return (T) properties.initializeDataSourceBuilder().type(type).build();
     *        }
     *
     * hikariDataSource--SpringBoot默认使用的数据源
     * @return
     */
    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties){
        HikariDataSource hikariDataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        if (StringUtils.hasText(dataSourceProperties.getName())) {
            hikariDataSource.setPoolName(dataSourceProperties.getName());
        }
        return new DataSourceProxy(hikariDataSource);
    }

    /**
     * 注册事务管理器在容器中
     * @return
     */
    @Bean
    public PlatformTransactionManager transactionManager(){
        return new DataSourceTransactionManager(dataSource(dataSourceProperties));
    }
}
