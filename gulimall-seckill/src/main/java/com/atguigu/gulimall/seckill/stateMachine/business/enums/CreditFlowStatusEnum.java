package com.atguigu.gulimall.seckill.stateMachine.business.enums;

import com.atguigu.gulimall.seckill.stateMachine.enums.IFlowNodeStatus;

/**
 * 状态机code枚举
 */
public enum CreditFlowStatusEnum implements IFlowNodeStatus {
    PENDING("1001", "初始状态"),
    PROCESS("2001", "处理中"),
    FAIL("3001", "失败"),

    NEXT_PENDING("1011", "下一节点初始状态"),

    SUCCESS_PENDING("200", "下一节点初始状态");

    /**
     * 流程状态码
     */
    private String code;

    /**
     * 流程状态描述
     */
    private String msg;

    CreditFlowStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getFlowStatusCode() {
        return code;
    }

    @Override
    public String getFlowStatusDesc() {
        return msg;
    }

    public static String getFlowStateMsg(String flowNodeCode){
        CreditFlowStatusEnum[] values = values();
        for (CreditFlowStatusEnum statusEnum : values) {
            if (statusEnum.getFlowStatusCode().equals(flowNodeCode)){
                return statusEnum.getFlowStatusDesc();
            }
        }
        return null;
    }
}
