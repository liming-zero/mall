package com.atguigu.gulimall.seckill.stateMachine.retry.service.impl;

import com.atguigu.gulimall.seckill.stateMachine.business.enums.BizTypeEnum;
import com.atguigu.gulimall.seckill.stateMachine.executor.FlowNodeExecutor;
import com.atguigu.gulimall.seckill.stateMachine.factory.FlowNodeExecutorFactory;
import com.atguigu.gulimall.seckill.stateMachine.factory.FlowNodeManagerFactory;
import com.atguigu.gulimall.seckill.stateMachine.manager.IFlowNodeManager;
import com.atguigu.gulimall.seckill.stateMachine.node.FlowNode;
import com.atguigu.gulimall.seckill.stateMachine.retry.FlowNodeRetryService;
import com.atguigu.gulimall.seckill.stateMachine.retry.vo.RetryResult;
import com.atguigu.gulimall.seckill.stateMachine.vo.ContextBO;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FlowNodeRetryServiceImpl implements FlowNodeRetryService {

    @Autowired
    private FlowNodeManagerFactory flowNodeManagerFactory;
    @Autowired
    private FlowNodeExecutorFactory flowNodeExecutorFactory;

    @Override
    public RetryResult execute(ContextBO contextBO) {
        RetryResult retryExecuteRes = new RetryResult();
        ContextBO flowContextBO = new ContextBO();
        flowContextBO.setBizId("");
        flowContextBO.setProductCode(null);
        flowContextBO.setBizParam(null);
        boolean executeState = true;

        // 1、根据产品编号和流程节点编号获取产品流程执行节点
        BizTypeEnum bizSctnTypeEunm = BizTypeEnum.getEnumByCode("");
        IFlowNodeManager flowNodeManager = flowNodeManagerFactory.getFlowNodeManagerByProduct(10000, bizSctnTypeEunm);
        FlowNode flowNode = flowNodeManager.getCurrentFlowNode("");
        FlowNodeExecutor flowNodeExecutor = flowNodeExecutorFactory.getServiceByEnum(flowNode.getCurrentCode());
        // 校验当前业务状态是否满足本次补偿任务执行
        if (checkFlowExecuteIsSuccess(flowContextBO, flowNode, flowNodeExecutor)) {
            retryExecuteRes.setExecuteStatus(true);
            return retryExecuteRes;
        }

        // 2、执行任务补偿
        try {
            flowNodeExecutor.execute(flowNode, flowContextBO);
            Thread.sleep(2000);
        } catch (Exception e) {
            log.info("FlowNodeRetryServiceImpl|execute|断点补偿重试执行失败,失败原因：{}", e.getMessage());
            executeState = false;
            retryExecuteRes.setErrorMsg(e.getMessage());
        }

        // 3、执行失败的情况下判断节点是否还处于补偿节点
        if (!executeState) {
            executeState = checkFlowExecuteIsSuccess(flowContextBO, flowNode, flowNodeExecutor);
        }
        retryExecuteRes.setExecuteStatus(executeState);
        return retryExecuteRes;
    }

    private boolean checkFlowExecuteIsSuccess(ContextBO flowContextBO, FlowNode flowNode, FlowNodeExecutor executor) {
        String nodeStatus = "";
        try {
            nodeStatus = executor.queryBizInfoByFlowNode(flowContextBO);
            log.info("补偿任务执行时查询业务数据参数：bizId:{}，查询到的数据状态：{}", flowContextBO.getBizId(), nodeStatus);
        } catch (Exception e) {
            log.error("FlowNodeRetryExecutServiceImpl|execute|查询当前状态对应的节点信息失败，失败原因：{}", e.getMessage());
        }
        if (StringUtils.isBlank(nodeStatus)) {
            log.warn("补偿任务执行时未查询到业务id对应的业务数据状态，本次补偿状态修改为成功，不再进行后续补偿操作。bizId:{}", flowContextBO.getBizId());
            return true;
        }

        boolean executeState = true;
        //流程重试异常后，业务流程状态与当前节点状态不一致则补偿成功。如果还是当前节点状态，需判断是否需要重试，如需重试则补偿失败再次进行补偿。
        String noteInitState = flowNode.getInitStatusCode() == null ? null : flowNode.getInitStatusCode().getFlowStatusCode();
        String noteFailedState = flowNode.getFailedStatusCode() == null ? null : flowNode.getFailedStatusCode().getFlowStatusCode();
        String noteProcessState = flowNode.getProcessStatusCode() == null ? null : flowNode.getProcessStatusCode().getFlowStatusCode();
        if (nodeStatus.equals(noteInitState)) {
            executeState = false;
        } else if (nodeStatus.equals(noteFailedState) && flowNode.isFailRetryFlag()) {
            executeState = false;
        } else if (nodeStatus.equals(noteProcessState) && flowNode.isProcessRetryFlag()) {
            executeState = false;
        }
        return executeState;
    }

}
