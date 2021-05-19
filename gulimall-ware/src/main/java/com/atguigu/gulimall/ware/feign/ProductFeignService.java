package com.atguigu.gulimall.ware.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("gulimall-product")
public interface ProductFeignService {

    /**
     *  1) 让所有请求过网关：
     *      1.@FeignClient("gulimall-gateway")：给网关所在的机器发请求
     *      2./api/product/skuinfo/info/{skuId}
     *  2) 直接让后台指定服务处理
     *      1.@FeignClient("gulimall-product")
     *      2./product/skuinfo/info/{skuId}
     */

    //远程获取Sku的name
    @GetMapping("product/skuinfo/getSkuName")
    R getSkuNameById(@RequestParam("skuId") Long skuId);
}
