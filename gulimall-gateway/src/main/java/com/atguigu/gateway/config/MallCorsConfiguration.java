package com.atguigu.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class MallCorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        //1.配置跨域
        corsConfiguration.addAllowedHeader("*");    //允许哪些请求头进行跨域
        corsConfiguration.addAllowedMethod("*");    //允许哪些请求方式进行跨域
        corsConfiguration.addAllowedOrigin("*");    //允许哪些请求来源进行跨域
        corsConfiguration.setAllowCredentials(true);//是否允许携带cookie进行跨域

        //注册跨域配置
        configurationSource.registerCorsConfiguration("/**",corsConfiguration);

        return new CorsWebFilter(configurationSource);
    }
}
