package com.atguigu.gulimall.seckill.ext;

import com.atguigu.gulimall.seckill.stateMachine.handler.StepHandlerResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 扩展原理：
 * BeanPostProcessor: bean后置处理器，bean创建对象初始化前后进行拦截工作的
 * BeanFactoryPostProcessor: beanFactory的后置处理器；
 *      在BeanFactory标准初始化之后调用；所有的bean定义已经保存加载到beanFactory中，但是bean的实例还未创建
 *
 * 1）、ioc创建对象
 * 2）、invokeBeanFactoryPostProcessors(beanFactory); 执行BeanFactoryPostProcessor;
 *          如何找到所有的BeanFactoryPostProcessor并执行他们的方法；
 *          1）、直接在BeanFactory中找到所有类型是BeanFactoryPostProcessor的组件，并执行他们的方法
 *          2）、在初始化创建其他组件前面执行
 */
@ComponentScan("com.atguigu.gulimall.seckill.ext")
@Configuration
public class ExtConfig {


    @Bean
    public StepHandlerResult stepHandlerResult(){
        return StepHandlerResult.success("1");
    }

}
