package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.atguigu.gulimall.product.vo.frontvo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, long catelogId) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }

        //如果id为0则查所有
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }else{

            wrapper.eq("catelog_id",catelogId);

            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper);

            return new PageUtils(page);
        }
    }

    /**
     * 根据分类id查出所有的分组以及这些组里面的属性
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        //1.查询分组信息
        List<AttrGroupEntity> attrGroupEntityList = baseMapper.selectList(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));

        //2.查询所有属性
        List<AttrGroupWithAttrsVo> attrsVos = attrGroupEntityList.stream().map((group) -> {
            AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group, attrsVo);
            List<AttrEntity> attrs = attrService.getRelationAttr(attrsVo.getAttrGroupId());
            attrsVo.setAttrs(attrs);
            return attrsVo;
        }).collect(Collectors.toList());

        return attrsVos;
    }

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        //1.查出当前spu对应的所有属性的分组信息，以及当前分组下的所有属性对应的值
        //groupName、attrName、attrValue
        /**
         * 可以使用分步查询和SQL语句组合查询，此处使用SQL组合查询
         * select pav.spu_id,
         *      ag.attr_group_name groupName,
         *      ag.attr_group_id ,
         *      aar.attr_id,
         *      attr.attr_name groupName,
         *      pav.attr_value attrValue
         * from pms_attr_group ag
         * left join pms_attr_attrgroup_relation aar
         * on aar.attr_group_id = ag.attr_group_id
         * left join pms_attr attr
         * on attr.attr_id=aar.attr_id
         * left join pms_product_attr_value pav
         * on pav.attr_id=attr.attr_id
         * where ag.catelog_id=225 AND pav.spu_id='5'
         */
        List<SpuItemAttrGroupVo> vos = baseMapper.getAttrGroupWithAttrsBySpuId(spuId,catalogId);
        return vos;
    }

}