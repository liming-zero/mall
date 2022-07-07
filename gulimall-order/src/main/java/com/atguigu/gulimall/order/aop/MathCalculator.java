package com.atguigu.gulimall.order.aop;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MathCalculator {

    public int div(int i, int j){
        log.info("MathCalculator...div被调用");
        return i / j;
    }
}
