package com.atguigu.gulimall.seckill.stateMachine.manager.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.seckill.stateMachine.enums.IFlowNodeStatus;
import com.atguigu.gulimall.seckill.stateMachine.manager.IFlowNodeManager;
import com.atguigu.gulimall.seckill.stateMachine.node.FlowNode;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class IFlowNodeManagerAdapter implements IFlowNodeManager, InitializingBean {

    /**
     * 可售产品节点链表
     */
    protected LinkedList<FlowNode> flowNodes = new LinkedList<>();

    /**
     * 对应的流程节点集合
     */
    protected Map<String, FlowNode> flowNodeMap = new HashMap<>();

    /**
     * 根据当前状态获取当前节点信息
     * @param currentStatusCode
     * @return
     */
    @Override
    public FlowNode getCurrentFlowNode(String currentStatusCode) {
        return flowNodeMap.get(currentStatusCode);
    }

    @Override
    public FlowNode getNextFlowNode(IFlowNodeStatus initState) {
        if (initState == null){
            return null;
        }
        List<FlowNode> flowNodes = this.flowNodes.stream().filter(flow -> initState.equals(flow.getInitStatusCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(flowNodes)){
            log.info("IFlowNodeManagerImpl|getNextFlowNode|根据初始状态获取节点失败,无匹配的节点!初始状态:{}", JSON.toJSONString(initState));
            return null;
        }
        if (flowNodes.size() > 1){
            log.info("IFlowNodeManagerImpl|getNextFlowNode|根据初始状态获取节点失败,匹配到多个节点!初始状态:{}", JSON.toJSONString(initState));
            return null;
        }
        return flowNodes.get(0);
    }

    /**
     * 根据当前状态获取当前节点信息
     * @param currentStatusCode
     * @return
     */
    @Override
    public FlowNode getCurrentFlowNodeLinked(String currentStatusCode) {
        if (StringUtils.isBlank(currentStatusCode) || CollectionUtils.isEmpty(flowNodes)){
            return null;
        }
        for (FlowNode flowNode : flowNodes) {
            //节点初始状态
            if (currentStatusCode.equals(flowNode.getInitStatusCode().getFlowStatusCode())){
                return flowNode;
            }
            //节点失败状态
            if (flowNode.getFailedStatusCode() != null && currentStatusCode.equals(flowNode.getFailedStatusCode().getFlowStatusCode())){
                return flowNode;
            }
        }
        return null;
    }
}
