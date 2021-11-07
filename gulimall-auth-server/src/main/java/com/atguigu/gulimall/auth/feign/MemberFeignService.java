package com.atguigu.gulimall.auth.feign;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.vo.LoginVo;
import com.atguigu.gulimall.auth.vo.RegistryVo;
import com.atguigu.gulimall.auth.vo.SocialUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-member")
public interface MemberFeignService {
    @PostMapping("/member/member/registry")
    R registry(@RequestBody RegistryVo registryVo);

    @PostMapping("/member/member/login")
    R login(@RequestBody LoginVo vo);


    @PostMapping("/member/member/oauth2/login")
    R oauth2login(@RequestBody SocialUser user) throws Exception;
}
