package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@Slf4j
@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private CategoryService categoryService;


    @Test
    void contextLoads() {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("完整路径:{}", Arrays.asList(catelogPath));
    }

}
