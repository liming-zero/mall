package com.atguigu.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

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


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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

}