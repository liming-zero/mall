package com.atguigu.gulimall.auth.utils;

import java.util.Random;

public class RandomUtils {
    public static void main(String[] args) {
        System.out.println(getrandom());
    }
    public static String getrandom(){
        String code = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int r = random.nextInt(10); //每次随机出一个数字（0-9）
            code = code + r;  //把每次随机出的数字拼在一起
        }
        return code;

    }
}
