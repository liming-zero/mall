package com.atguigu.gulimall.seckill.stateMachine.retry.vo;

public class RetryResult {

    private boolean executeStatus;

    private String errorMsg;

    public boolean isExecuteStatus() {
        return executeStatus;
    }

    public void setExecuteStatus(boolean executeStatus) {
        this.executeStatus = executeStatus;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
