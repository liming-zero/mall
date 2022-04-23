package com.atguigu.gulimall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1、如何使用Nacos作为配置中心统一管理配置
 *      1)、引入依赖
 *      <dependency>
 *          <groupId>com.alibaba.cloud</groupId>
 *          <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
 *      </dependency>
 *      2)、创建一个bootstrap.properties
 *         ①配置当前应用的名称
 *         ②配置Nacos作为配置中心的地址
 *      3)、给配置中心默认添加一个数据集(Data Id)  默认规则：应用名.properties
 *      4)、给应用名.properties添加任何配置
 *      5)、动态获取配置，使用两个注解
 *        @RefreshScope   //刷新Nacos配置文件的value
 *        @Value("${配置项的名}"): 获取到配置
 *        如果配置中心和当前应用的配置文件都相同，优先有用配置中心的参数配置。
 * 2、细节
 *      1)、命名空间：做配置隔离使用
 *      2)、配置集
 *      3)、配置集ID
 *      4)、配置分组
 */
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.coupon.dao")
@SpringBootApplication
public class GulimallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}
