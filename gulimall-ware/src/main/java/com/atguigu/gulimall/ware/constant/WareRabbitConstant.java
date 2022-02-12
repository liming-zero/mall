package com.atguigu.gulimall.ware.constant;

public class WareRabbitConstant {
    public static final String EXCHANGE = "x-dead-letter-exchange";
    public static final String ROUTING_KEY = "x-dead-letter-routing-key";
    public static final String TTL = "x-message-ttl";
    public static final String WARE_EXCHANGE= "stock-event-exchange";
    public static final String WARE_DELAY_QUEUE= "stock.delay.queue";
    public static final String WARE_RELEASE_QUEUE= "stock.release.stock.queue";
    public static final String WARE_RELEASE_ROUTING_KEY= "stock.release.stock";
    public static final String WARE_STOCK_RELEASE_BINDING_ROUTING_KEY= "stock.release.#";
    public static final String WARE_STOCK_LOCKED_ROUTING_KEY= "stock.locked";     //绑定死信队列
}
