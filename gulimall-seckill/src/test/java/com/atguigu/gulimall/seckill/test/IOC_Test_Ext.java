package com.atguigu.gulimall.seckill.test;

import com.atguigu.gulimall.seckill.ext.ExtConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootTest
public class IOC_Test_Ext {

    @Test
    public void testIoc(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ExtConfig.class);
        context.close();
    }
}
