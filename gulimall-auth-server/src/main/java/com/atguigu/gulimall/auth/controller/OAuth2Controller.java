package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理社交登录请求的
 */
@Slf4j
@Controller
public class OAuth2Controller {

    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        //1.根据code换取accessToken
        //https://api.weibo.com/oauth2/access_token?client_id=522349786&client_secret=b4fa6bbfbce7bee64daf59af8d5e78ba&grant_type=authorization_code&redirect_uri=http://gulimall.com/oauth2.0/weibo/success&code=30b9be4cf799ed843c4629e2dbacc051
        Map<String, String> map = new HashMap<>();
        map.put("client_id","522349786");
        map.put("client_secret","b4fa6bbfbce7bee64daf59af8d5e78ba");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code",code);
        HttpResponse res = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post",new HashMap<String, String>(), new HashMap<String, String>(), map);
        if (res.getStatusLine().getStatusCode() == 200){
            //获取到了accessToken
            HttpEntity entity = res.getEntity();
            String json = EntityUtils.toString(entity);
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

            //知道了当前是哪个社交用户
            //1）、如果当前用户是第一次进入网站，自动注册进来（为当前用户生成一个会员信息账号，以后这个社交账号就对应指定的会员）
            R r = memberFeignService.oauth2login(socialUser);
            if (r.getCode() == 0){
                //登录成功就跳回首页
                //向redis中存数据  MemberRespVo需要序列化
                MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {});
                System.out.println(data);
                log.info("登录成功：用户信息{}",data);
                /**
                 * 1、第一次使用session；命令浏览器保存卡号，JSESSIONID这个cookie；
                 * 以后浏览器访问哪个网站就会带上这个网站的cookie；
                 * 子域之间；gulimall.com    auth.gulimall.com   order.gulimall.com
                 * 发卡的时候，（指定域名为父域名），即使是子域系统发的卡，也能让父域直接使用。
                 * TODO 1、默认发的令牌。session=abcdefg。作用域：当前域；（解决子域session共享的问题）
                 * TODO 2、使用JSON序列化的方式来序列化对象到redis
                 * new Cookie("JSESSIONID","123456789").setDomain("");
                 * servletResponse.addCookie();
                 */
                session.setAttribute("loginUser",data);
                return "redirect:http://gulimall.com";
            }else{
                return "redirect:http://auth.gulimall.com/login.html";
            }

        }else{
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

}
