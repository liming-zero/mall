package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundsTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    /**
     * SpringCloud执行流程
     *  1) @RequestBody将这个方法参数对象转为json
     *  2) 找到gulimall-coupon服务，给/coupon/spubounds/save发送请求.
     *     将上一步转的json数据放在请求体位置，发送请求。
     *  3) 对方服务收到请求，请求体中有json数据。
     *     (@RequestBody SpuBoundsEntity spuBoundsEntity); 将请求体中的json数据转为SpuBoundsEntity实体类
     *
     *  只要json数据模型是兼容的，双方服务无需使用同一个对象发送和接收
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
