package com.atguigu.gulimall.order.web;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class HelloController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @ResponseBody
    @GetMapping("/test/createOrder")
    public String testOrder(){
        OrderEntity order = new OrderEntity();
        order.setOrderSn(IdWorker.get32UUID());
        order.setCreateTime(new Date());
        order.setModifyTime(new Date());
        //给mq发送消息
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order);
        return "ok";
    }

    @GetMapping("/{page}.html")
    public String getPage(@PathVariable("page") String page){
        return page;
    }
}
