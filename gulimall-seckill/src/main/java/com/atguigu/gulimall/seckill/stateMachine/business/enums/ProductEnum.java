package com.atguigu.gulimall.seckill.stateMachine.business.enums;

public enum ProductEnum {
    GULIMALL(100001, "谷粒商城");

    private Integer code;

    private String desc;

    ProductEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
