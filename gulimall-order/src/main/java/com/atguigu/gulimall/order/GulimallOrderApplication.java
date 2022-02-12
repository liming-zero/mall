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
 *
 *  Seata控制分布式事务
 *  1、每一个i微服务先必须创建undo_log表
 *  2、安装事务协调器：seata-server  https://github.com/seata/seata/releases
 *  3、整合
 *      1.guli-common添加依赖spring-cloud-starter-alibaba-seata seata-all-1.3.0
 *      2.解压并启动seata-server
 *        ①registry.conf注册中心相关的配置   修改registry type=nacos 配置使用file.conf
 *      3.所有想要用到分布式事务的微服务，都要使用seata DataSourceProxy代理自己的数据源
 *      4.每个微服务，都必须导入file.conf、registry.conf两个文件
 *        在 org.springframework.cloud:spring-cloud-starter-alibaba-seata 的org.springframework.cloud.alibaba.seata.GlobalTransactionAutoConfiguration类中，
 *        默认会使用 ${spring.application.name}-fescar-service-group作为服务名注册到 Seata Server上，如果和file.conf 中的配置不一致，会提示 no available server to connect错误
 *        也可以通过配置 spring.cloud.alibaba.seata.tx-service-group修改后缀，但是必须和file.conf中的配置保持一致
 *      5.给分布式大事务的入口标注@GlobalTransactional注解
 *      6.每一个远程的小事务使用@Transactional注解
 *      7.启动测试
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
