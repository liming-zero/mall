package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.to.SkuHasStockTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.feign.ProductFeignService;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private PurchaseDetailService detailService;
    @Autowired
    private ProductFeignService feignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String wareId = (String) params.get("wareId");
        String skuId = (String) params.get("skuId");
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }
        if (!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1.判断如果还没有这个库存j记录新增
        List<WareSkuEntity> wareSkuEntities = baseMapper.selectList
                (new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (StringUtils.isEmpty(wareSkuEntities) || wareSkuEntities.size() == 0){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询sku的name
            //TODO 还可以用什么办法让异常出现以后不回滚? 高级
            try{
                R r = feignService.getSkuNameById(skuId);
                if (r.getCode() == 0){
                    String name = (String) r.get("name");
                    wareSkuEntity.setSkuName(name);
                }
            }catch (Exception e){}
            baseMapper.insert(wareSkuEntity);
        }else{
            baseMapper.addStock(skuId,wareId,skuNum);
        }
    }

    /**
     * 查询sku的库存 总库存-锁定的库存
     * select sum(stock-stock_locked) from wms_ware_sku where sku_id=?
     * @param skuIds
     * @return
     */
    @Override
    public List<SkuHasStockTo> getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStockTo> collect = skuIds.stream().map((skuId) -> {
            SkuHasStockTo skuHasStockTo = new SkuHasStockTo();
            Long count = baseMapper.getSkuStock(skuId);
            skuHasStockTo.setSkuId(skuId);
            skuHasStockTo.setHasStock(count==null?false:count>0);
            return skuHasStockTo;
        }).collect(Collectors.toList());
        return collect;
    }

}