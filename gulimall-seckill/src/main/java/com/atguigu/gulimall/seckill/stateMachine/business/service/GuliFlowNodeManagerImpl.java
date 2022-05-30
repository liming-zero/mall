package com.atguigu.gulimall.seckill.stateMachine.business.service;

import com.atguigu.gulimall.seckill.stateMachine.business.enums.BizTypeEnum;
import com.atguigu.gulimall.seckill.stateMachine.business.enums.CreditFlowEnum;
import com.atguigu.gulimall.seckill.stateMachine.business.enums.CreditFlowStatusEnum;
import com.atguigu.gulimall.seckill.stateMachine.business.enums.ProductEnum;
import com.atguigu.gulimall.seckill.stateMachine.manager.impl.IFlowNodeManagerImpl;
import com.atguigu.gulimall.seckill.stateMachine.node.FlowNode;

public class GuliFlowNodeManagerImpl extends IFlowNodeManagerImpl {

    @Override
    public void afterPropertiesSet() throws Exception {
        flowNodes.add(new FlowNode(CreditFlowEnum.CREDIT_APPLY_VALIDATE, CreditFlowStatusEnum.PENDING, CreditFlowStatusEnum.FAIL,
                CreditFlowStatusEnum.NEXT_PENDING, null, true, true, false));
    }

    @Override
    public Integer getProductCode() {
        return ProductEnum.GULIMALL.getCode();
    }

    @Override
    public String getBizType() {
        return BizTypeEnum.BIZ_CREDIT.getCode();
    }
}
