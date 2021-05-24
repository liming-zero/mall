package com.atguigu.gulimall.product.vo.frontvo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
/**
 * 二级分类的vo
 */
public class Catalog2Vo {
    private String catalog1Id;  //一级父分类id
    private List<Catalog3VO> catalog3List;  //三级子分类
    private String id;
    private String name;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    /**
     * 三级分类的vo
     */
    public static class Catalog3VO{
        private String catalog2Id;  //父分类，二级分类id
        private String id;
        private String name;
    }
}
