package com.atguigu.gulimall.seckill.stateMachine.handler;

import com.atguigu.gulimall.seckill.stateMachine.enums.StepHandlerEnum;

public interface StepHandler {

    /**
     * 原子服务处理函数
     */
    StepHandlerResult handle(StepHandlerRequest request);

    /**
     * 原子服务节点获取
     * @return
     */
    StepHandlerEnum getNodeAtomCode();

    /**
     * 产品号 默认default
     */
    Integer getProductCode();
}
