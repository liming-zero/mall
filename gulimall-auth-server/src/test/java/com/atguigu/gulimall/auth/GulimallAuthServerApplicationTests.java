package com.atguigu.gulimall.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.to.mq.OrderTo;
import com.atguigu.common.utils.MapUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class GulimallAuthServerApplicationTests {

    @Test
    void contextLoads() {
        OrderTo orderTo = MapUtils.mapToBean(new HashMap(), OrderTo.class);
        Map map = testCastObj(Map.class);
        System.out.println(map);
    }

    public <T> T testCastObj(Class<T> clazz){
        if (clazz == Map.class || clazz == JSONObject.class || clazz == JSON.class) {
            return (T) this;
        }
        return null;
    }

}
