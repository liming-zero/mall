package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuHasStockTo;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundsTo;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.publishvo.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
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
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );
        return new PageUtils(page);
    }

    /**
     * TODO ???????????????????????????????????????????????????
     * @GlobalTransactional ?????????????????????????????????????????????????????????????????????seata???AT???????????????
     */

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1.??????spu???????????????pms_spu_info
        SpuInfoEntity spuInfoEntity = this.saveBaseSpuInfo(vo);

        //2.??????spu??????????????????pms_spu_info_desc
        this.saveSpuInfoDesc(vo, spuInfoEntity.getId());

        //3.??????spu???????????????pms_spu_images
        this.saveSpuImages(vo, spuInfoEntity.getId());

        //4.??????spu?????????????????????pms_product_attr_value
        this.saveProdAttrValue(vo, spuInfoEntity.getId());

        //5.??????spu??????????????????mall_sms -> sms_spu_bounds
        this.saveSpuBoundsFeign(vo, spuInfoEntity.getId());

        //6.????????????spu???????????????sku?????????
        this.saveSkuInfo(vo, spuInfoEntity);

    }

    //1.??????spu???????????????pms_spu_info
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

    //2.??????spu??????????????????pms_spu_info_desc
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

    //3.??????spu???????????????pms_spu_images
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

    //4.??????spu?????????????????????pms_product_attr_value
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

    //6.????????????spu???????????????sku?????????
    @Override
    public void saveSkuInfo(SpuSaveVo vo, SpuInfoEntity spuInfoEntity) {
        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            //skus.forEach((item) -> {
            for (Skus item : skus) {
                //6.1)??????sku??????????????????pms_sku_info
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

                //6.2)??????sku??????????????????pms_sku_images
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> skuImagesEntities = item.getImages().stream().map((img) -> {
                    SkuImagesEntity imagesEntity = new SkuImagesEntity();
                    imagesEntity.setSkuId(skuId);
                    imagesEntity.setDefaultImg(img.getDefaultImg());
                    imagesEntity.setImgUrl(img.getImgUrl());
                    return imagesEntity;
                }).filter(entity -> {
                    //??????true???????????????false?????????(??????)
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //TODO ?????????????????????????????????
                skuImagesService.saveBatch(skuImagesEntities);

                //6.3)??????sku????????????????????????pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> saleAttrValueEntities = attr.stream().map((saleAttr) -> {
                    SkuSaleAttrValueEntity saleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(saleAttr, saleAttrValueEntity);
                    saleAttrValueEntity.setSkuId(skuId);
                    return saleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(saleAttrValueEntities);

                //6.4)sku??????????????????????????????mall_sms -> sms_sku_ladder(sku????????????)???sms_sku_full_reduction(sku?????????)???
                //    sms_member_price(??????????????????????????????????????????)???
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R r = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r.getCode() != 0) {
                        log.error("??????????????????sku??????????????????");
                    }
                }
                //});
            }
        }
    }

    //5.??????spu??????????????????mall_sms -> sms_spu_bounds
    @Override
    public void saveSpuBoundsFeign(SpuSaveVo vo, Long id) {
        Bounds bounds = vo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(id);
        R r = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (r.getCode() != 0) {
            log.error("??????????????????spu??????????????????");
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

    /**
     * ????????????
     * @param spuId
     */
    @Transactional
    @Override
    public void spuUp(Long spuId) {
        //?????????????????????
        //1.????????????spuid???????????????sku????????????????????????
        List<SkuInfoEntity> skuInfoEntityList = skuInfoService.getSkus(spuId);
        List<Long> skuIds = skuInfoEntityList.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        //TODO 4.????????????sku?????????????????????????????????????????????
        List<ProductAttrValueEntity> baseAttrs = attrValueService.baseAttrListBySpu(spuId);
        List<Long> attrIds = baseAttrs.stream().map((baseAttr) -> {
            return baseAttr.getAttrId();
        }).collect(Collectors.toList());

        //??????attrId???????????????????????????search_type=1
        List<Long> searchAttrIds = attrService.selectSerachAttrs(attrIds);
        Set<Long> idSet = new HashSet<>(searchAttrIds);
        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs);
            return attrs;
        }).collect(Collectors.toList());

        //TODO 1.??????????????????????????????????????????????????????
        Map<Long, Boolean> stockMap = null;
        try{
            R r = wareFeignService.getSkusHasStock(skuIds);
            TypeReference<List<SkuHasStockTo>> typeReference = new TypeReference<List<SkuHasStockTo>>() {};
            List<SkuHasStockTo> data = r.getData(typeReference);
            stockMap =  data.stream().collect
                    (Collectors.toMap(SkuHasStockTo::getSkuId, item -> item.getHasStock()));
        }catch (Exception e){
            log.error("????????????????????????????????????:??????{}",e);
        }

        //2.????????????sku?????????
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> skuEsModelList = skuInfoEntityList.stream().map((skuInfo) -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(skuInfo,skuEsModel);

            //???????????????????????????????????????skuPrice,skuImg,hasStock,hotScore,brandName,brandImg,catalogName,attrs
            skuEsModel.setSkuPrice(skuInfo.getPrice());
            skuEsModel.setSkuImg(skuInfo.getSkuDefaultImg());
            //??????????????????
            if (finalStockMap == null){
                skuEsModel.setHasStock(true);
            }else{
                skuEsModel.setHasStock(finalStockMap.get(skuInfo.getSkuId()));  //map????????????key??????value
            }

            //TODO 2.???????????????0
            skuEsModel.setHotScore(0L);

            //TODO 3.????????????????????????????????????
            BrandEntity brandEntity = brandService.getById(skuInfo.getBrandId());
            skuEsModel.setBrandName(brandEntity.getName());
            skuEsModel.setBrandImg(brandEntity.getLogo());
            CategoryEntity categoryEntity = categoryService.getById(skuInfo.getCatalogId());
            skuEsModel.setCatalogName(categoryEntity.getName());
            //??????????????????
            skuEsModel.setAttrs(attrsList);
            return skuEsModel;
        }).collect(Collectors.toList());

        //TODO 5.??????????????????es????????????
        System.out.println(JSON.toJSONString(skuEsModelList));
        R r = searchFeignService.productStatusUp(skuEsModelList);
        if(r.getCode() == 0){
            //??????????????????
            //TODO 6.????????????spu?????????
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else{
            log.error("?????????????????????????????????");
            //??????????????????
            //TODO 7.?????????????????????????????????????????????
            /**
             * Feign????????????
             * 1.????????????????????????????????????Json
             *   RequestTemplate template = buildTemplateFormArgs.create(argv);
             * 2.????????????????????????(?????????????????????????????????)
             *   executeAndDecode(template)
             * 3.??????????????????????????????
             *   while(true){
             *   try{
             *       executeAndDecode(template);
             *   }catch(){
             *      try{
             *          retryer.continueOrPropagate(e);
             *      }catch(){
             *          throw ex;
             *          }
             *      continue;
             *      }
             *   }
             */
        }
    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        SpuInfoEntity spuInfo = getById(skuInfo.getSpuId());
        return spuInfo;
    }


}