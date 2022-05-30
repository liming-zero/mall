package com.atguigu.gulimall.seckill.stateMachine.business.service;

import com.atguigu.gulimall.seckill.stateMachine.business.enums.CreditFlowEnum;
import com.atguigu.gulimall.seckill.stateMachine.business.executor.AbstractGuliExecutor;
import com.atguigu.gulimall.seckill.stateMachine.enums.BizStatusEnum;
import com.atguigu.gulimall.seckill.stateMachine.enums.IFlowNodeEnum;
import com.atguigu.gulimall.seckill.stateMachine.vo.ContextBO;
import org.springframework.stereotype.Service;

@Service
public class GuliEndService extends AbstractGuliExecutor {
    @Override
    public BizStatusEnum process(ContextBO contextBO) {
        return BizStatusEnum.SUCCESS;
    }

    @Override
    public IFlowNodeEnum getAction() {
        return CreditFlowEnum.CREDIT_APPLY_END;
    }
}
