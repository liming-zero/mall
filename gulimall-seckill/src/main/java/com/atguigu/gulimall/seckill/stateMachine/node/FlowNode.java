package com.atguigu.gulimall.seckill.stateMachine.node;

import com.atguigu.gulimall.seckill.stateMachine.enums.BizStatusEnum;
import com.atguigu.gulimall.seckill.stateMachine.enums.IFlowNodeEnum;
import com.atguigu.gulimall.seckill.stateMachine.enums.IFlowNodeStatus;

import java.io.Serializable;

public class FlowNode<K extends IFlowNodeEnum, V extends IFlowNodeStatus> implements Serializable {

    /**
     * 当前事件
     */
    private final K currentCode;

    /**
     * 节点初始状态
     */
    private final V initStatusCode;

    /**
     * 节点执行失败状态
     */
    private final V failedStatusCode;

    /**
     * 节点执行成功状态
     */
    private final V successStatusCode;

    /**
     * 处理中状态
     */
    private final V processStatusCode;

    /**
     * 流程执行器后置处理拿到该节点是否自动执行
     * true 自动执行
     * false 外部接口或者消息接收后主动执行节点流程
     */
    private final boolean autoExecuteFlag;

    /**
     * 失败是否重视标识
     * true 需要重试
     * false 不需要重试
     */
    private final boolean failRetryFlag;

    /**
     * 处理中是否需要重试标识
     * true 需要
     * false 不需要
     */
    private final boolean processRetryFlag;

    /**
     * 根据业务执行结果获取当前状态
     * @param bizStatusEnum
     * @return
     */
    public V getFlowStatusByBizResult(BizStatusEnum bizStatusEnum){
        if (BizStatusEnum.SUCCESS.equals(bizStatusEnum)){
            return successStatusCode;
        }
        if (BizStatusEnum.PROCESS.equals(bizStatusEnum)){
            return processStatusCode;
        }
        if (BizStatusEnum.FAIL.equals(bizStatusEnum)){
            return failedStatusCode;
        }
        return initStatusCode;
    }

    /**
     *
     * @param currentCode 当前事件
     * @param initStatusCode 节点初始状态
     * @param failedStatusCode 节点执行失败状态
     * @param successStatusCode 节点执行成功状态
     * @param processStatusCode 处理中状态
     * @param autoExecuteFlag 流程执行器后置处理拿到该节点是否自动执行
     * @param failRetryFlag 失败是否重试标识
     * @param processRetryFlag 处理中是否需要重试标识
     */
    public FlowNode(K currentCode, V initStatusCode, V failedStatusCode, V successStatusCode, V processStatusCode, boolean autoExecuteFlag, boolean failRetryFlag, boolean processRetryFlag) {
        this.currentCode = currentCode;
        this.initStatusCode = initStatusCode;
        this.failedStatusCode = failedStatusCode;
        this.successStatusCode = successStatusCode;
        this.processStatusCode = processStatusCode;
        this.autoExecuteFlag = autoExecuteFlag;
        this.failRetryFlag = failRetryFlag;
        this.processRetryFlag = processRetryFlag;
    }

    public K getCurrentCode() {
        return currentCode;
    }

    public V getInitStatusCode() {
        return initStatusCode;
    }

    public V getFailedStatusCode() {
        return failedStatusCode;
    }

    public V getSuccessStatusCode() {
        return successStatusCode;
    }

    public V getProcessStatusCode() {
        return processStatusCode;
    }

    public boolean isAutoExecuteFlag() {
        return autoExecuteFlag;
    }

    public boolean isFailRetryFlag() {
        return failRetryFlag;
    }

    public boolean isProcessRetryFlag() {
        return processRetryFlag;
    }
}
