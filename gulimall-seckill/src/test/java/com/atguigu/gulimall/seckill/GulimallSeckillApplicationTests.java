package com.atguigu.gulimall.seckill;

import com.atguigu.gulimall.seckill.stateMachine.business.enums.BizTypeEnum;
import com.atguigu.gulimall.seckill.stateMachine.business.enums.CreditFlowEnum;
import com.atguigu.gulimall.seckill.stateMachine.executor.FlowNodeExecutor;
import com.atguigu.gulimall.seckill.stateMachine.factory.FlowNodeExecutorFactory;
import com.atguigu.gulimall.seckill.stateMachine.factory.FlowNodeManagerFactory;
import com.atguigu.gulimall.seckill.stateMachine.manager.IFlowNodeManager;
import com.atguigu.gulimall.seckill.stateMachine.node.FlowNode;
import com.atguigu.gulimall.seckill.stateMachine.vo.ContextBO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class GulimallSeckillApplicationTests {

    @Autowired
    private FlowNodeManagerFactory flowNodeManagerFactory;
    @Autowired
    private FlowNodeExecutorFactory flowNodeExecutorFactory;

    @Test
    public void testFlowNode(){
        Map<String, Object> map = new HashMap<>();
        map.put("bizId", "111111");
        ContextBO contextBO = new ContextBO();
        contextBO.setProductCode(10000);
        contextBO.setBizId("111111");
        contextBO.setBizParam(map);
        BizTypeEnum credit = BizTypeEnum.BIZ_CREDIT;
        IFlowNodeManager manager = flowNodeManagerFactory.getFlowNodeManagerByProduct(contextBO.getProductCode(), credit);
        FlowNode flowNode = manager.getCurrentFlowNode(CreditFlowEnum.CREDIT_APPLY_VALIDATE.getFlowName());
        FlowNodeExecutor executor = flowNodeExecutorFactory.getServiceByEnum(flowNode.getCurrentCode());
        executor.execute(flowNode, contextBO);
    }

    @Test
    void contextLoads() {
    }

}
