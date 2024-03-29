package com.atguigu.gulimall.order.listener;

import com.atguigu.common.annotation.SysLog;
import com.atguigu.gulimall.order.config.AlipayTemplate;
import com.atguigu.gulimall.order.constant.OrderRabbitConstant;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RabbitListener(queues = OrderRabbitConstant.ORDER_RELEASE_QUEUE)
@Component
public class OrderCloseListener {

    @Autowired
    private OrderService orderService;
    @Autowired
    private AlipayTemplate alipayTemplate;

    /**
     * 监听消息
     */
    @SysLog("监听过期的订单信息")
    @RabbitHandler
    public void listener(OrderEntity order, Channel channel, Message message) throws IOException {
        System.out.println("收到过期的订单信息，准备关闭订单" + order.getOrderSn());
        try{
            orderService.closeOrder(order);
            //由于时延问题(订单库存解锁了，支付成功异步消息才到)，需要手动调用支付宝收单。
            String res = alipayTemplate.closeOrder(order.getOrderSn());
            System.out.println(res);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            //拒绝消息  requeue：重新回到消息队列里面
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
