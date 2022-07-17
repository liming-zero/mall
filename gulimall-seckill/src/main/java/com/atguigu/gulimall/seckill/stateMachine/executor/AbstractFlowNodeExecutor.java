package com.atguigu.gulimall.seckill.stateMachine.executor;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.seckill.stateMachine.business.exception.BizException;
import com.atguigu.gulimall.seckill.stateMachine.enums.BizStatusEnum;
import com.atguigu.gulimall.seckill.stateMachine.enums.IFlowNodeStatus;
import com.atguigu.gulimall.seckill.stateMachine.factory.FlowNodeExecutorFactory;
import com.atguigu.gulimall.seckill.stateMachine.manager.IFlowNodeManager;
import com.atguigu.gulimall.seckill.stateMachine.node.FlowNode;
import com.atguigu.gulimall.seckill.stateMachine.vo.ContextBO;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

@Slf4j
public abstract class AbstractFlowNodeExecutor implements FlowNodeExecutor {

    @Autowired
    private FlowNodeExecutorFactory flowNodeExecutorFactory;

    @Override
    public final void execute(FlowNode flowNode, ContextBO contextBO) {
        log.info("业务编号:{},流程节点:{},开始执行", contextBO.getBizId(), flowNode.getCurrentCode().getFlowName());
        before(flowNode, contextBO);
        BizStatusEnum bizStatusEnum;
        try {
            bizStatusEnum = process(contextBO);
        }
        catch (BizException e){
            log.error("流程节点:{},出现异常:{},开始记录异常", flowNode.getCurrentCode().getFlowName(), e);
            this.retry(flowNode, contextBO, e.getMessage(), flowNode.getInitStatusCode().getFlowStatusCode());
            throw e;
        }
        catch (Exception e){
            log.error("流程节点:{},出现异常:{},开始记录异常", flowNode.getCurrentCode().getFlowName(), e);
            this.warn(flowNode, contextBO, e.getMessage(), flowNode.getInitStatusCode().getFlowStatusCode());
            throw e;
        }
        log.info("业务编号:{},流程节点:{},执行结束，开始后置处理", contextBO.getBizId(), flowNode.getCurrentCode().getFlowName());
        after(bizStatusEnum, flowNode, contextBO);
    }

    /**
     * 前置效验
     * @param flowNode
     * @param contextBO
     */
    @Override
    public final void before(FlowNode flowNode, ContextBO contextBO) {
        log.info("记录流程节点开始状态");
        Assert.notNull(contextBO.getBizId(), "业务编号不能为空");
        Assert.notNull(contextBO.getProductCode(), "产品编号不能为空");

        String status = queryBizInfoByFlowNode(contextBO);
        log.info("当前节点状态{}", status);
        if (StringUtils.isNotBlank(status)){
            //当前节点为成功状态不执行
            if (flowNode.getSuccessStatusCode() != null){
                if (flowNode.getSuccessStatusCode().equals(status)){
                    throw new IllegalArgumentException();
                }
            }

            //当前节点处理中状态且节点不允许重试不执行
            if (flowNode.getProcessStatusCode() != null){
                if (flowNode.getProcessStatusCode().equals(status) && !flowNode.isProcessRetryFlag()){
                    throw new IllegalArgumentException();
                }
            }

            //当前节点失败状态且节点不允许重试不执行
            if (flowNode.getFailedStatusCode() != null){
                if (flowNode.getFailedStatusCode().equals(status) && !flowNode.isFailRetryFlag()){
                    throw new IllegalArgumentException();
                }
            }

            this.before();
        }
    }

    public void before(){}

    @Override
    public void retry(FlowNode flowNode, ContextBO contextBO, String errMsg, String flowStatusCode) {

    }

    @Override
    public void warn(FlowNode flowNode, ContextBO contextBO, String errMsg, String flowStatusCode) {

    }

    @Override
    public final void after(BizStatusEnum bizStatusEnum, FlowNode flowNode, ContextBO contextBO) {
        //1、异常场景，直接返回
        if (bizStatusEnum == null){
            return;
        }

        //2、更新业务表状态
        IFlowNodeStatus nodeStatus = flowNode.getFlowStatusByBizResult(bizStatusEnum);
        updateBizStatus(contextBO, nodeStatus);

        //3、失败重试场景
        if (bizStatusEnum.FAIL.equals(bizStatusEnum) && flowNode.isFailRetryFlag()){
            log.info("业务编号{},流程节点{},失败开始补偿登记", contextBO.getBizId(), flowNode.getCurrentCode().getFlowName());
            this.retry(flowNode, contextBO, bizStatusEnum.getCode(), nodeStatus.getFlowStatusCode());
            return;
        }

        //3、处理中重试场景
        if (bizStatusEnum.PROCESS.equals(bizStatusEnum) && flowNode.isProcessRetryFlag()){
            log.info("业务编号{},流程节点{},处理中开始补偿登记", contextBO.getBizId(), flowNode.getCurrentCode().getFlowName());
            this.retry(flowNode, contextBO, bizStatusEnum.getCode(), nodeStatus.getFlowStatusCode());
            return;
        }

        //4、未成功重试场景
        if (!bizStatusEnum.SUCCESS.equals(bizStatusEnum)){
            log.info("业务编号{},流程节点{},未成功开始补偿登记", contextBO.getBizId(), flowNode.getCurrentCode().getFlowName());
            this.retry(flowNode, contextBO, bizStatusEnum.getCode(), nodeStatus.getFlowStatusCode());
            return;
        }

        //5、获取产品流程工厂
        IFlowNodeManager flowNodeManager = getProdFlowNodeManager(contextBO);
        FlowNode nextFlowNode = flowNodeManager.getNextFlowNode(flowNode.getSuccessStatusCode());
        if (nextFlowNode == null){
            log.warn("业务编号:{},业务流程节点信息:{},无下一个节点,执行结束", contextBO.getBizId(), JSON.toJSONString(flowNode));
            return;
        }

        //6、判断需要执行的节点是否是自驱节点
        if (!nextFlowNode.isAutoExecuteFlag()){
            log.warn("业务编号:{},待执行流程节点信息:{},该节点属于非自驱节点,不自动执行", contextBO.getBizId(), JSON.toJSONString(nextFlowNode));
            return;
        }

        //7、执行下一个节点方法
        FlowNodeExecutor flowNodeExecutor = flowNodeExecutorFactory.getServiceByEnum(nextFlowNode.getCurrentCode());
        flowNodeExecutor.execute(nextFlowNode, contextBO);
    }
}
