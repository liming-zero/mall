package com.atguigu.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 3.JSR303(Java规范体验第303号，规定了数据效验的相关标准)
 *  1）、加入spring-boot-starter-validation依赖
 *  2）、给Bean添加效验注解 javax.validation.constraints，并定义自己的message提示
 *  3）、开启效验功能@Valid  效果：效验错误以后会有默认的响应
 *  4）、给效验的bean后紧跟一个BindingResult, 就可以获取到效验的结果
 *
 * 4.统一的异常处理
 */
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.product.dao")
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
