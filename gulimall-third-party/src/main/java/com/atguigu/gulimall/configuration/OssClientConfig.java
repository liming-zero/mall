package com.atguigu.gulimall.configuration;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssClientConfig {

    @Value("${spring.alibaba.cloud.oss.endpoint}")
    private String endpoint;

    @Value("${spring.alibaba.cloud.access-key}")
    private String accessId;

    @Value("${spring.alibaba.cloud.secret-key}")
    private String secretAcessKey;

    @Bean
    public OSSClient ossClient(){
        return new OSSClient(endpoint, new DefaultCredentialProvider(accessId, secretAcessKey), null);
    }
}
