package com.atguigu.gulimall.ware.dao;

import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author liming-zero
 * @email limingzero@outlook.com
 * @date 2021-05-03 16:19:28
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
