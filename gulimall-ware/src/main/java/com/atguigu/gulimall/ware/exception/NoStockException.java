package com.atguigu.gulimall.ware.exception;

public class NoStockException extends RuntimeException{
    public NoStockException(Long skuId){
        super("商品ID:" + skuId + ";没有足够的库存了");
    }
}
