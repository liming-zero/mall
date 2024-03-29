package com.atguigu.gulimall.ware.service;

import com.atguigu.common.to.SkuHasStockTo;
import com.atguigu.common.to.mq.OrderTo;
import com.atguigu.common.to.mq.StockLockedTo;
import com.atguigu.gulimall.ware.vo.FareVo;
import com.atguigu.gulimall.ware.vo.OrderVo;
import com.atguigu.gulimall.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author liming-zero
 * @email limingzero@outlook.com
 * @date 2021-05-03 16:19:28
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockTo> getSkusHasStock(List<Long> skuIds);

    FareVo getFare(Long id);

    Boolean orderLockStock(WareSkuLockVo vo);

    void unLockStock(StockLockedTo lockedTo);

    void unLockStock(OrderTo orderVo);
}

