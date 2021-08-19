package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.SkuSaleAttrValueDao;
import com.atguigu.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.atguigu.gulimall.product.service.SkuSaleAttrValueService;
import com.atguigu.gulimall.product.vo.frontvo.SkuItemSaleAttrVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId) {
        /**
         * 传入了spuId
         * 1.分析当前spu有多少个sku，所有sku涉及到的属性组合
         * select
         *             ssav.attr_id,
         *             ssav.attr_name,
         *             GROUP_CONCAT(DISTINCT ssav.attr_value) attr_values
         *         from pms_sku_info info
         *                  left join pms_sku_sale_attr_value ssav
         *                            on ssav.sku_id=info.sku_id
         *         where spu_id=#{spuId}
         *         group by ssav.attr_id,ssav.attr_name
         */
        List<SkuItemSaleAttrVo> skuItemSaleAttrVos = baseMapper.getSaleAttrsBySpuId(spuId);
        return skuItemSaleAttrVos;
    }

}