package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MyRedissonConfig {

    /**
     * 所有对Redisson的使用都是通过RedissonClient对象
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown") //服务停止后销毁此对象的方法shutdown
    public RedissonClient redisson() throws IOException {
        //1.创建配置
        Config config = new Config();
        config.useSingleServer()    //单节点模式Redis url should start with redis:// or rediss:// (for SSL connection)
                .setAddress("redis://192.168.247.130:6379");
        //2.根据config对象创建出RedissonClient实例
        return Redisson.create(config);
    }

}
