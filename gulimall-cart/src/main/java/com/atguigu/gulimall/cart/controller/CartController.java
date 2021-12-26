package com.atguigu.gulimall.cart.controller;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.to.UserInfoTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class CartController {

    /**
     * 浏览器有一个cookie；user-key;标识用户身份，一个月后过期
     * 如果第一次使用jd的购物车功能，都会给一个临时的用户身份；
     * 浏览器以后保存，每次访问都会带上这个cookie;
     *
     * 登录：session中有用户数据
     * 没登陆：按照cookie里面带来的user-key来做
     * 第一次访问购物车，如果没有临时用户，帮忙临时创建一个临时用户。
     * @param
     * @return
     */
    @GetMapping("/cart.html")
    public String cartPage(HttpSession session){
        //1、快速得到用户信息，id，user-key
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();


        return "cartList";
    }
}
