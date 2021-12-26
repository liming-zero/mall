package com.atguigu.gulimall.cart.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.cart.to.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 在执行目标方法之前(获取购物车信息),判断用户的登录状态。并封装传递给controller目标请求。
 */
@Component
public class CartInterceptor implements HandlerInterceptor {

    //ThreadLocal:同一个线程共享数据，拦截器--》controller--》service--》dao
    //ThreadLocal:Map<Thread,Object> threadLocal;  Thread:当前线程，Object:当前线程共享的数据
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    /**
     * 目标方法执行之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();//SpringSession包装后的session
        MemberRespVo member = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        UserInfoTo userInfoTo = new UserInfoTo();
        if (member != null) {
            //说明用户登录了
            userInfoTo.setUserId(member.getId());
        }

        //用户没登陆
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if (CartConstant.TEMP_USER_COOKIE_NAME.equals(name)){
                userInfoTo.setUserKey(cookie.getValue());   //设置user-key的值与上次访问的值一致
                userInfoTo.setTempUser(true);
            }
        }

        /**
         * 如果没有临时用户一定分配一个临时用户
         */
        if (StringUtils.isEmpty(userInfoTo.getUserKey())){
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }

        //执行目标方法之前，将用户信息放入threadLocal，从controller中取出用户信息
        threadLocal.set(userInfoTo);

        //true：每一个请求都放行
        return true;
    }

    /**
     * 业务执行之后,给浏览器添加一个cookie：user-key
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        UserInfoTo userInfoTo = threadLocal.get();

        //如果没有临时用户一定保存一个临时用户
        if (!userInfoTo.isTempUser()){
            //持续延长临时用户的过期时间
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIME_OUT);
            response.addCookie(cookie);
        }

    }
}
