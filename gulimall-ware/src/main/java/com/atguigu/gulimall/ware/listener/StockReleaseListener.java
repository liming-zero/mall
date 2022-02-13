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
