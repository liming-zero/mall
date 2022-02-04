package com.atguigu.gulimall.order.service.impl;

import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.order.feign.CartFeignService;
import com.atguigu.gulimall.order.feign.MemberFeignService;
import com.atguigu.gulimall.order.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.order.vo.MemberAddressVo;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private CartFeignService cartFeignService;
    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 订单确认页返回需要的数据
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() throws InterruptedException, ExecutionException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        System.out.println("主线程ID--->" + Thread.currentThread().getId());
        /**
         * RequestContextHolder使用ThreadLocal共享数据，不同线程下会造成数据丢失问题
         * 所以需要获取之前的请求，每一个线程下都来共享之前的请求数据
         */
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //1、远程查询所有的收获地址列表
        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            System.out.println("memberFeignServiceID--->" + Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);   //共享之前的请求数据
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);
        },executor);

        //2、远程查询购物车所有选中的购物项
        CompletableFuture<Void> getCartItemFuture = CompletableFuture.runAsync(() -> {
            /**
             * Feign在远程调用之前(会创建一个新的Request,这个请求没有任何请求头)要构造请求，调用很多的拦截器
             */
            System.out.println("cartFeignServiceID--->" + Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);   //共享之前的请求数据
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
        },executor);

        //3、查询用户积分
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);

        //4、其他数据自动计算

        //5、TODO 防重令牌

        try {
            CompletableFuture.allOf(getAddressFuture,getCartItemFuture).get();
        } catch (InterruptedException e) {
            log.error("异步任务执行异常:{}",e.getMessage());
            throw e;
        } catch (ExecutionException e) {
            log.error("异步任务执行异常:{}",e.getMessage());
            throw e;
        }
        return confirmVo;
    }

}