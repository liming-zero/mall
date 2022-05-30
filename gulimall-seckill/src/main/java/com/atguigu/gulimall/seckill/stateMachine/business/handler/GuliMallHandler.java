package com.atguigu.gulimall.seckill.stateMachine.business.handler;

import com.atguigu.gulimall.seckill.stateMachine.business.enums.ProductEnum;
import com.atguigu.gulimall.seckill.stateMachine.enums.StepHandlerEnum;
import com.atguigu.gulimall.seckill.stateMachine.handler.StepHandler;
import com.atguigu.gulimall.seckill.stateMachine.handler.StepHandlerRequest;
import com.atguigu.gulimall.seckill.stateMachine.handler.StepHandlerResult;
import org.springframework.stereotype.Component;

@Component
public class GuliMallHandler implements StepHandler {
    @Override
    public StepHandlerResult handle(StepHandlerRequest request) {
        Object requestParam = request.getRequestParam();
        return StepHandlerResult.success(requestParam);
    }

    @Override
    public StepHandlerEnum getNodeAtomCode() {
        return StepHandlerEnum.CREDIT_APPLY_VALIDATE;
    }

    @Override
    public Integer getProductCode() {
        return ProductEnum.GULIMALL.getCode();
    }
}
