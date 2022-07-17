package com.atguigu.gulimall.seckill.stateMachine.business.manager;

import com.atguigu.gulimall.seckill.stateMachine.business.enums.BizTypeEnum;
import com.atguigu.gulimall.seckill.stateMachine.business.enums.CreditFlowEnum;
import com.atguigu.gulimall.seckill.stateMachine.business.enums.CreditFlowStatusEnum;
import com.atguigu.gulimall.seckill.stateMachine.business.enums.ProductEnum;
import com.atguigu.gulimall.seckill.stateMachine.manager.impl.IFlowNodeManagerAdapter;
import com.atguigu.gulimall.seckill.stateMachine.node.FlowNode;
import org.springframework.stereotype.Component;

@Component
public class GuliFlowNodeManagerImpl extends IFlowNodeManagerAdapter {

    @Override
    public void afterPropertiesSet(){
        flowNodes.add(new FlowNode(CreditFlowEnum.CREDIT_APPLY_VALIDATE, CreditFlowStatusEnum.PENDING, CreditFlowStatusEnum.FAIL,
                CreditFlowStatusEnum.NEXT_PENDING, null, true, true, false));
        flowNodes.add(new FlowNode(CreditFlowEnum.CREDIT_APPLY_END, CreditFlowStatusEnum.NEXT_PENDING, CreditFlowStatusEnum.FAIL,
                CreditFlowStatusEnum.SUCCESS_PENDING, null, true, true, false));
        for (FlowNode flowNode : flowNodes) {
            flowNodeMap.put(flowNode.getCurrentCode().getFlowName(), flowNode);
        }
    }

    @Override
    public Integer getProductCode() {
        return ProductEnum.GULIMALL.getCode();
    }

    @Override
    public String getBizType() {
        return BizTypeEnum.BIZ_CREDIT.getCode();
    }
}
