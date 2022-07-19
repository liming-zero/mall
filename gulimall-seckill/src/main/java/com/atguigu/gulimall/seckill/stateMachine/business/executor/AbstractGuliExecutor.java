package com.atguigu.gulimall.seckill.stateMachine.business.executor;

import com.atguigu.gulimall.seckill.stateMachine.business.enums.BizTypeEnum;
import com.atguigu.gulimall.seckill.stateMachine.enums.IFlowNodeStatus;
import com.atguigu.gulimall.seckill.stateMachine.executor.AbstractFlowNodeExecutor;
import com.atguigu.gulimall.seckill.stateMachine.factory.FlowNodeManagerFactory;
import com.atguigu.gulimall.seckill.stateMachine.manager.IFlowNodeManager;
import com.atguigu.gulimall.seckill.stateMachine.vo.ContextBO;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractGuliExecutor extends AbstractFlowNodeExecutor {

    @Autowired
    private FlowNodeManagerFactory flowNodeManagerFactory;

    @Override
    public void before() {
        super.before();
    }

    /**
     * 更新业务表状态
     * @param contextBO
     * @param flowNodeStatus
     */
    @Override
    public void updateBizStatus(ContextBO contextBO, IFlowNodeStatus flowNodeStatus) {
        //更新表状态
        String statusCode = flowNodeStatus.getFlowStatusCode();
        System.out.println(statusCode);
    }

    /**
     * 当前执行失败得节点可根据状态获取当前节点
     * @param contextBO
     * @return
     */
    @Override
    public IFlowNodeManager getProdFlowNodeManager(ContextBO contextBO) {
        return flowNodeManagerFactory.getFlowNodeManagerByProduct(contextBO.getProductCode(), BizTypeEnum.BIZ_CREDIT);
    }

    @Override
    public String queryBizInfoByFlowNode(ContextBO contextBO) {
        String bizId = contextBO.getBizId();
        //TODO 获取当前业务流程状态码
        System.out.println("查询数据库" + bizId);
        return bizId;
    }
}
