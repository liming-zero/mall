package com.atguigu.gulimall.seckill.config;

import com.atguigu.gulimall.seckill.feign.CouponFeignService;
import com.atguigu.gulimall.seckill.feign.config.JacksonDecoder;
import com.atguigu.gulimall.seckill.feign.config.JacksonEncoder;
import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.optionals.OptionalDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThirdpartyConfig {

    //获取Spring容器中所有的http信息转换器
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    Logger.Level feignLoggerLevel() {
        //这里记录所有，根据实际情况选择合适的日志level
        return Logger.Level.FULL;
    }

    @Bean
    public CouponFeignService couponFeignService(){
        return Feign.builder()
                .retryer(new Retryer.Default(5000,5000,3))
                .encoder(new SpringEncoder(this.messageConverters))
                .decoder(new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(this.messageConverters))))
                .logLevel(Logger.Level.FULL)
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
                .target(new Target<CouponFeignService>() {
                    @Override
                    public Class<CouponFeignService> type() {
                        //此目标应用于的接口类型
                        return CouponFeignService.class;
                    }

                    @Override
                    public String name() {
                        //与此目标相关联的配置键。例如，{@code route53}
                        return "";
                    }

                    @Override
                    public String url() {
                        //base HTTP目标URL。例如，{@code https://api/v2}
                        return "http://localhost:88";
                    }

                    @Override
                    public Request apply(RequestTemplate template) {
                        if (template.url().indexOf("http") != 0) {
                            template.target(url());
                        }
                        return template.request();
                    }
                });
    }
}
