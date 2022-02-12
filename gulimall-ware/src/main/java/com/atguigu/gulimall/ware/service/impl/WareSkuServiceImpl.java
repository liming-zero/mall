package com.atguigu.gulimall.ware.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.SkuHasStockTo;
import com.atguigu.common.to.mq.StockDetailTo;
import com.atguigu.common.to.mq.StockLockedTo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.constant.WareRabbitConstant;
import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.enume.OrderStatusEnum;
import com.atguigu.gulimall.ware.exception.NoStockException;
import com.atguigu.gulimall.ware.feign.MemberFeignService;
import com.atguigu.gulimall.ware.feign.OrderFeignService;
import com.atguigu.gulimall.ware.feign.ProductFeignService;
import com.atguigu.gulimall.ware.service.WareOrderTaskDetailService;
import com.atguigu.gulimall.ware.service.WareOrderTaskService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private ProductFeignService feignService;
    @Autowired
    private OrderFeignService orderFeignService;
    @Autowired
    private WareOrderTaskService orderTaskService;
    @Autowired
    private WareOrderTaskDetailService orderTaskDetailService;
    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String wareId = (String) params.get("wareId");
        String skuId = (String) params.get("skuId");
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }
        if (!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1.判断如果还没有这个库存j记录新增
        List<WareSkuEntity> wareSkuEntities = baseMapper.selectList
                (new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (StringUtils.isEmpty(wareSkuEntities) || wareSkuEntities.size() == 0){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询sku的name
            //TODO 还可以用什么办法让异常出现以后不回滚? 高级
            try{
                R r = feignService.getSkuNameById(skuId);
                if (r.getCode() == 0){
                    String name = (String) r.get("name");
                    wareSkuEntity.setSkuName(name);
                }
            }catch (Exception e){}
            baseMapper.insert(wareSkuEntity);
        }else{
            baseMapper.addStock(skuId,wareId,skuNum);
        }
    }

    /**
     * 查询sku的库存 总库存-锁定的库存
     * select sum(stock-stock_locked) from wms_ware_sku where sku_id=?
     * @param skuIds
     * @return
     */
    @Override
    public List<SkuHasStockTo> getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStockTo> collect = skuIds.stream().map((skuId) -> {
            SkuHasStockTo skuHasStockTo = new SkuHasStockTo();
            Long count = baseMapper.getSkuStock(skuId);
            skuHasStockTo.setSkuId(skuId);
            skuHasStockTo.setHasStock(count==null?false:count>0);
            return skuHasStockTo;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 根据用户的收获地址计算运费
     *
     * @param id
     * @return
     */
    @Override
    public FareVo getFare(Long id) {
        FareVo fareVo = new FareVo();
        R r = memberFeignService.info(id);
        MemberAddressVo data = r.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
        });
        if (data != null) {
            String phone = data.getPhone();
            //使用手机号最后一位数字作为运费
            String substring = phone.substring(phone.length() - 1, phone.length());
            fareVo.setFare(new BigDecimal(substring));
            fareVo.setAddressVo(data);
        }
        return fareVo;
    }

    /**
     * 为某个订单锁定库存
     * RabbitMQ订单库存解锁的场景
     * 1)、下订单成功，订单过期没有支付，被系统自动取消、或者被用户手动取消。都需要解锁库存。
     * 2)、下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚。之前锁定的库存就需要自动解锁。
     */
    //指示哪些异常类型必须导致事务回滚。
    @Transactional(rollbackFor = {NoStockException.class})
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        /**
         * 保存订单工作单
         */
        WareOrderTaskEntity orderTaskEntity = new WareOrderTaskEntity();
        orderTaskEntity.setOrderSn(vo.getOrderSn());
        orderTaskService.save(orderTaskEntity);

        //按照下单的收货地址，找到一个就近仓库，锁定库存
        //1、找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(lock -> {
            Long skuId = lock.getSkuId();
            SkuWareHasStock stock = new SkuWareHasStock();
            stock.setSkuId(skuId);
            //查询当前商品在哪个仓库有库存
            List<Long> wareId = baseMapper.listWareIdHasStock(skuId);
            stock.setWareId(wareId);
            stock.setNum(lock.getCount());  //锁定库存的件数
            return stock;
        }).collect(Collectors.toList());

        //2、锁定库存
        for (SkuWareHasStock stock : collect) {
            Boolean skuStocked = false;     //当前商品没有被锁住
            Long skuId = stock.getSkuId();
            Integer num = stock.getNum();
            List<Long> wareIds = stock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                //没有任何仓库有当前商品的库存,抛异常回滚数据
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                //成功就返回1，否则就是0
                Long count = baseMapper.lockSkuStock(skuId, wareId, num);
                //1、如果每一个商品都锁定成功，将当前商品锁定了几件的工作单记录发给MQ
                //2、锁定失败。前面保存的工作单信息就回滚了。发送出去的消息即使要解锁记录，由于去数据库查不到id，所以就不用解锁。
                if (count == 1) {
                    skuStocked = true;
                    //保存订单工作单锁定状态
                    WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity(null, skuId, "", num, orderTaskEntity.getId(), wareId, 1);
                    orderTaskDetailService.save(detailEntity);
                    StockDetailTo detailTo = new StockDetailTo();
                    BeanUtils.copyProperties(detailEntity, detailTo);
                    //TODO 告诉MQ库存锁定成功
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(orderTaskEntity.getId());
                    //只发id不行，防止数据回滚以后找不到数据
                    lockedTo.setDetailTo(detailTo);
                    System.out.println("发送消息给RabbitMQ:" + JSON.toJSONString(lockedTo) + "交换机--->" + WareRabbitConstant.WARE_EXCHANGE + "路由键--->" + WareRabbitConstant.WARE_STOCK_LOCKED_ROUTING_KEY);
                    rabbitTemplate.convertAndSend(WareRabbitConstant.WARE_EXCHANGE, WareRabbitConstant.WARE_STOCK_LOCKED_ROUTING_KEY, lockedTo);
                    break;
                }
            }
            if (skuStocked == false) {
                //当前商品所有仓库都没有锁住
                throw new NoStockException(skuId);
            }
        }
        //3、所有商品都被成功锁定库存
        return true;
    }

    @Transactional
    @Override
    public void unLockStock(StockLockedTo lockedTo) {
        StockDetailTo detail = lockedTo.getDetailTo();
        Long detailId = detail.getId();
        WareOrderTaskDetailEntity detailEntity = orderTaskDetailService.getById(detailId);
        if (detailEntity != null) {
            //有库存，解锁
            Long id = lockedTo.getId();
            WareOrderTaskEntity taskEntity = orderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn();
            //根据订单号查询订单的状态
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0){
                OrderVo order = r.getData(new TypeReference<OrderVo>() {
                });
                //没有订单或者已取消订单，解锁库存
                if (order == null || OrderStatusEnum.CANCLED.getCode().equals(order.getStatus())) {
                    //当前库存工作单详情，状态是1，已锁定但是未解锁才可以解锁
                    if (detailEntity.getLockStatus() == 1){
                        baseMapper.unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum());
                        //库存解锁，库存工作单也更新状态
                        detailEntity.setLockStatus(2);      //已解锁状态
                        orderTaskDetailService.updateById(detailEntity);
                    }
                }
            }else{
                throw new RuntimeException("远程订单服务调用失败");
            }
        }
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;    //锁定库存件数
        private List<Long> wareId;
    }
}