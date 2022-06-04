package com.atguigu.gulimall.seckill.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.seckill.to.SecKillSkuRedisTo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
@Component
public class GuliBlockExceptionHandler implements BlockExceptionHandler {

    /**
     * 请求被限制以后降级的方法
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        log.error("进入降级方法......原因:{}", e.getMessage());
        PrintWriter out = response.getWriter();
        //对应前面流控异常，浏览器展示的内容
        R error = R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        out.write(JSON.toJSONString(error));
        //out.flush();
        //out.close();
    }

    public List<SecKillSkuRedisTo> blockHandler(){
        log.error("getCurrentSeckillSkus被限流了......");
        return null;
    }
}
