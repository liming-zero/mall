package com.atguigu.gulimall.seckill.stateMachine.enums;

import com.atguigu.gulimall.seckill.stateMachine.business.enums.CreditFlowEnum;

public enum StepHandlerEnum {

    CREDIT_APPLY_VALIDATE(CreditFlowEnum.CREDIT_APPLY_VALIDATE, "申请效验");

    private IFlowNodeEnum flowNode;

    private String actionCode;

    StepHandlerEnum(IFlowNodeEnum flowNode, String actionCode) {
        this.flowNode = flowNode;
        this.actionCode = actionCode;
    }

    public IFlowNodeEnum getFlowNode() {
        return flowNode;
    }

    public String getActionCode() {
        return actionCode;
    }

    public static String getDescByCode(String code){
        for (StepHandlerEnum stepHandlerEnum : StepHandlerEnum.values()) {
            if (stepHandlerEnum.toString().equals(code)){
                return stepHandlerEnum.getActionCode();
            }
        }
        return null;
    }
}
