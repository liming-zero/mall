package com.atguigu.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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



}
