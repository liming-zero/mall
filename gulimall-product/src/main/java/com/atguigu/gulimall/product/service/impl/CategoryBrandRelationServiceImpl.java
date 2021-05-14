package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryBrandRelationDao;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();

        //查询详细 名字
        CategoryEntity category = categoryService.getById(catelogId);
        BrandEntity brand = brandService.getById(brandId);
        categoryBrandRelation.setBrandName(brand.getName());
        categoryBrandRelation.setCatelogName(category.getName());

        baseMapper.insert(categoryBrandRelation);

    }

    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandId(brandId);
        categoryBrandRelationEntity.setBrandName(name);

        UpdateWrapper<CategoryBrandRelationEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("brand_id",brandId);
        baseMapper.update(categoryBrandRelationEntity,wrapper);
    }

    @Override
    public void updateCategory(Long catId, String name) {
        baseMapper.updateCategory(catId,name);
    }

}