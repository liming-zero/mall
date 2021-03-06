package com.atguigu.gulimall.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.CommonRespUtils;
import com.atguigu.common.to.mq.SeckillOrderTo;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.seckill.feign.CouponFeignService;
import com.atguigu.gulimall.seckill.feign.ProductFeignService;
import com.atguigu.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.seckill.sentinel.GuliBlockExceptionHandler;
import com.atguigu.gulimall.seckill.service.SeckillService;
import com.atguigu.gulimall.seckill.to.SecKillSkuRedisTo;
import com.atguigu.gulimall.seckill.vo.SeckillSessionWithSkusVo;
import com.atguigu.gulimall.seckill.vo.SkuInfoVo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired(required = false)
    private ProductFeignService productFeignService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String SESSION_CACHE_PREFIX = "seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";    //+???????????????
    public static final String ORDER_EXCHANGE = "order-event-exchange";
    public static final String SECKILL_ROUTING_KEY = "order.seckill.order";

    @Override
    public void uploadSeckillSkuLatest3Days() {
        //1??????????????????????????????????????????????????????
        CommonRespUtils<List<SeckillSessionWithSkusVo>> r = couponFeignService.getLatest3DaySession();
        if (r.getCode() == 0) {
            //????????????
            List<SeckillSessionWithSkusVo> data = r.getData();
            //?????????Redis
            //1?????????????????????
            saveSessionInfos(data);
            //2????????????????????????????????????
            saveSessionSkuInfos(data);
        }
    }

    private void saveSessionInfos(List<SeckillSessionWithSkusVo> vos) {
        vos.stream().forEach(session -> {
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            String key = startTime + "_" + endTime;
            List<String> collect = session.getRelationEntities().stream().map(item -> item.getPromotionSessionId().toString() + "_" + item.getSkuId().toString())
                    .collect(Collectors.toList());
            //?????????????????? seckill:skus   key:start_endtime   val:[sessionId_skuId]
            Boolean hasKey = redisTemplate.hasKey(SESSION_CACHE_PREFIX + key);
            if (CollectionUtils.isNotEmpty(collect) && !hasKey) {
                redisTemplate.opsForList().leftPushAll(SESSION_CACHE_PREFIX + key, collect);
            }
        });
    }

    private void saveSessionSkuInfos(List<SeckillSessionWithSkusVo> vos) {
        for (SeckillSessionWithSkusVo session : vos) {
            //??????hash??????
            BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            session.getRelationEntities().stream().forEach(seckillSkuVo -> {
                String uuid = IdWorker.get32UUID();
                //??????????????????
                SecKillSkuRedisTo skuRedisTo = new SecKillSkuRedisTo();
                //1?????????Sku???????????????
                R r = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                if (r.getCode() == 0) {
                    SkuInfoVo data = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    skuRedisTo.setSkuInfo(data);
                }

                //2???Sku???????????????
                BeanUtils.copyProperties(seckillSkuVo, skuRedisTo);

                //key???????????????????????????????????????????????????id
                String key = seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString();
                boolean hasKey = hashOps.hasKey(key);
                if (!hasKey) {
                    //3??????????????????????????????????????????
                    skuRedisTo.setStartTime(session.getStartTime().getTime());
                    skuRedisTo.setEndTime(session.getEndTime().getTime());

                    //4?????????????????????? ?????????seckill?skuId=1???????????????????????????????????????????????????????????????????????????????????????????????????
                    skuRedisTo.setRandomCode(uuid);
                    hashOps.put(key, JSON.toJSONString(skuRedisTo));

                    //?????????????????????????????????????????????????????????????????????????????????

                    //5?????????????????????????????????????????????????????????????????????????????????????????????????????????
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + uuid);
                    //????????????????????????????????????????????????????????????????????????????????????????????????
                    semaphore.trySetPermits(skuRedisTo.getSeckillCount().intValue());
                }

            });
        }
    }

    public List<SecKillSkuRedisTo> blockHandler(){
        log.error("getCurrentSeckillSkus????????????......");
        return null;
    }
    /**
     * ??????????????????????????????
     * blockHandler?????????????????????
     * @return
     */
    @SentinelResource(value = "getCurrentSeckillSkus", blockHandler = "blockHandler", fallbackClass = GuliBlockExceptionHandler.class)
    @Override
    public List<SecKillSkuRedisTo> getCurrentSeckillSkus() {
        log.info("getCurrentSeckillSkus????????????......");

        //1???????????????????????????????????????
        long currentTime = new Date().getTime();

        //2??????????????????????????????????????????????????????
        try(Entry entry = SphU.entry("seckillSkus")){
            Set<String> keys = redisTemplate.keys(SESSION_CACHE_PREFIX + "*");
            for (String key : keys) {
                //???seckill:sessions:?????????????????????
                String replace = key.replace(SESSION_CACHE_PREFIX, "");
                String[] s = replace.split("_");
                long startTime = Long.parseLong(s[0]);
                long endTime = Long.parseLong(s[1]);
                if (currentTime >= startTime && currentTime <= endTime) {
                    //?????????????????????  -100, 100??????????????????????????? ??????key
                    List<String> rangeKeys = redisTemplate.opsForList().range(key, -100, 100);
                    BoundHashOperations<String, String, Object> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                    //??????????????????key
                    List<Object> list = hashOps.multiGet(rangeKeys);
                    if (CollectionUtils.isNotEmpty(list)) {
                        List<SecKillSkuRedisTo> collect = list.stream().map(item -> {
                            SecKillSkuRedisTo redis = JSON.parseObject((String) item, SecKillSkuRedisTo.class);
                            //redis.setRandomCode(null);    ?????????????????????????????????
                            return redis;
                        }).collect(Collectors.toList());
                        return collect;
                    }
                }
            }
        } catch (BlockException e){
            log.error("getCurrentSeckillSkus????????????......{}", e.getMessage());
        }

        return null;
    }

    @Override
    public SecKillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        //1?????????????????????????????????????????????key
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        //???????????????????????????key
        Set<String> keys = hashOps.keys();
        String regx = "\\d_" + skuId;
        for (String key : keys) {
            //40_1 ??????????????????
            boolean matches = Pattern.matches(regx, key);
            if (matches) {
                SecKillSkuRedisTo redisTo = JSON.parseObject(hashOps.get(key), SecKillSkuRedisTo.class);
                //?????????, ????????????????????????????????????????????????????????????
                long startTime = redisTo.getStartTime();
                long endTime = redisTo.getEndTime();
                long currentTime = new Date().getTime();
                if (!(currentTime >= startTime && currentTime <= endTime)) {
                    redisTo.setRandomCode(null);
                }
                return redisTo;
            }
        }
        return null;
    }

    @Override
    public String kill(String skillId, String key, Integer num) {
        MemberRespVo respVo = LoginUserInterceptor.loginUser.get();
        //1??????????????????????????????????????????
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String json = hashOps.get(skillId);
        if (StringUtils.isBlank(json)) {
            return null;
        }
        SecKillSkuRedisTo redis = JSON.parseObject(json, SecKillSkuRedisTo.class);
        //2????????????????????????
        long startTime = redis.getStartTime();
        long endTime = redis.getEndTime();
        long currentTime = new Date().getTime();
        long ttl = endTime - currentTime;
        if (currentTime >= startTime && currentTime <= endTime) {
            //3???????????????????????????id
            String randomCode = redis.getRandomCode();
            String sku = redis.getPromotionSessionId() + "_" + redis.getSkuId();
            if (randomCode.equals(key) && sku.equals(skillId)) {
                //4?????????????????????????????????
                BigDecimal limit = redis.getSeckillLimit();
                if (num <= limit.intValue()) {
                    //5?????????????????????????????????????????????????????????????????????; ???????????????????????????????????? userId_sessionId_skuId
                    //setnx,?????????????????????
                    String redisKey = respVo.getId() + "_" + sku;
                    //????????????
                    Boolean absent = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                    if (absent) {
                        //???????????????????????????????????????????????????????????????
                        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                        //Acquire???????????????tryAcquire???????????????????????????????????????
                        //boolean b = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                        boolean b = semaphore.tryAcquire(num);
                        if (b) {
                            //????????????
                            //????????????????????????mq?????????
                            String timeId = IdWorker.getTimeId();
                            SeckillOrderTo orderTo = new SeckillOrderTo();
                            orderTo.setOrderSn(timeId);
                            orderTo.setMemberId(respVo.getId());
                            orderTo.setNum(num);
                            orderTo.setPromotionSessionId(redis.getPromotionSessionId());
                            orderTo.setSkuId(redis.getSkuId());
                            orderTo.setSeckillPrice(redis.getSeckillPrice());
                            rabbitTemplate.convertAndSend(ORDER_EXCHANGE, SECKILL_ROUTING_KEY, orderTo);
                            return timeId;
                        }
                    }
                }
            }
        }
        return null;
    }
}
