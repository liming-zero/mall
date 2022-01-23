package com.atguigu.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class OrderConfirmVo {
    /**
     * 收获地址
     */
    @Getter @Setter
    private List<MemberAddressVo> address;

    /**
     * 所有选中的购物项
     */
    @Getter @Setter
    private List<OrderItemVo> items;

    //发票信息...

    /**
     * 优惠券信息(用户的积分信息)
     */
    @Getter @Setter
    private Integer integration;

    /**
     * 订单防重复提交令牌
     */
    @Getter @Setter
    private String orderToken;

    /**
     * 订单总额
     */
    //private BigDecimal total;

    /**
     * 应付价格
     */
    //private BigDecimal payPrice;

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null){
            for (OrderItemVo item : items) {
                //总价 = 价格 * 数量
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum  = sum.add(multiply);
            }
        }
        return sum;
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
