package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.frontvo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查出所有分类
        List<CategoryEntity> categoryAllList = baseMapper.selectList(null);

        //2.组装成父子的树形结构
        List<CategoryEntity> level1Menus = categoryAllList.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map((menu) ->{
                    menu.setChildren(getChildrenList(menu,categoryAllList));
                    return menu;
                }).sorted((menu1,menu2) ->{
                    return (menu1.getSort() == null ? 0 :menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                }).collect(Collectors.toList());

        return level1Menus;
    }
    /*
        递归查找所有菜单的子菜单
     */
    public List<CategoryEntity> getChildrenList(CategoryEntity category,List<CategoryEntity> categoryAllList){
        List<CategoryEntity> children = categoryAllList.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == category.getCatId();
        }).map(categoryEntity -> {
            //1.找到子菜单
            categoryEntity.setChildren(getChildrenList(categoryEntity, categoryAllList));
            return categoryEntity;
        }).sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 :menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort())
        ).collect(Collectors.toList());

        return children;
    }


    /**
     * delete
     * @param asList
     */
    @Override
    public void removeMenusByIds(List<Long> asList) {
        //TODO  1.检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 找到catelogId的完整路径
     * @param catelogId
     * @return 完整路径[]
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);    //逆转集合顺序
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths){
        paths.add(catelogId);   //收集当前节点id，递归收集

        CategoryEntity category = this.getById(catelogId);
        if (category.getParentCid() != 0){
            findParentPath(category.getParentCid(),paths);
        }
        return paths;
    }

    /**
     * 级联更新所有关联的数据
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        baseMapper.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    /**
     * 加入Redis缓存逻辑
     * TODO 产生对外内存溢出异常:OutOfDirectMemoryError
     * 1）、SpringBoot2.0以后默认使用lettuce作为操作redis的客户端，它使用netty进行网络通信。
     * 2）、lettuce的bug导致堆外内存溢出，netty如果没有指定堆外内存，默认使用配置的内存 -Xmx100m
     *     可以通过-Dio.netty.maxDirectMemory进行设置
     * 解决方案：不能使用-Dio.netty.maxDirectMemory调大内存。
     * 1）、升级lettuce客户端  2）、切换使用Jedis客户端
     * 2）、lettuce、Jedis都是操作redis的底层客户端，Spring再次封装redisTemplate;
     */
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson(){
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        //1.如果缓存中没有数据则从数据库中进行查询并放入缓存中(缓存中存的数据是json字符串)
        if (StringUtils.isEmpty(catalogJSON)){
            Map<String, List<Catalog2Vo>> catalogJsonFromDB = getCatalogJsonFromDB();
            //将对象转为JSON放在缓存中，json跨语言，跨平台兼容
            //!!!注意：给缓存中放json字符串，拿出的json字符串，还要逆转为能用的对象类型，【序列化与反序列化的过程】
            String jsonString = JSON.toJSONString(catalogJsonFromDB);
            redisTemplate.opsForValue().set("catalogJSON",jsonString);
            return catalogJsonFromDB;
        }

        //2.将存入的json对象转换为指定的类型
        Map<String, List<Catalog2Vo>> stringListMap = JSON.parseObject(catalogJSON,
                new TypeReference<Map<String, List<Catalog2Vo>>>() {
        });
        return stringListMap;
    }

    /**
     * 从数据库查询并封装整个分类数据的方法
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDB() {
        /**
         * 将数据库的多次查询变为一次
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1.查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList,0L);
        //2.封装数据
        Map<String, List<Catalog2Vo>> parentCid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1.查出每一个一级分类的二级分类
            List<CategoryEntity> categoryLevel2List = getParent_cid(selectList,v.getParentCid());
            //2.封装上面的结果
            List<Catalog2Vo> catelog2Vos = null;
            if (categoryLevel2List != null) {
                catelog2Vos = categoryLevel2List.stream().map((level2) -> {
                    Catalog2Vo catelog2Vo = new Catalog2Vo(v.getCatId().toString(), null, level2.getCatId().toString(), level2.getName());
                    //1.找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> categoryLevel3List = getParent_cid(selectList,level2.getParentCid());
                    if (categoryLevel3List != null){
                        List<Catalog2Vo.Catalog3VO> catalog3VOS = categoryLevel3List.stream().map((level3) -> {
                            Catalog2Vo.Catalog3VO catalog3VO = new Catalog2Vo.Catalog3VO(level2.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                            return catalog3VO;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catalog3VOS);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return parentCid;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
    }

}