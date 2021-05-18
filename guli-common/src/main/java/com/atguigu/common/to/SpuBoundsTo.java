package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpuBoundsTo {

    private Long spuId;
    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    private BigDecimal buyBounds;

}
