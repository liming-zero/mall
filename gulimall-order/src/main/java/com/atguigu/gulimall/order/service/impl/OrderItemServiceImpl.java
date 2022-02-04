package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderItemDao;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.service.OrderItemService;


@Slf4j
@RabbitListener(queues = {"hello-java-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 接受消息
     * @RabbitListener
     * queues: 声明所有需要监听的队列
     *  标注在业务逻辑组件上，并且这个组件必须在容器中才能起作用
     *
     * 形参可以写以下类型
     *  1、Message message:原生消息详细信息，头+体
     *  2、T<发送的消息类型> OrderEntity order
     *  3、Channel channel:当前传输数据的通道
     *
     * Queue:可以很多人都监听，但是只能有一个人获取消息
     *
     * 场景：
     *  1、订单服务启动多个；同一个消息，只能有一个客户端收到
     *  2、只有一个消息完全处理完，方法运行结束，我们就可以接收到下一个消息
     */
    //@RabbitListener(queues = {"hello-java-queue"})
    @RabbitHandler
    public void receiveMessage(Message message, OrderEntity order, Channel channel){
        //body:消息体，消息本身
        byte[] body = message.getBody();
        //messageProperties消息头属性信息
        MessageProperties messageProperties = message.getMessageProperties();
        log.info("接受到消息内容---》{}，类型--》{}，内容--》{}",message,message.getClass().getName(),order);
        //deliveryTag: channel内接收消息按顺序自增的
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //basicAck签收消息，第二个参数multiple代表是否批量签收
        try {
            if (deliveryTag % 2 == 0){
                //签收消息
                channel.basicAck(deliveryTag,false);
                System.out.println("签收了消息--->" + deliveryTag);
            }
            else{
                //long deliveryTag, boolean multiple, boolean requeue
                //requeue（true代表消息发回服务器，重新入队列）
                //拒收消息
                channel.basicNack(deliveryTag,false,false);
                System.out.println("没有签收消息--->" + deliveryTag);
            }
        } catch (Exception e) {
            //网络中断
            e.printStackTrace();
        }
    }

    @RabbitHandler
    public void receiveMessage(Message message,OrderReturnReasonEntity reasonEntity,Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //签收消息
        channel.basicAck(deliveryTag,false);
        System.out.println("接受到消息...." + reasonEntity);
    }

}