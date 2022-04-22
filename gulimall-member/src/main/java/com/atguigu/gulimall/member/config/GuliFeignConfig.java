package com.atguigu.gulimall.member.config;

import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Configuration
public class GuliFeignConfig {

    /**
     * 加上Feign远程调用的请求拦截器
     * @return
     */
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                System.out.println("RequestInterceptorID--->" + Thread.currentThread().getId());
                //1、RequestContextHolder将请求数据放到ThreadLocal里面，拿到当前线程刚进来的这个请求
                RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
                if (attributes != null){
                    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) attributes;
                    //获取到当前请求(老请求)
                    HttpServletRequest request = requestAttributes.getRequest();
                    //2、同步请求头数据，Cookie
                    String cookie = request.getHeader("Cookie");
                    //给Feign的新请求同步老请求的Cookie
                    template.header("Cookie",cookie);
                }
                System.out.println("Feign远程调用之前先进行RequestInterceptor.apply");
            }
        };
    }
}
