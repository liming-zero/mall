package com.atguigu.gulimall.seckill.stateMachine.vo;

import java.util.Map;

public class ContextBO {

    private String bizId;

    private String productCode;

    private Map<String, Object> bizParam;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Map<String, Object> getBizParam() {
        return bizParam;
    }

    public void setBizParam(Map<String, Object> bizParam) {
        this.bizParam = bizParam;
    }
}
