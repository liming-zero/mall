package com.atguigu.common.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement    //开启事务
public class MybatisPageConfig {

    //引入分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor interceptor = new PaginationInterceptor();
        //设置请求的页面大于最大页后操作，true回到首页，false继续请求，默认false
        interceptor.setOverflow(true);
        //设置最大单页数量，默认500条，-1不受限制
        interceptor.setLimit(1000);
        return interceptor;
    }
}

