package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Test
    void testRedisson() {
        System.out.println(redissonClient);
    }

    @Test
    void testRedis() {
        final ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("hello","world_" + UUID.randomUUID().toString());   //保存
        String hello = ops.get("hello");    //查询
        System.out.println("之前保存的数据是" + hello);
    }

    @Test
    void contextLoads() {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("完整路径:{}", Arrays.asList(catelogPath));
    }

}
