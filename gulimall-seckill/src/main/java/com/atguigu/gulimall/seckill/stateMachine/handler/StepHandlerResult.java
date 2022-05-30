package com.atguigu.gulimall.seckill.stateMachine.handler;

import com.atguigu.gulimall.seckill.stateMachine.enums.BizStatusEnum;

import java.io.Serializable;

public class StepHandlerResult<T> implements Serializable {

    private String resultCode;

    private String resultMsg;

    private T resultParam;

    public static <T> StepHandlerResult<T> success(T resultParam){
        StepHandlerResult<T> result = new StepHandlerResult<>();
        result.setResultCode(BizStatusEnum.SUCCESS.getCode());
        result.setResultMsg(BizStatusEnum.SUCCESS.getDesc());
        result.setResultParam(resultParam);
        return result;
    }

    public static <T> StepHandlerResult<T> failed(String resultMsg){
        StepHandlerResult<T> result = new StepHandlerResult<>();
        result.setResultCode(BizStatusEnum.FAIL.getCode());
        result.setResultMsg(resultMsg);
        return result;
    }

    public static <T> StepHandlerResult<T> failed(String resultMsg, T resultParam){
        StepHandlerResult<T> result = new StepHandlerResult<>();
        result.setResultCode(BizStatusEnum.FAIL.getCode());
        result.setResultMsg(resultMsg);
        result.setResultParam(resultParam);
        return result;
    }

    public static <T> StepHandlerResult<T> process(String resultMsg, T resultParam){
        StepHandlerResult<T> result = new StepHandlerResult<>();
        result.setResultCode(BizStatusEnum.PROCESS.getCode());
        result.setResultMsg(resultMsg);
        result.setResultParam(resultParam);
        return result;
    }

    public StepHandlerResult() {
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public T getResultParam() {
        return resultParam;
    }

    public void setResultParam(T resultParam) {
        this.resultParam = resultParam;
    }
}
