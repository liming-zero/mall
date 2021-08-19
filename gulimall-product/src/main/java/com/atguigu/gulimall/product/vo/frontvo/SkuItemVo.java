package com.atguigu.gulimall.product.vo.frontvo;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {
    //1.sku的基本信息获取  表pms_sku_info
    private SkuInfoEntity info;

    //有货无货
    private boolean hasStock = true;

    //2.sku的图片信息    表pms_sku_images
    private List<SkuImagesEntity> images;

    //3.获取spu的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttrVos;

    //4.获取spu的介绍
    private SpuInfoDescEntity desc;

    //5.获取spu的规格参数信息
    private List<SpuItemAttrGroupVo> groupAttrs;


}

