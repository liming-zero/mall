package com.atguigu.gulimall.search.thread;

import lombok.SneakyThrows;

import java.util.concurrent.*;

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
     *              2）、new THreadPoolExecutor
     *          2、开发中为什么使用线程池？
     *              降低资源的消耗。（降低线程创建和销毁带来的损耗）
     *              提高响应速度。（当有新任务时使用等待分配任务的线程池，无需创建新的线程就能执行）
     *              提高线程的可管理性。（线程池会根据当前系统特点对池内的线程进行优化处理，减少创建和销毁线程带来的系统开销）
     *      区别：
     *          1、2不能得到返回值。3可以获取返回值
     *          1、2、3都不能控制资源
     *          4 可以控制资源，性能稳定
     *
     *  CompletableFuture异步编排：
     *      CompletableFuture<T> implement Future<T>,CompletionStage<T>
     *      通过线程池性能稳定，也可以获取执行结果，并捕获异常。但是，在业务复杂情况下，一个异步调用可能会依赖于另一个异步调用的执行结果。
     */

    //我们以后在业务代码里面，以上三种启动线程的方式都不用，因为特别消耗资源。【将所有的多线程异步任务都交给线程池进行】
    //new Thread(() -> System.out.println("hello")).start();

    //当前系统中池只有一俩个，每个异步任务，提交给线程池让他自己去执行就行
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);



    //测试CompletableFuture异步编排
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /**
         * 创建异步对象:
         * CompletableFuture提供了四个静态方法来创建一个异步操作
         *      1.static CompletableFuture<void> runAsync(Runnable runnable);   //默认的在默认线程池内执行
         *      2.public static CompletableFuture<void> runAsync(Runnable runnable,Executor executor);
         *      3.public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier);    有返回值
         *      4.public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier,Executor executor);    有返回值
         *
         * CompletableFuture运行完成成功的回调方法
         *      1.whenComplete: 可以处理正常和异常的运算结果，exceptionally处理异常情况
         *      2.whenComplete和whenCompleteAsync的区别: 方法不以Async结尾，意味着action使用相同的线程执行，
         *        而Async可能会使用其他线程执行(如果是使用相同的线程池，也可能会被同一个线程选中执行)
         *
         * handle方法：只要方法完成，不管成功还是失败都返回。
         *
         * 线程串行化方法
         *      1）、thenRun(): 不能获取到上一步的执行结果
         *      2）、thenAcceptAsync(Consumer,Executor): 能接受上一步的结果，但是无返回值
         *      3）、thenApplyAsync(Function,Executor): 能接收上一步的结果，有返回值
         */
        System.out.println("main...start...");


        /*
        1.方法成功完成后的感知
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程: " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果: " + i);
            return i;
        }, executorService).whenComplete((result,exception)->{
            //whenComplete虽然能得到异常信息，但是没法修改返回数据。类似监听器。
            System.out.println("异步任务成功完成了...结果是" + result + "; 异常是" + exception);
        }).exceptionally((throwable)->{
            //exceptionally可以感知异常，同时返回默认值。
            return 10;
        });*/

        //2.handle方法，方法完成后的处理
        /*CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程: " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果: " + i);
            return i;
        }, executorService).handle((result,exception)->{
            if (result != null){
                return result * 2;
            }
            if (exception != null){
                return 0;
            }
            return 0;
        });*/

        /**
         * 3.线程串行化方法
         *  1）、thenRun(): 不能获取到上一步的执行结果
         *  2）、thenAcceptAsync(Consumer,Executor): 能接受上一步的结果，但是无返回值
         *  3）、thenApplyAsync(Function,Executor): 能接收上一步的结果，有返回值
         */
        /*CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程: " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果: " + i);
            return i;
        }, executorService).thenApplyAsync(result -> {  //R apply(T t)
            System.out.println("任务2启动了...");
            return "Hello" + result;
        }, executorService);*/

        /**
         * 4.两任务组合-都要完成(两个任务必须都完成，触发发该任务)
         *  1）、thenCombine(): 组合两个future，获取两个future的返回结果，并返回当前任务的返回值。
         *  2）、thenAcceptBoth: 组合两个future,获取两个future任务的返回结果，然后处理任务，没有返回值。
         *  3）、runAfterBoth: 组合两个future，不需要获取future的结果，只需两个future处理完任务后，处理该任务。
         */
        CompletableFuture<Object> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程启动: " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("任务1线程结束: ");
            return i;
        }, executorService);

        CompletableFuture<Object> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程启动: " + Thread.currentThread().getId());
            int i = 10 / 2;
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务2线程结束: ");
            return "Hello";
        }, executorService);

        /*CompletableFuture<String> future03 = future01.thenCombineAsync(future02, (f1, f2) -> {
            System.out.println("任务3开始..之前两个线程运行的结果，任务1线程: " + f1 + "任务2线程: " + f2);
            return "这是任务3线程的返回结果";
        }, executorService);
        System.out.println(future03.get());*/

        /**
         * 5.两任务组合-一个完成(当两个任务中，任意一个future任务完成的时候，触发发该任务) --需要两个线程返回类型相同
         *  1）、applyToEither(CompletionStage<?> other, Runnable action): 两个任务有一个执行完成，获取它的返回值，处理任务并有新的返回值。
         *  2）、acceptEither(CompletionStage<?> other, Runnable action): 两个任务有一个执行完成，获取它的返回值，处理任务，没有新的返回值。
         *  3）、runAfterEither(CompletionStage<?> other, Runnable action, Executor executor): 两个任务有一个执行完成，不需要获取future的结果，处理任务，也没有返回值。
         */
        CompletableFuture<Object> future03 = future01.applyToEitherAsync(future02, (result) -> {
            System.out.println("任务3线程启动,之前的结果是" + result);
            return "任务3线程运行的返回结果: " + result.toString();
        }, executorService);
        System.out.println(future03.get());

        /**
         * 6.多任务组合
         *  1）、allOf(): 等待所有任务完成。
         *  2）、anyOf(): 只要有一个任务完成。
         */
        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的图片信息");
            return "hello.jpg";
        });

        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的属性信息");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "黑色+256G";
        });

        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的介绍信息");
            return "华为";
        });

        //CompletableFuture<Void> allOf = CompletableFuture.allOf(futureImg, futureAttr, futureDesc);
        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(futureImg, futureAttr, futureDesc);
        //allOf.get();    //allOf等待所有任务都完成，才算完成

        //线程的返回值,future.get()会阻塞线程，在阻塞时间内线程运行成功之后拿到返回值才会执行以下代码
        //Integer result = future.get();
        //System.out.println("main...end..." + result);
        //System.out.println("main...end..." + futureImg.get() + "=>" + futureAttr.get() + "=>" + futureDesc.get());
        System.out.println("main...end..." + anyOf.get());

    }

    //测试线程池
    public void thread(String[] args) {
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


