package com.atguigu.gulimall.seckill.ext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.Array;
import java.util.Arrays;

@Slf4j
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        log.info("MyBeanFactoryPostProcessor执行了postProcessBeanFactory()方法");
        int count = beanFactory.getBeanDefinitionCount();
        String[] names = beanFactory.getBeanDefinitionNames();
        log.info("当前BeanFactory中有" + count + "个bean");
        System.out.println(Arrays.asList(names));
    }
}
