package com.atguigu.gulimall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 1、整合sentinel
 *      1）、在common服务导入依赖spring-cloud-starter-alibaba-sentinel
 *      2）、下载sentinel控制台，版本需要一致
 *      3）、在yml文件中配置控制台地址信息
 *      4）、在控制台调整流量控制参数【默认所有的流量控制设置保存在内存中，服务重启失效】
 *
 * 2、配置控制台统计信息
 *      每一个微服务都导入actuator; 并在properties文件配置management.endpoints.web.exposure.include=*
 * 3、自定义sentinel的流控返回
 */
@EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GulimallSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallSeckillApplication.class, args);
    }

}
