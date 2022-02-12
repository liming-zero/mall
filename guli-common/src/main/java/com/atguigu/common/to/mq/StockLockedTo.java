package com.atguigu.common.to.mq;

import lombok.Data;

/**
 * 库存锁定发送给mq的数据
 */
@Data
public class StockLockedTo {

    //库存工作单的id
    private Long id;
    //工作单详情的id
    private StockDetailTo detailTo;
}
