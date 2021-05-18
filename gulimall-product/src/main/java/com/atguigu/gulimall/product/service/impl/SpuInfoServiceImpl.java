package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundsTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.publishvo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService descService;
    @Autowired
    private SpuImagesService spuimagesService;
    @Autowired
    private ProductAttrValueService attrValueService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );
        return new PageUtils(page);
    }

    //TODO 高级部分完善服务出现异常等其他问题
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1.保存spu基本信息：pms_spu_info
        SpuInfoEntity spuInfoEntity = this.saveBaseSpuInfo(vo);

        //2.保存spu的描述信息：pms_spu_info_desc
        this.saveSpuInfoDesc(vo, spuInfoEntity.getId());

        //3.保存spu的图片集：pms_spu_images
        this.saveSpuImages(vo, spuInfoEntity.getId());

        //4.保存spu的规格参数；表pms_product_attr_value
        this.saveProdAttrValue(vo, spuInfoEntity.getId());

        //5.保存spu的积分信息：mall_sms -> sms_spu_bounds
        this.saveSpuBoundsFeign(vo, spuInfoEntity.getId());

        //6.保存当前spu对应的所有sku信息：
        this.saveSkuInfo(vo, spuInfoEntity);

    }

    //1.保存spu基本信息：pms_spu_info
    @Override
    public SpuInfoEntity saveBaseSpuInfo(SpuSaveVo vo) {
        if (vo != null) {
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            BeanUtils.copyProperties(vo, spuInfoEntity);
            baseMapper.insert(spuInfoEntity);
            return spuInfoEntity;
        }
        return null;
    }

    //2.保存spu的描述信息：pms_spu_info_desc
    @Override
    public void saveSpuInfoDesc(SpuSaveVo vo, Long id) {
        List<String> decript = vo.getDecript();
        if (decript != null && decript.size() > 0) {
            SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
            descEntity.setSpuId(id);
            descEntity.setDecript(String.join(",", decript));
            descService.save(descEntity);
        }
    }

    //3.保存spu的图片集：pms_spu_images
    @Override
    public void saveSpuImages(SpuSaveVo vo, Long id) {
        List<String> images = vo.getImages();
        if (images != null && images.size() > 0) {
            List<SpuImagesEntity> collect = images.stream().map((img) -> {
                SpuImagesEntity imagesEntity = new SpuImagesEntity();
                imagesEntity.setSpuId(id);
                imagesEntity.setImgUrl(img);
                return imagesEntity;
            }).collect(Collectors.toList());
            spuimagesService.saveBatch(collect);
        }
    }

    //4.保存spu的规格参数；表pms_product_attr_value
    @Override
    public void saveProdAttrValue(SpuSaveVo vo, Long id) {
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        if (baseAttrs != null && baseAttrs.size() > 0) {
            List<ProductAttrValueEntity> collect = baseAttrs.stream().map((attr) -> {
                ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
                attrValueEntity.setSpuId(id);
                attrValueEntity.setAttrId(attr.getAttrId());
                AttrEntity byId = attrService.getById(attr.getAttrId());
                attrValueEntity.setAttrName(byId.getAttrName());
                attrValueEntity.setAttrValue(attr.getAttrValues());
                attrValueEntity.setQuickShow(attr.getShowDesc());
                return attrValueEntity;
            }).collect(Collectors.toList());
            attrValueService.saveBatch(collect);
        }
    }

    //6.保存当前spu对应的所有sku信息：
    @Override
    public void saveSkuInfo(SpuSaveVo vo, SpuInfoEntity spuInfoEntity) {
        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            //skus.forEach((item) -> {
            for (Skus item : skus) {
                //6.1)保存sku的基本信息：pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());

                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        skuInfoEntity.setSkuDefaultImg(image.getImgUrl());
                    }
                }
                skuInfoService.save(skuInfoEntity);

                //6.2)保存sku的图片信息：pms_sku_images
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> skuImagesEntities = item.getImages().stream().map((img) -> {
                    SkuImagesEntity imagesEntity = new SkuImagesEntity();
                    imagesEntity.setSkuId(skuId);
                    imagesEntity.setDefaultImg(img.getDefaultImg());
                    imagesEntity.setImgUrl(img.getImgUrl());
                    return imagesEntity;
                }).filter(entity -> {
                    //返回true需要，返回false过滤掉(剔除)
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //TODO 没有图片路径的无需保存
                skuImagesService.saveBatch(skuImagesEntities);

                //6.3)保存sku的销售属性信息：pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> saleAttrValueEntities = attr.stream().map((saleAttr) -> {
                    SkuSaleAttrValueEntity saleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(saleAttr, saleAttrValueEntity);
                    saleAttrValueEntity.setSkuId(skuId);
                    return saleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(saleAttrValueEntities);

                //6.4)sku的优惠、满减等信息：mall_sms -> sms_sku_ladder(sku的打折表)、sms_sku_full_reduction(sku满减表)、
                //    sms_member_price(不同级别会员对应的商品价格表)、
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R r = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r.getCode() != 0) {
                        log.error("远程服务保存sku优惠信息失败");
                    }
                }
                //});
            }
        }
    }

    //5.保存spu的积分信息：mall_sms -> sms_spu_bounds
    @Override
    public void saveSpuBoundsFeign(SpuSaveVo vo, Long id) {
        Bounds bounds = vo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(id);
        R r = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (r.getCode() != 0) {
            log.error("远程服务保存spu积分信息失败");
        }
    }

    @Override
    public PageUtils queryPageCondition(Map<String, Object> params) {
        String status = (String) params.get("status");
        String key = (String) params.get("key");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(key)){
            wrapper.and((wap)->{
                wap.eq("id",key).or().like("spu_name",key);
            });
        }
        if (!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }


}