package com.atguigu.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.mq.OrderTo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.order.constant.OrderConstant;
import com.atguigu.gulimall.order.constant.OrderRabbitConstant;
import com.atguigu.gulimall.order.dao.OrderDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.enume.OrderStatusEnum;
import com.atguigu.gulimall.order.exception.NoStockException;
import com.atguigu.gulimall.order.feign.CartFeignService;
import com.atguigu.gulimall.order.feign.MemberFeignService;
import com.atguigu.gulimall.order.feign.ProductFeignService;
import com.atguigu.gulimall.order.feign.WmsFeignService;
import com.atguigu.gulimall.order.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.order.service.OrderItemService;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.to.OrderCreateTo;
import com.atguigu.gulimall.order.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> orderSubmitVoThreadLocal = new ThreadLocal<>();
    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private CartFeignService cartFeignService;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    private WmsFeignService wmsFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

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
     *
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
        }, executor);

        //2、远程查询购物车所有选中的购物项
        CompletableFuture<Void> getCartItemFuture = CompletableFuture.runAsync(() -> {
            /**
             * Feign在远程调用之前(会创建一个新的Request,这个请求没有任何请求头)要构造请求，调用很多的拦截器
             */
            System.out.println("cartFeignServiceID--->" + Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);   //共享之前的请求数据
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
        }, executor).thenRunAsync(() -> {
            //批量查询商品是否有库存
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> skuIds = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R r = wmsFeignService.getSkusHasStock(skuIds);
            List<SkuStockVo> stockVos = r.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (stockVos != null) {
                //是否有库存转为map
                Map<Long, Boolean> collect = stockVos.stream().collect(Collectors.toMap(stock -> stock.getSkuId(), stock -> stock.getHasStock()));
                confirmVo.setStocks(collect);
            }
        }, executor);

        //3、查询用户积分
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);

        //4、其他数据自动计算

        //5、TODO 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId(), token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);

        try {
            CompletableFuture.allOf(getAddressFuture, getCartItemFuture).get();
        } catch (InterruptedException e) {
            log.error("异步任务执行异常:{}", e.getMessage());
            throw e;
        } catch (ExecutionException e) {
            log.error("异步任务执行异常:{}", e.getMessage());
            throw e;
        }
        return confirmVo;
    }

    //@GlobalTransactional  seata效率太低，不适合使用高并发场景，使用rabbitMQ延迟消息机制保证数据最终一致性
    //isolation = Isolation.REPEATABLE_READ mysql默认的隔离级别
    @Transactional
    @Override
    public SubmitOrderRespVo submitOrder(OrderSubmitVo vo) {
        SubmitOrderRespVo respVo = new SubmitOrderRespVo();
        orderSubmitVoThreadLocal.set(vo);
        String orderToken = vo.getOrderToken();
        //1、验证令牌【令牌的对比和删除必须保证原子性,使用lua脚本】
        //最终返回0和1
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
        if (result == 0) {
            //令牌验证失败
            respVo.setCode(1);
            return respVo;
        } else {
            //令牌验证通过
            //1、创建订单
            OrderCreateTo order = createOrder();
            //2、验价
            BigDecimal payAmount = order.getOrder().getPayAmount();     //后台计算的订单价格
            BigDecimal payPrice = vo.getPayPrice();                     //前端传入的订单价格
            double abs = Math.abs(payAmount.subtract(payPrice).doubleValue());
            //验价范围绝对值小于0.01则金额对比成功
            if (abs < 0.01) {
                //3、保存订单
                saveOrder(order);
                /**
                 * 4、TODO 远程锁定库存，只要有异常，回滚订单数据
                 * 为了保持高并发，库存服务自己回滚，可以发消息给库存服务
                 * 库存服务本身也可以使用自动解锁模式
                 */
                R r = getR(order);
                if (r.getCode() == 0){
                    //锁顶库存成功了
                    respVo.setOrder(order.getOrder());

                    //TODO 远程扣减积分，出异常
                    //int i = 10 / 0;

                    //TODO 订单创建成功发送消息给MQ
                    rabbitTemplate.convertAndSend(OrderRabbitConstant.ORDER_EXCHANGE,OrderRabbitConstant.ORDER_CREATE_ROUTING_KEY,order.getOrder());
                    respVo.setCode(0);
                    return respVo;
                } else {
                    //锁定库存失败了
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);
                }
            } else {
                respVo.setCode(2);
                return respVo;
            }
        }

        //一下操作相当于lua脚本
        /*String redisToken = redisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId());
        if (orderToken != null && orderToken.equals(redisToken)){
            //令牌验证通过
            redisTemplate.delete(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId());
        }
        else{
            //不通过
        }*/
    }

    /**
     * 远程锁库存
     * @param order
     * @return
     */
    private R getR(OrderCreateTo order) {
        //订单号，所有订单项(skuId、skuName、num)
        WareSkuLockVo skuLockVo = new WareSkuLockVo();
        List<OrderItemVo> itemVos = order.getOrderItems().stream().map(item -> {
            OrderItemVo itemVo = new OrderItemVo();
            itemVo.setSkuId(item.getSkuId());
            itemVo.setCount(item.getSkuQuantity());
            itemVo.setTitle(item.getSkuName());
            return itemVo;
        }).collect(Collectors.toList());
        skuLockVo.setOrderSn(order.getOrder().getOrderSn());
        skuLockVo.setLocks(itemVos);
        R r = wmsFeignService.orderLockStock(skuLockVo);
        return r;
    }

    /**
     * 保存订单数据
     * @param OrderTo
     */
    private void saveOrder(OrderCreateTo OrderTo) {
        OrderEntity order = OrderTo.getOrder();
        order.setCreateTime(new Date());
        order.setModifyTime(new Date());
        this.save(order);
        List<OrderItemEntity> orderItems = OrderTo.getOrderItems();
        orderItemService.saveBatch(orderItems);
    }

    /**
     * 创建订单
     *
     * @return
     */
    private OrderCreateTo createOrder() {
        OrderCreateTo to = new OrderCreateTo();
        String orderSn = IdWorker.getTimeId();              //生成订单号
        //1、构建订单
        OrderEntity orderEntity = buildOrder(orderSn);
        //2、构建订单项
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);
        //3、计算价格
        computePrice(orderEntity, itemEntities);
        to.setOrder(orderEntity);
        to.setOrderItems(itemEntities);
        return to;
    }

    /**
     * 计算价格
     * @param orderEntity
     * @param itemEntities
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities) {
        //订单的总额，叠加每一个订单项的总额信息
        BigDecimal totalAmt = BigDecimal.ZERO;
        BigDecimal coupon = BigDecimal.ZERO;
        BigDecimal integration = BigDecimal.ZERO;
        BigDecimal promotion = BigDecimal.ZERO;
        BigDecimal gift = BigDecimal.ZERO;
        BigDecimal growth = BigDecimal.ZERO;
        //价格计算
        for (OrderItemEntity itemEntity : itemEntities) {
            BigDecimal realAmount = itemEntity.getRealAmount();
            totalAmt = totalAmt.add(realAmount);
            coupon = coupon.add(itemEntity.getCouponAmount());                  //优惠劵优惠金额
            integration = integration.add(itemEntity.getIntegrationAmount());   //积分优惠总额
            promotion = promotion.add(itemEntity.getPromotionAmount());         //打折总额
            gift = gift.add(new BigDecimal(itemEntity.getGiftIntegration()));   //赠送积分
            growth = growth.add(new BigDecimal(itemEntity.getGiftGrowth()));    //赠送成长值
        }
        //1、订单价格相关
        orderEntity.setTotalAmount(totalAmt);
        //订单应付总额 = 订单项最终总额 + 运费金额
        orderEntity.setPayAmount(totalAmt.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());
    }

    private OrderEntity buildOrder(String orderSn) {
        OrderEntity entity = new OrderEntity();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        entity.setMemberId(memberRespVo.getId());               //会员id
        entity.setMemberUsername(memberRespVo.getUsername());   //会员名称
        entity.setOrderSn(orderSn);
        //获取收获地址信息
        OrderSubmitVo submitVo = orderSubmitVoThreadLocal.get();
        R r = wmsFeignService.getFare(submitVo.getAddrId());
        FareVo fareVo = r.getData(new TypeReference<FareVo>() {
        });
        entity.setFreightAmount(fareVo.getFare());                          //运费
        //设置收货人信息
        entity.setReceiverCity(fareVo.getAddressVo().getCity());            //收货人城市
        entity.setReceiverDetailAddress(fareVo.getAddressVo().getDetailAddress());  //
        entity.setReceiverName(fareVo.getAddressVo().getName());            //收货人名称
        entity.setReceiverPhone(fareVo.getAddressVo().getPhone());          //收货人手机号
        entity.setReceiverPostCode(fareVo.getAddressVo().getPostCode());    //收货人邮编
        entity.setReceiverProvince(fareVo.getAddressVo().getProvince());    //收货人所在省
        entity.setReceiverRegion(fareVo.getAddressVo().getRegion());        //收货人所在区
        //设置订单状态信息
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());             //待付款
        entity.setAutoConfirmDay(7);                                        //自动确认时间
        entity.setDeleteStatus(0);                                          //未删除

        return entity;
    }

    /**
     * 构建所有订单项数据
     *
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        //最后一次确定每一个购物项的价格
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (currentUserCartItems != null) {
            List<OrderItemEntity> orderItemEntities = currentUserCartItems.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
            return orderItemEntities;
        }
        return null;
    }

    /**
     * 构建每一个订单项数据
     *
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        //1、商品的SPU信息
        Long skuId = cartItem.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo spuInfoVo = r.getData(new TypeReference<SpuInfoVo>() {
        });
        itemEntity.setSpuId(spuInfoVo.getId());
        itemEntity.setSpuBrand(spuInfoVo.getBrandId().toString());
        itemEntity.setSpuName(spuInfoVo.getSpuName());
        itemEntity.setCategoryId(spuInfoVo.getCatalogId());
        //2、商品的SKU信息
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(cartItem.getPrice());
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");   //将集合转换为带分隔符的字符串
        itemEntity.setSkuAttrsVals(skuAttr);
        itemEntity.setSkuQuantity(cartItem.getCount());
        //3、商品的优惠信息【暂不做】
        //4、积分信息(单价 * 数量)
        itemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())).intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())).intValue());
        //5、订单项的价格信息 TODO 远程查询优惠服务
        itemEntity.setPromotionAmount(BigDecimal.ZERO);
        itemEntity.setCouponAmount(BigDecimal.ZERO);
        itemEntity.setIntegrationAmount(BigDecimal.ZERO);
        //订单项的实际金额 = 单价 * 数量 - 各种优惠价
        BigDecimal orign = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        BigDecimal subtract = orign.subtract(itemEntity.getPromotionAmount()).subtract(itemEntity.getCouponAmount()).subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(subtract);
        return itemEntity;
    }


    @Override
    public OrderEntity getOrderStatus(String orderSn) {
        return baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn",orderSn));
    }

    /**
     * rabbitMQ发送消息关闭订单
     * @param order
     */
    @Override
    public void closeOrder(OrderEntity order) {
        OrderEntity entity = baseMapper.selectById(order.getId());
        //如果订单是新建状态则关闭订单
        if (OrderStatusEnum.CREATE_NEW.getCode() == entity.getStatus()){
            //不使用rabbitMQ发送的对象，因为在这期间订单信息有可能发生改变
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setId(order.getId());
            orderEntity.setStatus(OrderStatusEnum.CANCLED.getCode());
            baseMapper.updateById(orderEntity);
            //订单只要解锁成功，由于绑定了库存的解锁队列，会通知库存服务进行库存解锁
            //注意，发送给库存服务的对象要和库存服务接收的对象是同一个
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(order,orderTo);
            rabbitTemplate.convertAndSend(OrderRabbitConstant.ORDER_EXCHANGE,OrderRabbitConstant.ORDER_RELEASE_OTHER_ROUTING_KEY,orderTo);
        }
    }
}


