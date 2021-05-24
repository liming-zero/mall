package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.vo.publishvo.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * spu信息
 *
 * @author liming-zero
 * @email limingzero@outlook.com
 * @date 2021-05-03 01:28:57
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    SpuInfoEntity saveBaseSpuInfo(SpuSaveVo vo);

    void saveSpuInfoDesc(SpuSaveVo vo, Long id);

    void saveSpuImages(SpuSaveVo vo, Long id);

    void saveProdAttrValue(SpuSaveVo vo, Long id);

    void saveSkuInfo(SpuSaveVo vo, SpuInfoEntity id);

    void saveSpuBoundsFeign(SpuSaveVo vo, Long id);

    PageUtils queryPageCondition(Map<String, Object> params);

    void spuUp(Long spuId);
}

