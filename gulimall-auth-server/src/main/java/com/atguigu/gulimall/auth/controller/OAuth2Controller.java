package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.vo.MemberRespVo;
import com.atguigu.gulimall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String weibo(@RequestParam("code") String code) throws Exception {
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
                MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {});
                System.out.println(data);
                log.info("登录成功：用户信息{}",data);
                return "redirect:http://gulimall.com";
            }else{
                return "redirect:http://auth.gulimall.com/login.html";
            }

        }else{
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

}
