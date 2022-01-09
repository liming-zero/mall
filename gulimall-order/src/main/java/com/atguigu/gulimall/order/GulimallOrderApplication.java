package com.atguigu.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

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
 */
@EnableRabbit
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.order.dao")
@SpringBootApplication
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
