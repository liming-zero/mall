package com.atguigu.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;

public class MapUtils {

    /**
     * 将map转换为bean
     * @param map
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T mapToBean(Map map, Class<T> clazz) {
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(map));
        T object = jsonObject.toJavaObject(clazz);
        return object;
    }

    /**
     * 将bean转换为map
     * @param object
     * @return
     */
    public static Map<String, Object> beanToMap(Object object) {
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(object));
        Map<String, Object> resultMap = JSONObject.toJavaObject(jsonObject, Map.class);
        return resultMap;
    }

    /**
     * 将map转换为bean
     * @param map
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T mapToBean(Map map, TypeReference<T> typeReference) {
        T t = JSONObject.parseObject(JSON.toJSONString(map), typeReference);
        return t;
    }

    /**
     * @param map
     * @param key
     * @return
     * @方法名称 getBigDecimal
     * @功能描述 <pre>取出map中key对应的value值并转换为BigDecimal型</pre>
     * @作者 chenqun
     * @创建时间 2017年8月28日 上午10:26:49
     */
    public static BigDecimal getBigDecimal(final Map map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof BigDecimal) {
                    return (BigDecimal) answer;
                } else if (answer instanceof String) {
                    return new BigDecimal((String) answer);
                } else if (answer instanceof Double) {
                    return BigDecimal.valueOf((Double) answer);
                } else if (answer instanceof Float) {
                    return new BigDecimal(Float.toString((Float) answer));
                } else if (answer instanceof Integer) {
                    return new BigDecimal((Integer) answer);
                } else if (answer instanceof Long) {
                    return new BigDecimal((Long) answer);
                } else {
                    throw new NumberFormatException("BigDecimal cast exception!" + answer.getClass().getName() + " nonsupport");
                }
            }
        }
        return null;
    }

}
