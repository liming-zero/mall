package com.atguigu.gulimall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter(){
        MessageConverter messageConverter = new Jackson2JsonMessageConverter();
        return messageConverter;
    }

    /**
     * 定制RabbitTemplate
     * 1、服务收到消息就回调
     *      1.配置文件spring.rabbitmq.publisher-confirm-type=correlated
     *      2.设置确认回调
     * 2、消息正确抵达队列进行回调
     *      1.spring.rabbitmq.publisher-returns=true
     *        spring.rabbitmq.template.mandatory=true
     *      2.设置确认回调ReturnCallback
     * 3、消费端确认(避免网络抖动服务器宕机等因素导致消息没有收到，保证每一个消息被正确消费，此时broker才可以删除这个消息)
     *      配置文件spring.rabbitmq.listener.simple.acknowledge-mode=manual
     *      1.默认是自动确认的，只要消息接受到，客户端会自动确认，服务端就会移除这个消息
     *          问题：
     *              我们收到很多消息，自动回复给服务器ack，只有一个消息处理成功，宕机了，发生消息丢失。
     *              需要手动确认。只要我们没有明确告诉MQ，消息被签收。没有Ack，消息就一直是unacked没有被签收状态。即使Consumer宕机，消息不会丢失，会重新变为Ready状态，下一次有新的Consumer连接进来就发给它
     *      2.如何签收消息
     *          channel.basicAck(deliveryTag,false);        签收消息  业务成功完成就应该签收
     *          channel.basicNack(deliveryTag,false,false); 拒签消息  业务失败，拒签
     */
    @PostConstruct  //MyRabbitConfig对象创建完成以后，执行这个方法
    public void initRabbitTemplate(){
        //1、设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 只要消息抵达Broker代理服务器  b=true
             *
             * @param correlationData   当前消息的唯一关联数据(这个是消息的唯一id)
             * @param ack 消息是否成功收到
             * @param cause 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirm...correlationData--->" + correlationData + "===消息是否成功接受--->" + ack + "===失败原因--->" + cause);
            }
        });

        //2、设置消息抵达队列的确认回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 只要消息没有投递给指定的队列，就触发这个失败回调
             * @param message 投递失败的消息的详细信息
             * @param replyCode 回复的状态码
             * @param replyText 回复的文本内容
             * @param exchange  这个消息发给哪个交换机
             * @param routingKey 这个消息用哪个路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("投递消息失败，详细信息--->" + message + "===状态码--->" + replyCode + "===失败原因--->" + replyText + "===发送的交换机--->" + exchange + "===所用的路由键--->" + routingKey);
            }
        });
    }
}
