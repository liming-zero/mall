package com.atguigu.gulimall.order.service;

import com.atguigu.common.to.mq.SeckillOrderTo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author liming-zero
 * @email limingzero@outlook.com
 * @date 2021-05-03 16:16:35
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 订单确认页返回需要的数据
     * @return
     */
    OrderConfirmVo confirmOrder() throws InterruptedException, ExecutionException;

    SubmitOrderRespVo submitOrder(OrderSubmitVo vo);

    OrderEntity getOrderStatus(String orderSn);

    void closeOrder(OrderEntity order);

    PayVo getOrderPay(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    String handePayResult(PayAsyncVo asyncVo);

    void createSeckillOrder(SeckillOrderTo seckillOrder);
}

