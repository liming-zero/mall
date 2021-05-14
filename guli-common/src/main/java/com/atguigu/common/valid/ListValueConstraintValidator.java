package com.atguigu.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义效验器
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {

    private Set<Integer> set = new HashSet<Integer>();

    //初始化方法
    public void initialize(ListValue constraintAnnotation) {
        int[] values = constraintAnnotation.values();
        if (values.length != 0) {
            for (int value : values) {
                set.add(value);
            }
        }
    }

    /**
     * 判断是否效验成功
     * @param value 需要效验的值
     * @param context
     * @return
     */
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
