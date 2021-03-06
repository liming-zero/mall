package com.atguigu.gulimall.seckill.config;

import com.atguigu.gulimall.seckill.feign.CouponFeignService;
import com.atguigu.gulimall.seckill.feign.config.OptionalDecoder;
import com.atguigu.gulimall.seckill.stateMachine.mock.MockSwitchConfig;
import feign.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class ThirdpartyConfig {

    //获取Spring容器中所有的http信息转换器
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Autowired
    private MockSwitchConfig mockSwitchConfig;

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
                //.encoder(new JacksonEncoder())
                //.decoder(new JacksonDecoder())
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
                        //动态判断是否需要mock
                        String suffixUrl = template.url();
                        if (mockSwitchConfig != null && mockSwitchConfig.getMockCouponService().contains(suffixUrl)) {
                            //命中mock策略
                            log.info("调用mock优惠服务接口");
                            template.target(mockSwitchConfig.getMockCouponService());
                        }else {
                            template.target(url());
                        }
                        return template.request();
                    }
                });
    }
}
