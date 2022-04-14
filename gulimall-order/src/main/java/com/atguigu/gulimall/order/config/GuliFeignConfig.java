package com.atguigu.gulimall.order.config;

import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.cloud.openfeign.encoding.BaseRequestInterceptor;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
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

    //@Bean
    public Object getFeignObject(){
        return Feign.builder()
                .retryer(new Retryer.Default(5000,5000,3))
                .encoder(new Encoder.Default())
                .decoder(new Decoder.Default())
                .logLevel(Logger.Level.BASIC)
                .logger(new Logger() {
                    @Override
                    protected void log(String configKey, String format, Object... args) {
                        System.out.println(String.format(configKey + ":" + format, args));
                    }
                })
                .options(new Request.Options(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, true))
                .requestInterceptor(template -> {
                    template.header("Content-Type", "application/json;charset=utf-8");
                    template.header("gwAppId", "");
                    template.header("gwRequestTs", "");
                })
                //.target(Object.class, "");
                .target(new Target<Object>() {
                    @Override
                    public Class<Object> type() {
                        //此目标应用于的接口类型
                        return null;
                    }

                    @Override
                    public String name() {
                        //与此目标相关联的配置键。例如，{@code route53}
                        return null;
                    }

                    @Override
                    public String url() {
                        //base HTTP目标URL。例如，{@code https://api/v2}
                        return null;
                    }

                    @Override
                    public Request apply(RequestTemplate template) {
                        return template.request();
                    }
                });
    }
}
