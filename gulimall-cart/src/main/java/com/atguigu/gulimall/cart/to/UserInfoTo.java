package com.atguigu.gulimall.cart.to;

import lombok.Data;
import lombok.ToString;

/**
 * 用户传输对象
 */
@ToString
@Data
public class UserInfoTo {
    private Long userId;    //如果登录了有userId
    private String userKey; //如果没登录有一个用户的临时cookieId
    private boolean tempUser = false;   //判断浏览器是否有临时用户
}
