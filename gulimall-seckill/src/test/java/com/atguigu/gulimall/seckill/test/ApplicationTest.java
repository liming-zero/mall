package com.atguigu.gulimall.seckill.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.CommonRespUtils;
import com.atguigu.gulimall.seckill.feign.CouponFeignService;
import com.atguigu.gulimall.seckill.vo.SeckillSessionWithSkusVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ApplicationTest {

    @Autowired
    private CouponFeignService couponFeignService;

    @Test
    public void testCoupon(){
        CommonRespUtils<List<SeckillSessionWithSkusVo>> utils = couponFeignService.getLatest3DaySession();
        String toJSONString = JSON.toJSONString(utils);
        System.out.println(toJSONString);
    }
}