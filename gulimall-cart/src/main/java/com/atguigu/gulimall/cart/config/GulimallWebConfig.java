package com.atguigu.gulimall.cart.config;

import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {

    /**
     * 重写添加拦截器的方法
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加购物车的拦截器  /**当前路径的所有请求都让拦截器进行拦截
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
