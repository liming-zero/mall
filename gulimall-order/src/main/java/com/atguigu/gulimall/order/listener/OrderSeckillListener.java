package com.atguigu.gulimall.order.listener;

import com.atguigu.common.to.mq.SeckillOrderTo;
import com.atguigu.gulimall.order.constant.OrderRabbitConstant;
import com.atguigu.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RabbitListener(queues = OrderRabbitConstant.ORDER_SECKILL_QUEUE)
@Component
public class OrderSeckillListener {

    @Autowired
    private OrderService orderService;

    /**
     * 监听消息
     */
    @RabbitHandler
    public void listener(SeckillOrderTo seckillOrder, Channel channel, Message message) throws IOException {
        log.info("准备创建秒杀单的详细信息......");
        try{
            orderService.createSeckillOrder(seckillOrder);
            //由于时延问题(订单库存解锁了，支付成功异步消息才到)，需要手动调用支付宝收单。
            //String res = alipayTemplate.closeOrder(order.getOrderSn());
            //System.out.println(res);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            //拒绝消息  requeue：重新回到消息队列里面
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
