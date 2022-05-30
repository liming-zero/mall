package com.atguigu.gulimall.seckill.stateMachine.business.service;

import com.atguigu.gulimall.seckill.stateMachine.business.enums.CreditFlowEnum;
import com.atguigu.gulimall.seckill.stateMachine.business.executor.AbstractGuliExecutor;
import com.atguigu.gulimall.seckill.stateMachine.enums.BizStatusEnum;
import com.atguigu.gulimall.seckill.stateMachine.enums.IFlowNodeEnum;
import com.atguigu.gulimall.seckill.stateMachine.enums.StepHandlerEnum;
import com.atguigu.gulimall.seckill.stateMachine.factory.StepHandlerFactory;
import com.atguigu.gulimall.seckill.stateMachine.handler.StepHandler;
import com.atguigu.gulimall.seckill.stateMachine.handler.StepHandlerRequest;
import com.atguigu.gulimall.seckill.stateMachine.handler.StepHandlerResult;
import com.atguigu.gulimall.seckill.stateMachine.vo.ContextBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GuliValidateService extends AbstractGuliExecutor {

    @Autowired
    private StepHandlerFactory stepHandlerFactory;

    @Override
    public BizStatusEnum process(ContextBO contextBO) {
        StepHandler handler = stepHandlerFactory.getStepHandler(contextBO.getProductCode(), StepHandlerEnum.CREDIT_APPLY_VALIDATE);
        StepHandlerRequest<Object> request = new StepHandlerRequest<>();
        request.setRequestParam(contextBO.getBizParam());
        StepHandlerResult result = handler.handle(request);
        if (BizStatusEnum.FAIL.equals(result.getResultCode())){
            return BizStatusEnum.FAIL;
        }
        return BizStatusEnum.SUCCESS;
    }

    @Override
    public IFlowNodeEnum getAction() {
        return CreditFlowEnum.CREDIT_APPLY_VALIDATE;
    }
}
