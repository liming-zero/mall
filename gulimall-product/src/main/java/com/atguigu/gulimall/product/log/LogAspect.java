package com.atguigu.gulimall.product.log;

import com.alibaba.fastjson.JSON;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class LogAspect {

    //切点 @FeignClient
    @Pointcut("@annotation(org.springframework.cloud.openfeign.FeignClient)")
    public void CallThird() {
    }

    //postMapping
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping() {
    }

    //
    @Pointcut("execution(* com.atguigu.gulimall.product.app.*.*(..))")
    public void controller() {
    }

    @Around("CallThird() || postMapping() || controller()")
    public Object timeOut(ProceedingJoinPoint joinPoint) {
        Object result = null;
        Object[] args = joinPoint.getArgs();            //切入方法的参数
        Signature signature = joinPoint.getSignature();
        log.info("监听的方法：{}", signature.getName());
        if (args[0] instanceof Map){
            //打印日志剔除掉字节流
            Map<Object, Object> newMap = getCopyMap(args[0]);
            log.info("开始调用第三方接口,map入参：{}", JSON.toJSONString(newMap));
        }else{
            log.info("开始调用第三方接口,入参：{}", JSON.toJSONString(Arrays.asList(args)));
        }
        long startTime = System.currentTimeMillis();
        log.info("开始时间:{}", dateUtil(startTime));
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            if (throwable instanceof SocketTimeoutException || throwable instanceof RetryableException) {
                log.error("调用第三方接口超时,异常原因：{}", throwable.getMessage());
                throw new IllegalArgumentException(throwable.getMessage());
            } else {
                log.error("调用第三方接口异常,异常原因{}", throwable.getMessage());
                throw new IllegalArgumentException(throwable.getMessage());
            }
        }
        if (result == null) {
            throw new IllegalArgumentException();
        }
        long stopTime = System.currentTimeMillis();
        log.info("调用第三方接口结束,出参{}", JSON.toJSONString(result));
        log.info("结束时间:{}", dateUtil(stopTime));
        log.info("总耗时:{}ms", (stopTime - startTime));
        return result;
    }

    private Map<Object, Object> getCopyMap(Object arg) {
        Map map = (Map) arg;
        Map<Object, Object> newMap = new HashMap<>();
        newMap.putAll(map);
        newMap.remove("base64");
        newMap.remove("handwritingImage");
        return newMap;
    }

    public String dateUtil(Long longTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date(longTime));
    }

    public String getMethodName(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method =  methodSignature.getMethod();
        return method.getName();
    }

}
