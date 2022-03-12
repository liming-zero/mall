package com.atguigu.gulimall.ware.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.mq.OrderTo;
import com.atguigu.common.to.mq.StockLockedTo;
import com.atguigu.gulimall.ware.constant.WareRabbitConstant;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.OrderVo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RabbitListener(queues = {WareRabbitConstant.WARE_RELEASE_QUEUE})
public class StockReleaseListener {

    @Autowired
    private WareSkuService wareSkuService;


    /**
     * RabbitMQ订单库存解锁
     * 1)、库存自动解锁
     * 下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚。之前锁定的库存就需要自动解锁。
     * 2)、下订单失败
     * 锁库存失败
     */
    @RabbitHandler
    public void handleStockLockedRelease(Message message, StockLockedTo lockedTo, Channel channel) throws IOException {
        System.out.println("接收到解锁库存的消息--->" + JSON.toJSONString(lockedTo));

        /**
         * 解锁
         * 查询数据库关于这个订单的锁定库存信息
         *    1、有库存：证明库存锁定成功了
         *      解锁：订单情况
         *          1.没有订单，无需解锁
         *          2.有订单，不是解锁库存。查看订单状态
         *            ①已取消，解锁库存
         *            ②没取消，不能解锁
         *    2、没有库存：库存锁定失败了，库存回滚了。这种情况无需解锁。
         *    3.只要解锁库存的消息失败，一定要手动告诉服务解锁失败。
         */
        try{
            /**
             * 如何保证消息可靠性-消息重复
             * 1、消息消费成功，事务已经提交，ack时，机器宕机。导致没有ack成功，Broker的消息重新由unack变为ready，并发送给其他消费者。
             * 2、消息消费失败，由于重试机制，自动又将消息发送出去
             * 3、成功消费，ack时宕机，消息有unack变为ready，Broker又重新发送。
             *   ①消费者的业务消费接口应该设计为幂等性的，比如扣库存有工作单的状态标志。
             *   ②使用防重表(redis/mysql),发送消息每一个都有业务的唯一标识，处理过就不用处理。
             *   ③rabbitMQ的每一个消息都有redelivered字段，可以获取是否是被重新投递过来的，而不是第一次投递过来的。
             */

            /**
             * 如何保证消息可靠性-消息积压
             * 1、消费者宕机积压
             * 2、消费者消费能力不足积压
             * 3、发送者发送流量太大
             *    ①上线更多的消费者，进行正常消费
             *    ②上线专门的队列消费服务，将消息先批量取出来，记录数据库，离线慢慢处理。
             */
            //当前消息是否是被第二次及以后(重新)派发过来的。
            //Boolean redelivered = message.getMessageProperties().getRedelivered();
            wareSkuService.unLockStock(lockedTo);
            //手动确认消息接收
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        catch (Exception e){
            log.error("RabbitMQ订单库存解锁异常，原因:{}",e.getMessage());
            //消息拒绝以后重新放到队列里面，让别人继续消费解锁
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            throw e;
        }
    }

    /**
     * 订单如果关闭，库存服务应该主动解锁库存
     */
    @RabbitHandler
    public void handleOrderCloseRelease(Message message, OrderTo orderVo, Channel channel) throws IOException {
        System.out.println("收到订单服务消息，订单关闭，准备解锁库存--->" + JSON.toJSONString(orderVo));
        try{
            wareSkuService.unLockStock(orderVo);
            //手动确认消息接收
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        catch (Exception e){
            log.error("RabbitMQ订单库存解锁异常，原因:{}",e.getMessage());
            //消息拒绝以后重新放到队列里面，让别人继续消费解锁
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            throw e;
        }
    }
}
