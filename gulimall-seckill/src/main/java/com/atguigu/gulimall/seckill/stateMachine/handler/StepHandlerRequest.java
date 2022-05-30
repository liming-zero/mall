package com.atguigu.gulimall.seckill.stateMachine.handler;

import java.io.Serializable;

public class StepHandlerRequest<T> implements Serializable {

    private T requestParam;

    public StepHandlerRequest() {
    }

    public StepHandlerRequest(T requestParam) {
        this.requestParam = requestParam;
    }

    public T getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(T requestParam) {
        this.requestParam = requestParam;
    }
}
