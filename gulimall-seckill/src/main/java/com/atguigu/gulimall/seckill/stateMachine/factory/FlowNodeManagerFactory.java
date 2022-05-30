package com.atguigu.gulimall.seckill.stateMachine.factory;

import com.atguigu.gulimall.seckill.stateMachine.business.enums.BizTypeEnum;
import com.atguigu.gulimall.seckill.stateMachine.manager.IFlowNodeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FlowNodeManagerFactory {

    private static Map<String, IFlowNodeManager> flowNodeManagerMap = new HashMap<>();

    /**
     * 初始化业务流程
     * @param flowNodeManagers
     */
    @Autowired(required = false)
    private void init(List<IFlowNodeManager> flowNodeManagers){
        if (flowNodeManagers != null){
            for (IFlowNodeManager flowNodeManager : flowNodeManagers) {
                flowNodeManagerMap.put(flowNodeManager.getProductCode() + "_" + flowNodeManager.getBizType(), flowNodeManager);
            }
        }
    }

    public IFlowNodeManager getFlowNodeManagerByProduct(Integer productCode, BizTypeEnum bizTypeEnum){
        if (productCode == null || bizTypeEnum == null){
            throw new NullPointerException();
        }
        IFlowNodeManager flowNodeManager = flowNodeManagerMap.get(productCode + "_" + bizTypeEnum.getCode());
        if (flowNodeManager == null){
            throw new NullPointerException();
        }
        return flowNodeManager;
    }
}
