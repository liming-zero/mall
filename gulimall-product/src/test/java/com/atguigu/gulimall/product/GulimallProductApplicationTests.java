package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.SkuSaleAttrValueDao;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.service.SkuSaleAttrValueService;
import com.atguigu.gulimall.product.service.SpuInfoService;
import com.atguigu.gulimall.product.vo.frontvo.SkuItemSaleAttrVo;
import com.atguigu.gulimall.product.vo.frontvo.SkuItemVo;
import com.atguigu.gulimall.product.vo.frontvo.SpuBaseAttrVo;
import com.atguigu.gulimall.product.vo.frontvo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private SkuSaleAttrValueDao saleAttrValueDao;
    @Autowired
    private SpuInfoService spuInfoService;

    @Test
    public void spuUp(){
        spuInfoService.spuUp(11L);
    }

    @Test
    public void testAspect(){
        Object o = new Object();
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(o.getClass());
    }

    @Test
    public void testSQL02(){
        List<SkuItemSaleAttrVo> saleAttrsBySpuId = saleAttrValueDao.getSaleAttrsBySpuId(5L);
        System.out.println(saleAttrsBySpuId);
    }

    @Test
    public void testSQL01(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(7L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }

    /**
     * 测试分布式闭锁
     */
    @Test
    void testLockDoor(){
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        door.countDown();//计数减一
        try {
            door.await();   //等待闭锁都完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRedisson() {
        System.out.println(redissonClient);
        RLock lock = redissonClient.getLock("lock");
        /**
         * 1.如果我们指定了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是我们指定的时间。
         * 2.如果未指定锁的超时时间，就使用30 * 1000【LockWatchdogTimeout看门狗的默认时间】
         *   只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】,每隔10s就会再次续期。续成30s
         *   internalLockLeaseTime【看门狗时间】 / 3, 10s
         */
        lock.lock(10, TimeUnit.SECONDS);    //10秒自动解锁，自动解锁时间一定要大于业务的执行时间，因为它不会自动续期
        try {
            System.out.println("，加锁成功，执行业务");
            Thread.sleep(30000);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();//解锁
            System.out.println("解锁成功");
        }

        //TODO 读写锁(保证一定能获取到最新数据，写锁是一个排他锁(互斥锁)。读锁是一个共享锁) 写锁执行业务未成功，读锁读不到数据
        //写 + 读 ： 等待写锁释放    写 + 写 ： 阻塞方式    读 + 写 ： 等待读锁释放     读 + 读 ：相当于无锁，并发读，只会在redis记录好，所有当前的读锁，他们都会同时加锁成功
        //只要有写的存在，都必须等待
        RReadWriteLock writeLock = redissonClient.getReadWriteLock("wr-lock");
        writeLock.writeLock();  //加写锁，读锁是readLock
    }

    @Test
    void testRedis() {
        final ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("hello","world_" + UUID.randomUUID());   //保存
        String hello = ops.get("hello");    //查询
        System.out.println("之前保存的数据是" + hello);
    }

    @Test
    void contextLoads() {
        Long[] catelogPath = categoryService.findCatelogPath(null);
        log.info("完整路径:{}", Arrays.asList(catelogPath));
    }

}
