package com.atguigu.gulimall.ware.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Configuration
public class MySeataConfig {

    @Autowired
    DataSourceProperties dataSourceProperties;

    /**
     * 配了数据源后SpringBoot不会使用默认的数据源
     * protected static <T> T createDataSource(DataSourceProperties properties, Class<? extends DataSource> type) {
     * 		return (T) properties.initializeDataSourceBuilder().type(type).build();
     *        }
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
}
