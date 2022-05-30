package com.atguigu.gulimall.seckill.stateMachine.retry;

import com.atguigu.gulimall.seckill.stateMachine.retry.vo.RetryResult;
import com.atguigu.gulimall.seckill.stateMachine.vo.ContextBO;

public interface FlowNodeRetryService {

    RetryResult execute(ContextBO contextBO);
}
