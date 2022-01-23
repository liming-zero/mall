package com.atguigu.gulimall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 整个购物车数据模型抽取
 *      需要计算的属性，必须重写它的get方法，保证每次获取属性都会进行计算
 */

public class Cart {
    List<CartItem> items;
    private Integer countNum;  //商品数量
    private Integer countType;  //商品类型数量
    private BigDecimal totalAmount; //商品总价
    private BigDecimal reduce = new BigDecimal("0.00");      //优惠减免价格

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    /**
     * 动态计算商品的总量
     * @return
     */
    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size() > 0){
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    /**
     * 商品类型数量动态获取
     * @return
     */
    public Integer getCountType() {
        int count = 0;
        if (items != null && items.size() > 0){
            for (CartItem item : items) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * 动态计算总价
     * @return
     */
    public BigDecimal getTotalAmount() {
        //1、计算购物项总价
        BigDecimal amount = new BigDecimal("0");
        if (items != null && items.size() > 0){
            for (CartItem item : items) {
                if (item.getCheck()){
                    BigDecimal totalPrice = item.getTotalPrice();
                    amount = amount.add(totalPrice);
                }
            }
        }

        //2、减去优惠总价
        BigDecimal subtract = amount.subtract(getReduce());
        return subtract;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
