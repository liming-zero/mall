package com.atguigu.gulimall.product.exception;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j  //日志记录
@RestControllerAdvice("com.atguigu.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class) //指定处理什么异常
    public R handleValidateException(MethodArgumentNotValidException e){
        log.error("数据效验出现问题{},异常类型:{}",e.getMessage(),e.getClass());
        BindingResult result = e.getBindingResult();
        Map<String,String> map = new HashMap<>();
        result.getFieldErrors().forEach((items)->{
            map.put(items.getField(),items.getDefaultMessage());
        });
        return R.error(BizCodeEnum.VALIDATE_EXCEPTION.getCode(),BizCodeEnum.VALIDATE_EXCEPTION.getMsg()).put("data",map);
    }

    //如果不能精确匹配到异常则执行此方法
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
        return R.error(BizCodeEnum.UNKNOWN_EXCEPTION.getCode(), BizCodeEnum.UNKNOWN_EXCEPTION.getMsg());
    }
}
