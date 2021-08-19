package com.atguigu.gulimall.product.vo.frontvo;

import com.atguigu.gulimall.product.vo.publishvo.Attr;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class SpuItemAttrGroupVo {
    private String groupName;
    private List<SpuBaseAttrVo> attrs;
}
