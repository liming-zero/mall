package com.atguigu.gulimall.seckill.stateMachine.manager;

import com.atguigu.gulimall.seckill.stateMachine.enums.IFlowNodeStatus;
import com.atguigu.gulimall.seckill.stateMachine.node.FlowNode;

/**
 * 流程管理器
 */
public interface IFlowNodeManager {

    /**
     * 根据状态匹配初始状态或者失败状态的节点
     * @param currentStatusCode
     * @return
     */
    FlowNode getCurrentFlowNode(String currentStatusCode);

    /**
     * 根据初始状态获取下一个节点
     * @param initState
     * @return
     */
    FlowNode getNextFlowNode(IFlowNodeStatus initState);

    /**
     * 获取流程编排所属产品
     * @return
     */
    Integer getProductCode();

    /**
     * 获取业务类型
     * @return
     */
    String getBizType();

    FlowNode getCurrentFlowNodeLinked(String currentStatusCode);

}
