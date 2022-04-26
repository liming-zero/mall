package com.atguigu.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.seckill.feign.CouponFeignService;
import com.atguigu.gulimall.seckill.feign.ProductFeignService;
import com.atguigu.gulimall.seckill.service.SeckillService;
import com.atguigu.gulimall.seckill.to.SecKillSkuRedisTo;
import com.atguigu.gulimall.seckill.vo.SeckillSessionWithSkusVo;
import com.atguigu.gulimall.seckill.vo.SeckillSkuRelationVo;
import com.atguigu.gulimall.seckill.vo.SkuInfoVo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private RedissonClient redissonClient;

    private final String SESSION_CACHE_PREFIX = "seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";    //+商品随机码

    @Override
    public void uploadSeckillSkuLatest3Days() {
        //1、扫描最近三天所有需要参与秒杀的活动
        R r = couponFeignService.getLatest3DaySession();
        if (r.getCode() == 0){
            //上架商品
            List<SeckillSessionWithSkusVo> data = r.getData(new TypeReference<List<SeckillSessionWithSkusVo>>() {
            });
            //缓存到Redis
            //1、缓存活动信息
            saveSessionInfos(data);
            //2、缓存活动的关联商品信息
            saveSessionSkuInfos(data);
        }
    }

    private void saveSessionInfos(List<SeckillSessionWithSkusVo> vos){
        vos.stream().forEach(session ->{
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            String key = startTime + "_" + endTime;
            List<String> collect = session.getRelationEntities().stream().map(item -> item.getSkuId().toString() + "_" + item.getPromotionSessionId().toString())
                    .collect(Collectors.toList());
            //缓存活动信息 seckill:skus   key:start_endtime   val:[sessionId_skuId]
            Boolean hasKey = redisTemplate.hasKey(SESSION_CACHE_PREFIX + key);
            if (CollectionUtils.isNotEmpty(collect) && !hasKey){
                redisTemplate.opsForList().leftPushAll(SESSION_CACHE_PREFIX + key, collect);
            }
        });
    }

    private void saveSessionSkuInfos(List<SeckillSessionWithSkusVo> vos){
        for (SeckillSessionWithSkusVo session : vos) {
            //准备hash操作
            BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            session.getRelationEntities().stream().forEach(seckillSkuVo ->{
                String uuid = IdWorker.get32UUID();
                //缓存商品信息
                SecKillSkuRedisTo skuRedisTo = new SecKillSkuRedisTo();
                //1、缓存Sku的基本数据
                R r = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                if (r.getCode() == 0){
                    SkuInfoVo data = r.getData("skuInfo", new TypeReference<SkuInfoVo>(){});
                    skuRedisTo.setSkuInfo(data);
                }

                //2、Sku的秒杀信息
                BeanUtils.copyProperties(seckillSkuVo, skuRedisTo);

                //key是否包含相同的秒杀场次和相同的商品id
                String key = seckillSkuVo.getSkuId().toString() + "_" + seckillSkuVo.getPromotionSessionId().toString();
                boolean hasKey = hashOps.hasKey(key);
                if (!hasKey){
                    //3、设置当前商品的秒杀时间信息
                    skuRedisTo.setStartTime(session.getStartTime().getTime());
                    skuRedisTo.setEndTime(session.getEndTime().getTime());

                    //4、商品的随机码? 发请求seckill?skuId=1。商品秒杀时间一到不断发请求会不断获取商品，按照随机码来减信号量。
                    skuRedisTo.setRandomCode(uuid);
                    hashOps.put(key, JSON.toJSONString(skuRedisTo));

                    //如果当前这个场次的商品的库存信息已经上架就不需要上架了

                    //5、设置秒杀商品分布式信号量，作为库存扣减信息。可以理解为商品的自增量。
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + uuid);
                    //商品可以秒杀的数量作为信号量，使用库存作为分布式的信号量，限流。
                    semaphore.trySetPermits(skuRedisTo.getSeckillCount().intValue());
                }

            });
        }
    }
}
