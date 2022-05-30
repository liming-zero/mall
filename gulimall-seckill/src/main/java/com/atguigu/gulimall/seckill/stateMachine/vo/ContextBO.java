package com.atguigu.gulimall.seckill.stateMachine.vo;

import java.util.Map;

public class ContextBO {

    private String bizId;

    private Integer productCode;

    private Map<String, Object> bizParam;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getProductCode() {
        return productCode;
    }

    public void setProductCode(Integer productCode) {
        this.productCode = productCode;
    }

    public Map<String, Object> getBizParam() {
        return bizParam;
    }

    public void setBizParam(Map<String, Object> bizParam) {
        this.bizParam = bizParam;
    }
}
