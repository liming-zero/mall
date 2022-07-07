package com.atguigu.gulimall.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

import java.util.Arrays;

@Slf4j
@Aspect
public class LogAspect {

    @Pointcut("execution(public int com.atguigu.gulimall.order.aop.MathCalculator.*(..))")
    public void pointCut(){}

    @Before("pointCut()")
    public void logStart(JoinPoint joinPoint){
        //System.out.println("前置通知：触发运行。。。参数列表是{"+ Arrays.asList(joinPoint.getArgs())+"}");
        log.info("前置通知：触发运行。。。参数列表是{}", Arrays.asList(joinPoint.getArgs()));
    }

    @After("pointCut()")
    public void logEnd(){
        System.out.println("后置通知：除法结束。。。");
    }

    @AfterReturning(value = "pointCut()", returning = "result")
    public void logReturn(Object result){
        System.out.println("返回通知：触发正常返回。。。运行结果是{"+ result +"}");
    }

    @AfterThrowing(value = "pointCut()", throwing = "e")
    public void logException(Exception e){
        System.out.println("异常通知：除法异常。。。异常信息{}");
    }

}
