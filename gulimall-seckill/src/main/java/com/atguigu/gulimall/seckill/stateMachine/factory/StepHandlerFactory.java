package com.atguigu.gulimall.seckill.stateMachine.factory;

import com.atguigu.gulimall.seckill.stateMachine.business.enums.ProductEnum;
import com.atguigu.gulimall.seckill.stateMachine.enums.StepHandlerEnum;
import com.atguigu.gulimall.seckill.stateMachine.handler.StepHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StepHandlerFactory {

    private static Map<String, StepHandler> stepHandlerMap;

    @Autowired
    private void init(List<StepHandler> stepHandlers){
        for (StepHandler stepHandler : stepHandlers) {
            stepHandlerMap.put(stepHandler.getProductCode() + "_" + stepHandler.getNodeAtomCode().name(), stepHandler);
        }
    }

    public StepHandler getStepHandler(String productCode, StepHandlerEnum stepHandlerEnum){
        Assert.notNull(productCode, "");
        Assert.notNull(stepHandlerEnum, "");

        String key = productCode + "_" + stepHandlerEnum.name();
        StepHandler stepHandler = stepHandlerMap.get(key);
        if (stepHandler == null){
            key = ProductEnum.GULIMALL.getCode() + "_" + stepHandlerEnum.name();
            stepHandler = stepHandlerMap.get(key);
        }
        return stepHandler;
    }
}
