package com.atguigu.gulimall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * SpringSession核心原理：
 *  1）、@EnableRedisHttpSession 注解导入了RedisHttpSessionConfiguration配置
 *      RedisHttpSessionConfiguration配置：
 *      1.给容器中添加了一个组件
 *          sessionRepository--->【RedisIndexedSessionRepository】：redis操作session。session的增删改查封装类
 *      2.SessionRepositoryFilter--》Filter：session存储过滤器；每个请求过来都必须经过Filter
 *          1、创建的时候，就自动从容器中初始化获取到了sessionRepository
 *          2、原始的request、response都被包装成了SessionRepositoryRequestWrapper，SessionRepositoryResponseWrapper
 *          3、以后获取session，都是获取包装后的session。利用 ，拦截request.getSession()
 *          4、wrappedRequest.getSession();  --->sessionRepository中获取到的
 *
 *          刷新浏览器session自动延期，redis中的数据也是有过期时间的
 */
@EnableRedisHttpSession //整合redis作为session缓存
@EnableFeignClients(basePackages = "com.atguigu.gulimall.auth.feign") //开启远程服务调用功能,扫描此包下的所有接口
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallAuthServerApplication.class, args);
    }

}
