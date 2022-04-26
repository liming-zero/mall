package com.atguigu.gulimall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务：
 *      1、@EnableScheduling 开启定时任务
 *      2、@Scheduled 开启一个定时任务
 * 异步任务：
 *      1、@EnableAsync开启异步任务功能
 *      2、@Async 给希望异步任务执行的方法上标注
 *      3、自动配置类TaskExecutionAutoConfiguration
 */
@Slf4j
@Component
//@EnableAsync
//@EnableScheduling
public class HelloScheduled {
    /**
     * 1、在Spring中，cron表达式只允许6位，不允许7位的年
     * 2、在周几的位置，1-7分别代表周一到周日; MON-SUN
     * 3、定时任务不应该阻塞，默认是阻塞的
     *      1）、可以让业务运行，以异步的方式，自己提交到线程池
     *      2）、支持定时任务线程池； TaskSchedulingAutoConfiguration中设置TaskSchedulingProperties（不好使）
     *      3）、让定时任务异步执行
     *          异步任务；
     *
     *      解决方案：使用异步 + 定时任务来完成定时任务不阻塞的功能
     */
    @Scheduled(cron = "* * * ? * 2")    //周一1秒执行一次
    @Async
    public void Hello() throws InterruptedException {
        log.info("Hello");
        Thread.sleep(300);
    }
}
