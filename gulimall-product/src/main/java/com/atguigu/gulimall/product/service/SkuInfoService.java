package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.frontvo.SkuItemVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author liming-zero
 * @email limingzero@outlook.com
 * @date 2021-05-03 01:28:57
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkus(Long spuId);

    SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException;
}

