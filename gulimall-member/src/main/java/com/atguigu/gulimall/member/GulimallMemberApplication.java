package com.atguigu.gulimall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.atguigu.gulimall.member.feign") //开启远程服务调用功能,扫描此包下的所有接口
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.member.dao")
@SpringBootApplication
public class GulimallMemberApplication {


    public static void main(String[] args) {
        SpringApplication.run(GulimallMemberApplication.class, args);
    }

}
