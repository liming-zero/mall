package com.atguigu.gulimall.seckill.stateMachine.enums;

public enum BizStatusEnum {

    SUCCESS("SUCCESS", "成功"),
    PROCESS("PROCESS", "处理中"),
    FAIL("FAIL", "失败");


    BizStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取当前枚举
     * @param code
     * @return
     */
    public static BizStatusEnum getEnumByCode(String code){
        for (BizStatusEnum bizStatusEnum : BizStatusEnum.values()) {
            if (bizStatusEnum.getCode().equals(code)){
                return bizStatusEnum;
            }
        }
        return null;
    }

    private String code;

    private String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
