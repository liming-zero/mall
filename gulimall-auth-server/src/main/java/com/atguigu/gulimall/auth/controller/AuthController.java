package com.atguigu.gulimall.auth.controller;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.ThirdPartFeignService;
import com.atguigu.gulimall.auth.vo.RegistryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    @Autowired
    private ThirdPartFeignService thirdPartFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 发送一个请求直接跳转到一个页面
     * SpringMVC的 ViewController:将请求和页面映射过来
     */

    /*@RequestMapping("login.html")
    public String loginPage(){
        return "login";
    }

    @RequestMapping("register.html")
    public String registerPage(){
        return "register";
    }*/

    @ResponseBody
    @RequestMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone) {

        /**
         * 此处需要做页面判断，防止redis缓存多余的验证码
         */
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)){
            long l = Long.parseLong(redisCode.split("_")[1]);   //取出存入redis的当前系统时间
            boolean flag = System.currentTimeMillis() - l < 60000;
            System.out.println(System.currentTimeMillis());
            System.out.println(flag);
            if (System.currentTimeMillis() - l < 60000){
                //60秒内不能再发验证码
                return R.error(BizCodeEnum.VALIDATE_SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.VALIDATE_SMS_CODE_EXCEPTION.getMsg());
            }
        }

        //TODO 1.接口防刷
        //2.验证码的再次效验:redis 存key-phone value-code
        String code = UUID.randomUUID().toString().substring(0, 5) + "_" + System.currentTimeMillis();

        //redis缓存验证码，并且要防止同一个phone在60秒内再次发送验证码
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, code, 10, TimeUnit.MINUTES);

        thirdPartFeignService.sendCode(phone, code);
        return R.ok();
    }

    /**
     * TODO 重定向携带数据，利用session原理。将数据放在session中。只要跳到下一个页面取出这个数据以后，session里面的数据就会删掉
     *
     * TODO 1.分布式下的session问题。
     * @param registryVo
     * @param result
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/registry")
    public String registry(@Valid RegistryVo registryVo, BindingResult result, RedirectAttributes redirectAttributes){

        /**
         * 如果效验有异常获取所有异常并返回异常信息
         * fieldError -> {
         *                 return fieldError.getField();
         *             }
         *      可以简写为 FieldError::getField
         */
        if (result.hasErrors()){
            Map<String, String> collect = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));
            //model.addAttribute("errors",collect);
            //效验出错，转发到注册页
            // Request method 'POST' not supported 不支持请求方法post
            //用户注册->/registry[post] ---->转发forward:/register.html (路径映射默认都是get方式访问的)
            /**
             *  转发会使表单重复进行二次提交，需要使用重定向和 RedirectAttributes对象，重定向携带数据
             */
            redirectAttributes.addFlashAttribute("errors",collect);
            return "redirect:/register.html";
        }


        //注册成功后回到登录页
        return "redirect:http://auth.gulimall.com/login.html";
    }
}

