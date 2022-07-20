package com.atguigu.gulimall.seckill.stateMachine.mock;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * mock开关控制
 *
 * @Author: zengweipei
 * @CreateTime: 2021-12-04 10:30
 * @Description:
 */
@Configuration
@Conditional(ConditionalOnMockSwitch.class)
public class MockSwitchConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("#{mock.switch}")
    private boolean mockSwitch;


    @Value("#{mock.td.switch}")
    private boolean mockTdSwitch;


    @Value("#{fmq.risk.consumer.topic}")
    private String riskTopic;

    @Value("#{fmq.risk.tax.consumer.topic}")
    private String newRiskTopic;

    public boolean isMockSwitch() {
        return mockTdSwitch;
    }

    public void process(String flowNode, Map<String, Object> contextBO, String bizId, String pdId) {
        if (!mockSwitch) {
            return;
        }
        logger.info("mock 流程节点 flowNode :{}", flowNode);
        //如果是mock的话
        switch (flowNode) {
            //授信决策处理中
            case "CREDIT_APPROVAL":
                break;
            case "DISBURSE_APPROVAL":
                break;

            default:

        }

    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.setRequestFactory();
        HttpComponentsClientHttpRequestFactory hcchrf = new HttpComponentsClientHttpRequestFactory();
        // 指定连接池配置，否则线上容易出现请求排队导致接口超时问题
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(100);

        RequestConfig.Builder rcb = RequestConfig.custom();
        // 按需注入
        rcb.setConnectTimeout(4000);
        rcb.setSocketTimeout(6000);
        rcb.setConnectionRequestTimeout(6000);
        hcchrf.setHttpClient(
                HttpClientBuilder.create()
                        .setConnectionManager(connectionManager)
                        .build());
        restTemplate.setRequestFactory(hcchrf);
        return restTemplate;
    }
}

