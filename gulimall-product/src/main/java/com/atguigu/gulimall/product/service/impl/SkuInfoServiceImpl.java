package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.exception.RRException;
import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.frontvo.SkuItemSaleAttrVo;
import com.atguigu.gulimall.product.vo.frontvo.SkuItemVo;
import com.atguigu.gulimall.product.vo.frontvo.SpuItemAttrGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SkuInfoDao;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService imagesService;
    @Autowired
    private SpuInfoDescService descService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private SkuSaleAttrValueService saleAttrValueService;
    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        String key = (String) params.get("key");
        String catalogId = (String) params.get("catalogId");
        String brandId = (String) params.get("brandId");
        String min = (String) params.get("min");
        String max = (String) params.get("max");

        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("sku_id",key).or().like("sku_name",key);
            });
        }
        if (!StringUtils.isEmpty(catalogId) && !"0".equalsIgnoreCase(catalogId)){
            wrapper.eq("catalog_id",catalogId);
        }
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        if (!StringUtils.isEmpty(min)){
            wrapper.ge("price",min);
        }
        if (!StringUtils.isEmpty(max)){
            BigDecimal bigDecimal = new BigDecimal(max);
            try{
                if (bigDecimal.compareTo(new BigDecimal("0")) == 1){
                    wrapper.le("price",max);
                }
            }catch (Exception e){
                throw new RRException("参数错误");
            }
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkus(Long spuId) {
        List<SkuInfoEntity> skuInfoEntities = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return skuInfoEntities;
    }

    /**
     * 异步编排
     * @param skuId
     * @return
     */
    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //1.sku的基本信息获取  表pms_sku_info
            SkuInfoEntity skuInfo = this.getById(skuId);
            skuItemVo.setInfo(skuInfo);
            return skuInfo;
        }, executor);

        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((result) -> {
            //3.获取spu的销售属性组合
            List<SkuItemSaleAttrVo> saleAttrVos = saleAttrValueService.getSaleAttrsBySpuId(result.getSpuId());
            skuItemVo.setSaleAttrVos(saleAttrVos);
        }, executor);

        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((result) -> {
            //4.获取spu的介绍
            SpuInfoDescEntity infoDesc = descService.getById(result.getSpuId());
            skuItemVo.setDesc(infoDesc);
        }, executor);

        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((result) -> {
            //5.获取spu的规格参数信息
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(result.getSpuId(), result.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
        }, executor);

        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            //2.sku的图片信息    表pms_sku_images
            List<SkuImagesEntity> images = imagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        }, executor);

        //等待所有任务都完成
        CompletableFuture.allOf(saleAttrFuture,descFuture,baseAttrFuture,imageFuture).get();

        return skuItemVo;
    }

}