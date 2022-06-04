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
 * 4、使用sentinel来保护Feign远程调用：熔断
 *      1）、feign.sentinel.enabled=true  需要加入springcloud-alibaba和openfeign依赖
 *      2）、调用方手动指定远程服务的降级策略。远程服务被降级处理，触发我们的熔断方法。
 *      3）、在超大流量的时候，必须牺牲一些远程服务，在服务的提供方（远程服务）指定降级策略。
 *          提供方是在运行，但是不运行自己的业务逻辑，返回的是默认的降级数据（限流的数据）。
 *
 * 5、自定义受保护的资源（需要配置限流以后的默认返回）
 *    1）、try(Entry entry = SphU.entry("seckillSkus")){
 *    }catch (BlockException){}
 *    2）、使用注解的方式@SentinelResource
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
