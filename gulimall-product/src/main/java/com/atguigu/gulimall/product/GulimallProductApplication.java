package com.atguigu.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 3.JSR303(Java规范体验第303号，规定了数据效验的相关标准)
 *  1）、加入spring-boot-starter-validation依赖
 *  2）、给Bean添加效验注解 javax.validation.constraints，并定义自己的message提示
 *  3）、开启效验功能@Valid  效果：效验错误以后会有默认的响应
 *  4）、给效验的bean后紧跟一个BindingResult, 就可以获取到效验的结果
 *  5）、分组效验
 *          * @NotBlank(message = "品牌名必须提交" ,groups = {AddGroup.class, UpdateGroup.class})
 *          * 给效验注解标注什么情况下需要效验
 *          * public R save(@Validated({AddGroup.class})
 *          * 默认没有指定分组的效验注解，在分组效验情况下不生效。如@Validated({UpdateGroup.class})必须指定
 *  6）、自定义效验
 *          * 编写一个自定义的效验注解
 *          * 编写一个自定义的效验器
 *          * 关联自定义的效验器和自定义的效验注解
 *
 * 4.统一的异常处理
 * @ControllerAdvice
 *  1）、编写异常处理类，使用@ControllerAdvice
 *  2）、使用@ExceptionHandler标注方法可以处理的异常
 *
 * 5.模板引擎
 *  1）、thymeleaf-starter:关闭缓存
 *  2）、静态资源都放在static文件夹下就可以按照路径直接访问
 *  3）、页面放在templates下，直接访问
 *      SpringBoot,访问项目的时候，默认会找index
 *
 * 6.整合redis
 *  1）、引入data-redis-starter依赖
 *  2）、配置文件配置redis的host等信息
 *  3）、使用springboot自动配置好的StringRedisTemplate来操作redis
 */
@EnableFeignClients(basePackages = {"com.atguigu.gulimall.product.feign"})
@ComponentScan("com.atguigu")
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.product.dao")
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
