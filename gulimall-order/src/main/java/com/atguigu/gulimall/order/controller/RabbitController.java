package com.atguigu.gulimall.order.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("/sendMq")
    public String sendMq(@RequestParam(value = "num", defaultValue = "10") Integer num){
        for (int i = 0; i < num; i++) {
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
        return "OK";
    }
}
