package com.atguigu.gulimall.seckill.stateMachine.mock;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;
import java.util.List;

public class ConditionalOnMockSwitch extends SpringBootCondition {

    private List<String> mockProfiles = Arrays.asList("dev");

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("mock.profile");
        if(StringUtils.isNotBlank(property)){
            mockProfiles = Arrays.asList(property);
        }
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();

        if(activeProfiles != null){
            for (String activeProfile : activeProfiles) {
                if(mockProfiles.contains(activeProfile)){
                    return ConditionOutcome.match();
                }
            }
        }
        return ConditionOutcome.noMatch("不匹配");
    }
}
