package com.atguigu.gulimall.seckill.feign;

import com.atguigu.common.to.CommonRespUtils;
import com.atguigu.gulimall.seckill.vo.SeckillSessionWithSkusVo;
import feign.RequestLine;

import java.util.List;

//@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    //@GetMapping("/coupon/seckillsession/latest3DaySession")
    @RequestLine("GET /api/coupon/seckillsession/latest3DaySession")
    CommonRespUtils<List<SeckillSessionWithSkusVo>> getLatest3DaySession();
    //String getLatest3DaySession();
}
