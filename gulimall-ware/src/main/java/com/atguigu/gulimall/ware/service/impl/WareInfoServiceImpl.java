package com.atguigu.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.exception.NoStockException;
import com.atguigu.gulimall.ware.feign.MemberFeignService;
import com.atguigu.gulimall.ware.vo.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareInfoDao;
import com.atguigu.gulimall.ware.entity.WareInfoEntity;
import com.atguigu.gulimall.ware.service.WareInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        String key = (String) params.get("key");
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            wrapper.eq("id", key)
                    .or().like("name", key)
                    .or().like("address", key)
                    .or().like("areacode", key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 根据用户的收获地址计算运费
     *
     * @param id
     * @return
     */
    @Override
    public FareVo getFare(Long id) {
        FareVo fareVo = new FareVo();
        R r = memberFeignService.info(id);
        MemberAddressVo data = r.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
        });
        if (data != null) {
            String phone = data.getPhone();
            //使用手机号最后一位数字作为运费
            String substring = phone.substring(phone.length() - 1, phone.length());
            fareVo.setFare(new BigDecimal(substring));
            fareVo.setAddressVo(data);
        }
        return fareVo;
    }

    /**
     * 为某个订单锁定库存
     *
     * @param vo
     * @return
     */
    //指示哪些异常类型必须导致事务回滚。
    @Transactional(rollbackFor = {NoStockException.class})
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        //按照下单的收货地址，找到一个就近仓库，锁定库存
        //1、找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(lock -> {
            Long skuId = lock.getSkuId();
            SkuWareHasStock stock = new SkuWareHasStock();
            stock.setSkuId(skuId);
            //查询当前商品在哪个仓库有库存
            List<Long> wareId = baseMapper.listWareIdHasStock(skuId);
            stock.setWareId(wareId);
            stock.setNum(lock.getCount());  //锁定库存的件数
            return stock;
        }).collect(Collectors.toList());

        //2、锁定库存
        for (SkuWareHasStock stock : collect) {
            Boolean skuStocked = false;     //当前商品没有被锁住
            Long skuId = stock.getSkuId();
            Integer num = stock.getNum();
            List<Long> wareIds = stock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                //没有任何仓库有当前商品的库存,抛异常回滚数据
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                //成功就返回1，否则就是0
                Long count = baseMapper.lockSkuStock(skuId, wareId, num);
                if (count == 1) {
                    skuStocked = true;
                    break;
                }
            }
            if (skuStocked == false){
                //当前商品所有仓库都没有锁住
                throw new NoStockException(skuId);
            }
        }
        //3、所有商品都被成功锁定库存
        return true;
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;    //锁定库存件数
        private List<Long> wareId;
    }

}