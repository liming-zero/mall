package com.atguigu.gulimall.order;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@ToString
@SpringBootTest
class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 测试发送消息
     */
    @Test
    public void sendMessage() {
        /**
         * 发送消息，如果发送的消息是个对象，我们会使用序列化机制，将对象写出去。所以要求对象必须实现Serializable接口
         * 1、此处MQ接受的消息为application/x-java-serialized-object格式
         * 2、可以将对象转换为JSON发出去
         *    配置MessageConverter接口实现类Jackson2JsonMessageConverter
         * new CorrelationData(UUID.randomUUID().toString()) 代表消息相关联的唯一id
         */
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setOrderSn("1111");
                orderEntity.setCreateTime(new Date());
                orderEntity.setBillReceiverPhone("16621735515");
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", orderEntity, new CorrelationData(UUID.randomUUID().toString()));
                log.info("发送消息成功，交换机--》{}，队列--》{}，消息：{}", "hello-java-exchange", "hello-java-queue", JSON.toJSONString(orderEntity));
            } else {
                OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
                reasonEntity.setId(1L);
                reasonEntity.setCreateTime(new Date());
                reasonEntity.setName("哈哈" + i);
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", reasonEntity, new CorrelationData(UUID.randomUUID().toString()));
                log.info("发送消息成功，交换机--》{}，队列--》{}，消息：{}", "hello-java-exchange", "hello-java-queue", JSON.toJSONString(reasonEntity));
            }
        }
    }


    /**
     * 1、如何创建Exchange[hello-java-exchange]、Queue、Binding
     * 1）、使用RabbitAdmin进行创建
     * 2.如何收发消息
     */
    @Test
    public void createExchange() {
        /**
         * String name:交换机名称
         * boolean durable:是否是可持久化的
         * boolean autoDelete:是否是自动删除的
         * Map<String, Object> arguments:参数信息
         * DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)全参构造器
         */
        DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
        log.info("交换机创建完成,Exchange---》{}:", "hello-java-exchange");
    }

    /**
     * 创建队列
     */
    @Test
    public void createQueue() {
        /**
         * 全参构造器
         * String name:队列名
         * boolean durable:是否可持久化
         * boolean exclusive:是否排他的。如果是只能被声明了的链接使用
         * boolean autoDelete:是否是自动删除的
         * Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments)
         */
        Queue queue = new Queue("hello-java-queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("队列创建完成,Queue---》{}:", "hello-java-queue");
    }

    /**
     * 将交换机与队列绑定
     */
    @Test
    public void createBinding() {
        /**
         * String destination:目的地，代表将哪个交换机跟哪个目的地进行绑定。当前目的地是队列,使用routingKey作为指定的路由键
         * DestinationType destinationType:目的地类型
         * String exchange:交换机
         * String routingKey:路由键
         * String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments
         */
        Binding binding = new Binding(
                "hello-java-queue",
                Binding.DestinationType.QUEUE,
                "hello-java-exchange",
                "hello.java",
                null);
        amqpAdmin.declareBinding(binding);
        log.info("绑定成功，交换机-->{},队列-->{}", "hello-java-exchange", "hello-java-queue");
    }

    @Test
    void contextLoads() {
    }

}
