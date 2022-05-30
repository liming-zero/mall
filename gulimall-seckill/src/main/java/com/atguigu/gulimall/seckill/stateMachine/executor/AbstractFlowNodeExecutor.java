package com.atguigu.gulimall.seckill.stateMachine.executor;

import com.atguigu.gulimall.seckill.stateMachine.business.exception.BizException;
import com.atguigu.gulimall.seckill.stateMachine.enums.BizStatusEnum;
import com.atguigu.gulimall.seckill.stateMachine.node.FlowNode;
import com.atguigu.gulimall.seckill.stateMachine.vo.ContextBO;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
public abstract class AbstractFlowNodeExecutor implements FlowNodeExecutor {

    /**
     * 进入方法前验证
     * @param flowNode
     * @param contextBO
     */
    @Override
    public void before(FlowNode flowNode, ContextBO contextBO) {
        log.info("记录流程节点开始状态");
        Assert.notNull(contextBO.getBizId(), "");
        Assert.notNull(contextBO.getProductCode(), "");

        String status = queryBizInfoByFlowNode(contextBO);
        log.info("当前节点状态{}", status);
        if (StringUtils.isNotBlank(status)){
            //当前节点为成功状态不执行
            if (flowNode.getSuccessStatusCode().equals(status)){
                throw new IllegalArgumentException();
            }
            //当前节点处理中状态且节点不允许重试不执行
            if (flowNode.getProcessStatusCode().equals(status) && !flowNode.isProcessRetryFlag()){
                throw new IllegalArgumentException();
            }
            //当前节点失败状态且节点不允许重试不执行
            if (flowNode.getFailedStatusCode().equals(status) && !flowNode.isFailRetryFlag()){
                throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public void execute(FlowNode flowNode, ContextBO contextBO) {
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

    @Override
    public void retry(FlowNode flowNode, ContextBO contextBO, String errMsg, String flowStatusCode) {

    }

    @Override
    public void warn(FlowNode flowNode, ContextBO contextBO, String errMsg, String flowStatusCode) {

    }

    @Override
    public void after(BizStatusEnum bizStatusEnum, FlowNode flowNode, ContextBO contextBO) {

    }
}
