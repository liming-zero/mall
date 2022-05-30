package com.atguigu.gulimall.seckill.stateMachine.business.enums;

import com.atguigu.gulimall.seckill.stateMachine.enums.IFlowNodeEnum;

/**
 * 状态机流程枚举
 */
public enum CreditFlowEnum implements IFlowNodeEnum {
    CREDIT_APPLY_VALIDATE("CREDIT_APPLY_VALIDATE", "通用效验"),
    CREDIT_APPLY_END("CREDIT_APPLY_END", "执行结束");

    CreditFlowEnum(String flowName, String flowDesc) {
        this.flowName = flowName;
        this.flowDesc = flowDesc;
    }

    private String flowName;

    private String flowDesc;

    @Override
    public String getFlowName() {
        return flowName;
    }

    @Override
    public String getFlowDesc() {
        return flowDesc;
    }
}
