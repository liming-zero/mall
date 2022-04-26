package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.ProductFeignClient;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.to.SkuInfoTo;
import com.atguigu.gulimall.cart.to.UserInfoTo;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ThreadPoolExecutor poolExecutor;

    private final String CART_PREFIX = "gulimall:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();

        String cartInfo = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(cartInfo)) {
            //如果redis中没有当前商品信息，添加新商品到购物车

            CartItem cartItem = new CartItem();
            //runAsync不关心返回值    supplyAsync关心返回值
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                //1.远程查询当前要添加的商品的信息
                R skuInfo = productFeignClient.getSkuInfo(skuId);
                SkuInfoTo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoTo>() {
                });
                //将商品信息添加到购物车
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(info.getSkuDefaultImg());
                cartItem.setTitle(info.getSkuTitle());
                cartItem.setSkuId(skuId);
                cartItem.setPrice(info.getPrice());
            }, poolExecutor);

            //2.远程查询Sku的组合信息
            CompletableFuture<Void> getSkuAttrValuesTask = CompletableFuture.runAsync(() -> {
                List<String> values = productFeignClient.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(values);
            }, poolExecutor);

            try {
                CompletableFuture.allOf(getSkuInfoTask, getSkuAttrValuesTask).get();
            } catch (InterruptedException e) {
                log.error("异步任务执行异常{}", e.getMessage());
            } catch (ExecutionException e) {
                log.error("异步任务执行异常{}", e.getMessage());
            }

            //字符流默认使用jdk的序列化，需要转为json
            String s = JSON.toJSONString(cartItem);
            //将购物车数据以hash结构存储到redis
            cartOps.put(skuId.toString(), s);
            return cartItem;
        } else {
            //购物车中有此商品，修改数量
            CartItem item = JSON.parseObject(cartInfo, CartItem.class);
            item.setCount(item.getCount() + num);
            //字符流默认使用jdk的序列化，需要转为json
            String s = JSON.toJSONString(item);
            //将购物车数据以hash结构存储到redis
            cartOps.put(skuId.toString(), s);
            return item;
        }

    }

    /**
     * 获取到我们要操作的购物车
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        //首先获取用户信息，如果用户登录了，有用户id，说明登录了
        String cartKey;
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            //gulimall:cart:1
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        //购物车数据结构是一个hash，
        //boundHashOps提供了对key的“bound”(绑定)便捷化操作API，可以通过bound封装指定的key，
        //然后进行一系列的操作而无须“显式”的再次指定Key，即BoundKeyOperations
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }

    /**
     * 防止重复添加商品到购物车
     *
     * @param skuId
     * @return
     */
    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        String cartInfo = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(cartInfo, CartItem.class);
        return cartItem;
    }

    @Override
    public Cart getCartList() {
        //先区分用户有没有登录
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Cart cart = new Cart();
        if (userInfoTo.getUserId() != null) {
            //1.登录
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> tempCartItems = getCartItems(cartKey);
            //2.如果临时购物车有数据还需要进行合并
            String tempCartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> tempItemList = getCartItems(tempCartKey);
            if (tempItemList != null){
                for (CartItem cartItem : tempCartItems) {
                    addToCart(cartItem.getSkuId(),cartItem.getCount());
                }
            }
            //3.获取登录后的购物车的数据【包含合并后的临时购物车的数据】
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
            //4.清空临时购物车
            this.clearCart(tempCartKey);
        }else{
            //没登录
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }

    /**
     * 根据购物车键获取所有的购物项
     * @param cartKey
     * @return
     */
    public List<CartItem> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOperations.values();
        //获取购物车中所有的购物项
        if (values != null && values.size() > 0){
            List<CartItem> cartItems = values.stream().map((obj) -> {
                String str = obj.toString();
                CartItem cartItem = JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return cartItems;
        }
        return null;
    }

    /**
     * 清空临时购物车
     * @param cartKey
     */
    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    /**
     * 修改购物项选中状态
     * @param skuId
     * @param check
     */
    @Override
    public void checkItem(Long skuId, Integer check) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1 ? true : false);
        String jsonString = JSON.toJSONString(cartItem);
        //将选中的商品在redis中保存
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),jsonString);
    }

    /**
     * 修改购物项数量
     * @param skuId
     * @param num
     */
    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        String jsonString = JSON.toJSONString(cartItem);
        //在redis中改变商品的数量
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),jsonString);
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo == null){
            return null;
        }
        String cartKey = CART_PREFIX + userInfoTo.getUserId();
        List<CartItem> cartItems = this.getCartItems(cartKey);
        //获取所有被选中的购物项
        List<CartItem> collect = cartItems.stream().filter(cart -> cart.getCheck()).map(cart ->{
            //TODO 更新为最新价格
            R r = productFeignClient.getPrice(cart.getSkuId());
            String data = (String) r.get("data");

            cart.setPrice(new BigDecimal(data));
            return cart;
        }).collect(Collectors.toList());
        return collect;
    }


}
