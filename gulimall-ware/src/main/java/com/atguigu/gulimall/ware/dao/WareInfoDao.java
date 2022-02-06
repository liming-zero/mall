package com.atguigu.gulimall.ware.dao;

import com.atguigu.gulimall.ware.entity.WareInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 仓库信息
 * 
 * @author liming-zero
 * @email limingzero@outlook.com
 * @date 2021-05-03 16:19:28
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {

    List<Long> listWareIdHasStock(@Param("skuId") Long skuId);

    Long lockSkuStock(@Param("skuId")Long skuId, @Param("wareId")Long wareId, @Param("num")Integer num);
}
