package com.atguigu.gulimall.seckill.stateMachine.business.enums;

public enum BizTypeEnum {
    BIZ_CREDIT("CREDIT", "");

    private String code;

    private String msg;

    BizTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static BizTypeEnum getEnumByCode(String code){
        BizTypeEnum[] values = values();
        for (BizTypeEnum typeEnum : values) {
            if (typeEnum.getCode().equals(code)){
                return typeEnum;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
