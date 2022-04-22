package com.atguigu.gulimall.member.web;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MemberWebController {

    @Autowired
    private OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, Model model, HttpServletRequest request){
        //获取到支付宝给我们传来的所有请求数据, request验证签名，如果正确可以去修改
        Map<String, String[]> parameterMap = request.getParameterMap();
        //查出当前登录的用户的所有订单列表数据，需要配置登录拦截器
        Map<String, Object> params = new HashMap<>();
        params.put("page", pageNum.toString());
        //在远程调用时，member页面服务带了cookie，也算登录成功，所以要使用Feign的拦截器把member的cookie同步到Feign的请求头中
        R r = orderFeignService.queryPageWithItem(params);
        model.addAttribute("orders" , r);
        return "orderList";
    }
}
