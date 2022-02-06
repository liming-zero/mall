package com.atguigu.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 使用RabbitMQ
 *  1.引入amqp场景启动器
 *    RabbitAutoConfiguration就会自动生效
 *  2.给容器中自动配置了RabbitTemplate、AmqpAdmin、CachingConnectionFactory、RabbitMessagingTemplate
 *    所有的属性都是在
 *      @ConfigurationProperties(prefix = "spring.rabbitmq") spring.rabbitmq配置文件进行绑定
 *      给配置文件中配置 spring.rabbitmq 信息
 *  3.@EnableRabbit:开启Rabbit功能
 *  4.交换机不同，路由键不同，消息就会到达不同的队列
 *  5.监听消息：使用@RabbitListener注解，必须先开启@EnableRabbit注解
 *    ① @RabbitListener：类 + 方法上（监听哪些队列即可）
 *    ② @RabbitHandler：标注在方法上（重载区分不同的消息）
 *
 *  本地【事务】失效问题
 *  同一个对象内事务方法互调默认失效，原因：绕过了代理对象，事务是使用代理对象来控制的
 *  解决：使用代理对象来调用事务方法
 *    ①引入spring-boot-starter-aop; 引入了aspectjweaver动态代理
 *    ②@EnableAspectJAutoProxy(exposeProxy = true)
 *      开启aspectJ动态代理功能，以后所有的动态代理都是aspectj创建的。（即使没有接口也可以代理，JDK默认的动态代理必须有接口）
 *      exposeProxy = true： 对外暴露代理对象
 *    ③本类互调
 *      OrderServiceImpl orderService = (OrderServiceImpl) AopContext.currentProxy();
 *      orderService.a();
 *      orderService.b();
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFeignClients
@EnableRedisHttpSession
@EnableRabbit
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.order.dao")
@SpringBootApplication
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
