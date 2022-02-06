package com.atguigu.gulimall.order.exception;

public class NoStockException extends RuntimeException{
    public NoStockException(String msg){
        super(msg);
    }
}
