package com.atguigu.gulimall.search.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadTest {

    /**
     * 实现线程的几种方式
     *  1）、继承Thread
     *          Thread01 extends Thread
     *          Thread01 thread = new Thread01();
     *          thread.start(); 启动线程
     *  2）、实现Runnable接口
     *          Runnable01 implement Runnable
     *          Runnable01 runnable = new Runnable01();
     *          new Thread(runnable.start());
     *  3）、实现Callable接口 + FutureTask（可以拿到返回结果，可以处理异常）
     *          Callable01 implement Callable<Object>
     *          FutureTask<Object> futureTask = new FutureTask<>(new Callable01());
     *          new Thread(futureTask.start());
     *          //阻塞等待整个线程执行完成，获取返回结果
     *          Integer integer = futureTask.get();
     *  4）、线程池[ExecutorService]
     *          给线程池直接提交任务
     *          executorService.execute(new Runnable01());
     *          1、创建：
     *              1）、Executors
     *              2）、
     *
     *  区别：
     *      1、2不能得到返回值。3可以获取返回值
     *      1、2、3都不能控制资源
     *      4 可以控制资源，性能稳定
     */

    //我们以后在业务代码里面，以上三种启动线程的方式都不用，因为特别消耗资源。【将所有的多线程异步任务都交给线程池进行】
    //new Thread(() -> System.out.println("hello")).start();

    //当前系统中池只有一俩个，每个异步任务，提交给线程池让他自己去执行就行
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        executorService.execute(new Runnable01());

        /**
         * 线程池的7大参数
         * 1、corePoolSize:[5] 核心线程数;【一直存在，除非设置了(allowCoreThreadTimeOut)】; 线程池创建好以后就准备就绪的线程数量，就等待来接收异步任务去执行。
         *                     Thread thread = new Thread(); 创建5个线程对象
         *                     thread.start();
         * 2、maximumPoolSize: 最大线程数量; 控制资源并发。
         * 3、keepAliveTime:   存活时间; 如果当前正在运行的线程数量大于core核心数量，释放空闲的线程。只要空闲线程大于指定的keepAliveTime。
         * 4、TimeUnit unit:   时间单位
         * 5、BlockingQueue<Runnable> workQueue: 阻塞队列; 如果任务有很多，就会将目前多的任务放到队列里面。
         *                    只要有线程空闲，就会去队列里面取出新的任务继续执行。
         * 6、ThreadFactory threadFactory: 线程的创建工厂。
         * 7、RejectedExecutionHandler handler: 如果队列满了，按照我们指定的拒绝策略拒绝执行任务。
         *
         * 工作顺序：
         *      1）、线程池创建，准备好core数量的核心线程，准备接收任务。
         *          1.1、core满了，就会将在进来的任务放入到阻塞队列中。空闲的core就会自己去阻塞队列获取任务执行。
         *          1.2、阻塞队列满了，就直接开新线程执行，最大只能开到max指定的数量。
         *          1.3、max满了就用RejectedExecutionHandler拒绝任务。
         *          1.4、max都执行完成，有很多空闲，在指定的时间keepAliveTime以后，释放max-core这些线程。
         *
         *          new LinkedBlockingDeque<>(): 容量默认是Integer的最大值。内存不够，需要指定默认值。
         *          private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();   默认使用这个丢弃策略
         *
         *      2）、
         *
         * 面试题：
         *      一个线程池 core 7; max 20;  queue: 50,100并发进来是怎么分配的 ?
         *          7个会立即执行，50个会进入队列，再开13个线程进行执行。剩下的30个使用‘拒绝策略’。
         *          如果不想抛弃还要执行，CallerRunsPolicy;
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5,
                200,
                10,
                null,
                new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());


        /**
         * Executors工具类中常见的四种线程池。
         * 1）、Executors.newCachedThreadPool()，core是0，带缓存的线程池，可以灵活的回收空闲线程
         * 2）、Executors.newFixedThreadPool()，固定池大小的，core=max; 都是核心线程，都不可回收
         * 3）、Executors.newScheduledThreadPool()，定时任务的线程池
         * 4）、Executors.newSingleThreadExecutor()，使用单线程的方式保证任务一个一个的顺序执行，后台从队列中获取任务。
         */
    }
}



class Runnable01 implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("runnable线程-->"+Thread.currentThread().getName()+"执行了"+i);
        }
    }
}

class Thread01 extends Thread{
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("thread线程-->"+Thread.currentThread().getName()+"执行了"+i);
        }
    }
}


