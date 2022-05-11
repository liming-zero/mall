package com.atguigu.gulimall.order.constant;

public class OrderRabbitConstant {
    public static final String EXCHANGE = "x-dead-letter-exchange";
    public static final String ROUTING_KEY = "x-dead-letter-routing-key";
    public static final String TTL = "x-message-ttl";
    public static final String ORDER_EXCHANGE= "order-event-exchange";
    public static final String ORDER_DELAY_QUEUE= "order.delay.queue";
    public static final String ORDER_RELEASE_QUEUE= "order.release.order.queue";
    public static final String ORDER_CREATE_ROUTING_KEY= "order.create.order";
    public static final String ORDER_RELEASE_ROUTING_KEY= "order.release.order";
    //订单绑定库存服务队列
    public static final String ORDER_RELEASE_STOCK_QUEUE= "stock.release.stock.queue";
    //订单绑定库存服务队列路由键
    public static final String ORDER_RELEASE_OTHER_ROUTING_KEY= "order.release.other.#";
    //监听秒杀服务，削峰队列
    public static final String ORDER_SECKILL_QUEUE= "order.seckill.order.queue";
    //秒杀服务->订单服务路由键
    public static final String ORDER_SECKILL_ROUTING_KEY= "order.seckill.order";
}
