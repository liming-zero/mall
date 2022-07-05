package com.atguigu.gulimall.seckill.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.CommonRespUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.seckill.feign.CouponFeignService;
import com.atguigu.gulimall.seckill.feign.ProductFeignService;
import com.atguigu.gulimall.seckill.vo.SeckillSessionWithSkusVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ApplicationTest {

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private ProductFeignService productFeignService;

    @Test
    public void productService(){
        R info = productFeignService.getSkuInfo(13L);
        System.out.println(info);
    }

    @Test
    public void testCoupon(){
        long start = System.currentTimeMillis();
        CommonRespUtils<List<SeckillSessionWithSkusVo>> utils = couponFeignService.getLatest3DaySession();
        String toJSONString = JSON.toJSONString(utils);
        System.out.println(toJSONString);
        long end = System.currentTimeMillis();
        System.out.println("耗时" + (end-start));
    }
}
