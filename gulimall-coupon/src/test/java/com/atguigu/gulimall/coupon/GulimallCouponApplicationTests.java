package com.atguigu.gulimall.coupon;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

//@SpringBootTest
class GulimallCouponApplicationTests {

    public static void main(String[] args) {
        int i = 1;
        System.out.println(args);
        System.out.println(i);


    }

    @Test
    void contextLoads() {
        //年月日不带时分秒
        LocalDate now = LocalDate.now();
        LocalDate plus1 = now.plusDays(1);  //加1天
        LocalDate plus2 = now.plusDays(2);  //加2天
        System.out.println(plus1);
        System.out.println(plus2);

        //一天之内最大时间和最小时间
        LocalTime min = LocalTime.MIN;
        LocalTime max = LocalTime.MAX;
        System.out.println(min);
        System.out.println(max);

        //计算起始时间和结束时间
        LocalDateTime startDate = LocalDateTime.of(now, min);
        LocalDateTime endDate = LocalDateTime.of(plus2, max);
        System.out.println(startDate);
        System.out.println(endDate);

        LocalDateTime current = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        Duration between = Duration.between(current, end);
        long days = between.toDays();
        System.out.println(String.format("相差的天数:%s", days));
        System.out.println(current);
    }

    public static String getDate(){
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }

}
