package com.atguigu.gulimall.seckill.stateMachine.executor;

import com.atguigu.gulimall.seckill.stateMachine.enums.BizStatusEnum;
import com.atguigu.gulimall.seckill.stateMachine.enums.IFlowNodeEnum;
import com.atguigu.gulimall.seckill.stateMachine.enums.IFlowNodeStatus;
import com.atguigu.gulimall.seckill.stateMachine.manager.IFlowNodeManager;
import com.atguigu.gulimall.seckill.stateMachine.node.FlowNode;
import com.atguigu.gulimall.seckill.stateMachine.vo.ContextBO;

public interface FlowNodeExecutor {

    /**
     * 前置处理
     */
    void before(FlowNode flowNode, ContextBO contextBO);

    /**
     * 执行节点方法
     */
    void execute(FlowNode flowNode, ContextBO contextBO);

    /**
     * 执行任务
     */
    BizStatusEnum process(ContextBO contextBO);

    /**
     * 后置处理
     */
    void after(BizStatusEnum bizStatusEnum, FlowNode flowNode, ContextBO contextBO);

    /**
     * 获取动作枚举
     */
    IFlowNodeEnum getAction();

    /**
     * 更新业务表状态
     */
    void updateBizStatus(ContextBO contextBO, IFlowNodeStatus flowNodeStatus);

    /**
     * 根据业务id获取当前对应的数据状态信息
     */
    String queryBizInfoByFlowNode(ContextBO contextBO);

    /**
     * 获取产品流程工厂
     */
    IFlowNodeManager getProdFlowNodeManager(ContextBO contextBO);

    /**
     * 告警
     * @param flowNode 流程节点
     * @param contextBO 业务对象
     * @param errMsg 异常信息
     * @param flowStatusCode 当前流程状态
     */
    void warn(FlowNode flowNode, ContextBO contextBO, String errMsg, String flowStatusCode);

    /**
     * 补偿登记
     */
    void retry(FlowNode flowNode, ContextBO contextBO, String errMsg, String flowStatusCode);
}
