package com.atguigu.gulimall.seckill.stateMachine.factory;

import com.atguigu.gulimall.seckill.stateMachine.enums.IFlowNodeEnum;
import com.atguigu.gulimall.seckill.stateMachine.executor.FlowNodeExecutor;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FlowNodeExecutorFactory {

    private static Map<IFlowNodeEnum, FlowNodeExecutor> flowNodeExecutorMap = new HashMap<>();

    @Autowired
    private void init(List<FlowNodeExecutor> executorServiceList){
        if (CollectionUtils.isNotEmpty(executorServiceList)){
            for (FlowNodeExecutor nodeExecutor : executorServiceList) {
                flowNodeExecutorMap.put(nodeExecutor.getAction(), nodeExecutor);
            }
        }
    }

    public FlowNodeExecutor getServiceByEnum(IFlowNodeEnum flowNodeEnum){
        if (flowNodeEnum == null){
            throw new NullPointerException();
        }
        FlowNodeExecutor flowNodeExecutor = flowNodeExecutorMap.get(flowNodeEnum);
        if (flowNodeExecutor == null){
            log.error("根据节点编号获取执行器不存在,flowName:{}", flowNodeEnum.getFlowName());
            throw new IllegalArgumentException();
        }
        return flowNodeExecutor;
    }
}
