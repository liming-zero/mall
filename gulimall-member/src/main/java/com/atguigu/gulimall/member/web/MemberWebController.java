package com.atguigu.gulimall.member.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberWebController {

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(){

        //查出当前登录的用户的所有订单列表数据，需要配置登录拦截器

        return "orderList";
    }
}
