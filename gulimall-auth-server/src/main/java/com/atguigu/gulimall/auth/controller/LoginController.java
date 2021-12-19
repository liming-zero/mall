package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.annotation.SysLog;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.feign.ThirdPartFeignService;
import com.atguigu.gulimall.auth.utils.RandomUtils;
import com.atguigu.gulimall.auth.vo.LoginVo;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    private ThirdPartFeignService thirdPartFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MemberFeignService memberFeignService;

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

        if (!StringUtils.isEmpty(phone)) {

            /**
             * 此处需要做页面判断，防止redis缓存多余的验证码
             */
            String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
            if (!StringUtils.isEmpty(redisCode)) {
                long l = Long.parseLong(redisCode.split("_")[1]);   //取出存入redis的当前系统时间
                if (System.currentTimeMillis() - l < 60000) {
                    //60秒内不能再发验证码
                    return R.error(BizCodeEnum.VALIDATE_SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.VALIDATE_SMS_CODE_EXCEPTION.getMsg());
                }
            }

            //TODO 1.接口防刷
            //2.验证码的再次效验:redis 存key-phone value-code
            String code = RandomUtils.getrandom();
            String subString = code + "_" + System.currentTimeMillis();


            //redis缓存验证码，并且要防止同一个phone在60秒内再次发送验证码
            redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, subString, 60, TimeUnit.SECONDS);

            thirdPartFeignService.sendCode(phone, code);
            return R.ok();
        }
        return R.error(BizCodeEnum.VALIDATE_EXCEPTION.getCode(), BizCodeEnum.VALIDATE_EXCEPTION.getMsg());
    }

    /**
     * TODO 重定向携带数据，利用session原理。将数据放在session中。只要跳到下一个页面取出这个数据以后，session里面的数据就会删掉
     * <p>
     * TODO 1.分布式下的session问题。
     *
     * @param registryVo
     * @param result
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/registry")
    public String registry(@Valid RegistryVo registryVo, BindingResult result, RedirectAttributes redirectAttributes) {

        /**
         * 如果效验有异常获取所有异常并返回异常信息
         * fieldError -> {
         *                 return fieldError.getField();
         *             }
         *      可以简写为 FieldError::getField
         */
        if (result.hasErrors()) {
            Map<String, String> collect = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            //model.addAttribute("errors",collect);
            //效验出错，转发到注册页
            // Request method 'POST' not supported 不支持请求方法post
            //用户注册->/registry[post] ---->转发forward:/register.html (路径映射默认都是get方式访问的)
            /**
             *  转发会使表单重复进行二次提交，需要使用重定向和 RedirectAttributes对象，重定向携带数据
             */
            redirectAttributes.addFlashAttribute("errors", collect);
            //效验出错，转发到注册页
            return "redirect:http://auth.gulimall.com/register.html";
        }

        //真正注册，调用远程服务进行注册
        //1.效验验证码
        String code = registryVo.getCode();

        if (!StringUtils.isEmpty(code)){
            String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + registryVo.getPhone());
            if (!StringUtils.isEmpty(s)) {
                if (code.equals(s.split("_")[0])) {
                    //1.删除验证码, 令牌机制，用过之后，令牌就直接删除
                    redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + registryVo.getPhone());

                    //2.验证码效验成功，调用远程服务进行注册
                    R r = memberFeignService.registry(registryVo);
                    if (r.getCode() == 0) {
                        //注册成功
                        return "redirect:http://auth.gulimall.com/login.html";
                    } else {
                        //注册失败
                        Map<String, String> errors = new HashMap<>();
                        errors.put("msg", r.getData("msg",new TypeReference<String>() {
                        }));
                        redirectAttributes.addFlashAttribute("errors", errors);
                        return "redirect:http://auth.gulimall.com/register.html";
                    }

                } else {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("code", "验证码错误");
                    redirectAttributes.addFlashAttribute("errors", errors);
                    //效验出错，转发到注册页
                    return "redirect:http://auth.gulimall.com/register.html";
                }
            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                //效验出错，转发到注册页
                return "redirect:http://auth.gulimall.com/register.html";
            }
        }
        return "redirect:http://auth.gulimall.com/register.html";

    }

    @SysLog("登录")
    @PostMapping("/login")
    public String login(LoginVo vo,RedirectAttributes attributes){

        R r = memberFeignService.login(vo);
        if (r.getCode() == 0){
            //远程登陆
            return "redirect:http://gulimall.com";
        }else{
            //登录失败重新定向到登录页 重定向可以使用RedirectAttributes放入错误消息，利用session原理
            Map<String,String> errors = new HashMap<>();
            errors.put("errors",r.getData("msg",new TypeReference<String>(){}));
            attributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}

