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
 *              ①开发、测试、生产； 利用命名空间来做环境隔离
 *                注意：在bootstrap.properties; 配置上，需要使用哪个命名空间下的配置
 *                spring.cloud.nacos.config.namespace=xxx
 *              ②每一个微服务之间互相配置隔离，每一个微服务都创建自己的命名空间，只加载自己命名空间下的配置
 *      2)、配置集：所有配置的集合
 *      3)、配置集ID：类似文件名
 *      4)、配置分组
 *          默认所有的配置都属于：DEFAULT_GROUP
 *
 *      项目中的使用：每个微服务创建自己的命名空间，使用配置分组区分环境，dev、test、prod
 *
 * 3、同时加载多个配置集
 *      1）、微服务任何配置信息，任何配置文件都可以放在配置中心中
 *      2）、只需要在bootstrap.properties说明加载配置中心中的哪些配置文件即可
 *      3）、SpringBoot任何从配置文件中获取值都可以获取到。@Value  @ConfigurationProperties等
 *      配置中心有的优先使用配置中心中的值
 */
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.coupon.dao")
@SpringBootApplication
public class GulimallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}
