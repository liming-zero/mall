package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.aop.LogAspect;
import com.atguigu.gulimall.order.aop.MathCalculator;
import com.atguigu.gulimall.order.config.MainConfigOfAop;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * AOP原理：【看给容器中注册了什么组件，这个组件什么时候工作，这个组件工作时候的功能是什么】
 * 1、@EnableAspectJAutoProxy是什么
 *    @Import(AspectJAutoProxyRegistrar.class)：给容器中导入AspectJAutoProxyRegistrar组件
 *    利用AspectJAutoProxyRegistrar自定义给容器中注册bean；BeanDefinetion
 *    AnnotationAwareAspectJAutoProxyCreator.equals(org.springframework.aop.config.internalAutoProxyCreator)
 *    给容器中注册一个AnnotationAwareAspectJAutoProxyCreator
 * 2、AnnotationAwareAspectJAutoProxyCreator
 *    ①继承关系
 *    AnnotationAwareAspectJAutoProxyCreator
 *      -> AspectJAwareAdvisorAutoProxyCreator
 *          -> AbstractAdvisorAutoProxyCreator
 *              -> AbstractAutoProxyCreator
 *                 implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware
 *                 关注后置处理器(在bean初始化完成前后做事情)、自动装配beanFactory
 *
 *   AbstractAutoProxyCreator.setBeanFactory()
 *   AbstractAutoProxyCreator.有后置处理器的逻辑
 *
 *   重写
 *   AbstractAdvisorAutoProxyCreator.setBeanFactory()
 *   AbstractAdvisorAutoProxyCreator.initBeanFactory()
 *
 *   AspectJAwareAdvisorAutoProxyCreator.initBeanFactory()
 *
 *   流程：
 *      1）、传入配置类，创建ioc容器AnnotationConfigApplicationContext();
 *      2）、注册配置类，调用refresh(); 刷新容器
 *      3）、registerBeanPostProcessors(beanFactory); 注册bean的后置处理器来方便拦截bean的创建
 *          1.先获取ioc容器中已经定义的需要创建对象的所有BeanPostProcessor
 *          2.给容器中添加其他BeanPostProcessor
 *          3.优先注册实现了PriorityOrdered接口的BeanPostProcessor
 *          4.再给容器中注册实现了Ordered接口的BeanPostProcessor
 *          5.注册没实现优先级接口的BeanPostProcessor
 *          6.注册BeanPostProcessor，实际上就是创建BeanPostProcessor对象，保存在BeanFactory容器中。
 *            创建internalAutoProxyCreator的BeanPostProcessor【AnnotationAwareAspectJAutoProxyCreator】
 *            ①创建bean的实例
 *            ②populateBean(beanName, mbd, instanceWrapper);给bean的各种属性赋值
 *            ③initializeBean(beanName, exposedObject, mbd);初始化bean
 *              ⅠinvokeAwareMethods(beanName, bean);处理Aware接口的方法回调
 *              ⅡapplyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);应用后置处理器的postProcessBeforeInitialization()
 *              ⅢinvokeInitMethods(beanName, wrappedBean, mbd);执行自定义的初始化方法
 *              ⅣapplyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);执行后置处理器的postProcessAfterInitialization()
 *            ④BeanPostProcessor(AnnotationAwareAspectJAutoProxyCreator)创建成功 --> aspectJAdvisorsBuilder
 *          7.把BeanPostProcessor注册到BeanFactory中。 beanFactory.addBeanPostProcessor(postProcessor);
 *      =========================================
 *      以上是创建AnnotationAwareAspectJAutoProxyCreator的过程
 *      AnnotationAwareAspectJAutoProxyCreator 继承=> InstantiationAwareBeanPostProcessor
 *      4）、finishBeanFactoryInitialization(beanFactory); 完成bean的初始化工作；创建剩下的单实例bean
 *          1.遍历容器中所有的bean，依次创建bean对象
 *            getBean(beanName) -> doGetBean() -> getSingleton()
 *          2.创建bean
 *            AnnotationAwareAspectJAutoProxyCreator在所有bean创建之前会有一个拦截，因为它是InstantiationAwareBeanPostProcessor后置处理器，会调用postProcessBeforeInstantiation()
 *            ①先从缓存中获取bean，如果能获取到，说明单实例bean是之前被创建过的；直接使用，否则再创建
 *             只要创建好的bean都会被缓存起来
 *            ②createBean();创建bean AnnotationAwareAspectJAutoProxyCreator会在任何bean创建之前先尝试返回bean的实例
 *             【BeanPostProcessor是在Bean对象创建完成初始化前后调用的】
 *             【InstantiationAwareBeanPostProcessor是在创建bean实例之前先尝试用后置处理器返回对象的】
 *              Ⅰ resolveBeforeInstantiation(beanName, mbdToUse); 解析BeforeInstantiation
 *                希望后置处理器在此能返回一个代理对象；如果能返回代理对象，就使用，如果不能就继续
 *                  1.后置处理器先尝试返回对象，bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
 *                    拿到所有后置处理器，如果是InstantiationAwareBeanPostProcessor，就执行后置处理器的postProcessBeforeInstantiation
 *                     if (bean != null) {
 * 						 bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
 *                     }
 *              Ⅱ doCreateBean(); 真正的去创建一个bean的实例；和3.6流程一样。
 *
 *  AnnotationAwareAspectJAutoProxyCreator【InstantiationAwareBeanPostProcessor】的作用
 *  1）、每一个bean创建之前，调用postProcessBeforeInstantiation();
 *      关心@Configuration配置类bean的创建
 *      1.判断当前bean是否在advisedBeans中（保存了所有需要增强的bean）advisedBeans.containsKey(cacheKey)
 *      2.判断当前bean是否是基础类型的，实现了Advice、Pointcut、Advisor、AopInfrastructureBean。或者是否是切面类型的isAspect()
 *        hasAspectAnnotation AnnotationUtils.findAnnotation(clazz, Aspect.class) != null;  或者field.getName().startsWith("ajc$")
 *      3.是否需要跳过
 *        ①获取候选的增强器（切面里面的通知方法） List<Advisor> candidateAdvisors
 *          每一个封装的通知方法的增强器是InstantiationAwarePointcutAdvisor;
 *          判断每一个增强器是否是AspectJPointcutAdvisor类型的；返回true，否则永远返回false
 *  2）、创建对象
 *      postProcessAfterInitialization()
 *      return wrapIfNecessary(bean, beanName, cacheKey);   //包装如果需要的情况下
 *      1.获取当前bean的所有增强器（通知方法）
 *        ①找到侯选的所有增强器（找哪些通知方法是需要切入当前bean方法的）
 *        ②获取到能在当前bean使用的增强器
 *        ③给增强器排序
 *      2.保存当前bean在advisedBeans中，
 *      3.如果当前bean需要增强，创建当前bean的代理对象
 *        ①获取所有增强器（通知方法）
 *        ②保存到proxyFactory中
 *        ③proxyFactory.getProxy(ClassLoader classLoader) 创建代理对象,Spring决定创建jdk代理或cglib代理对象
 *          JdkDynamicAopProxy(config);
 *          ObjenesisCglibAopProxy(config);
 *        ④给容器中返回当前组件使用cglib增强了的代理对象；
 *        ⑤以后容器中获取到的就是这个组件的代理对象，执行目标方法的时候，代理对象就会执行通知方法的流程。
 *
 *  3）、目标方法执行：
 *      容器中保存了组件的代理对象（cglib增强后的对象），这个对象里面保存了代理对象的详细信息（比如增强器，目标对象）
 *      1.CglibAopProxy.intercept();   拦截目标方法的执行
 *      2.根据ProxyFactory对象获取将要执行的目标方法拦截器链;
 *          List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
 *          ①List<Object> interceptorList = new ArrayList<>(advisors.length); 保存所有拦截器
 *          ②遍历所有的增强器，将其转为Interceptor;  registry.getInterceptors(advisor);
 *          ③将增强器转为List<MethodInterceptor>,
 *           如果是MethodInterceptor，直接加入到集合中
 *           再使用AdvisorAdapter将增强器转为MethodInterceptor。 返回MethodInterceptor数组
 *      3.如果没有拦截器链，直接执行目标方法
 *          拦截器链（每一个通知方法又被包装为方法拦截器，利用MethodInterceptor机制）
 *      4.如果有拦截器链，把需要执行的目标对象，目标方法，拦截器链等信息传入，调用proceed方法，new CglibMethodInvocation().proceed()，
 *      5.拦截器链的触发过程
 *          ①如果没有拦截器，执行目标方法，或者拦截器的索引和拦截器数组-1大小一样（指定到了最后一个拦截器）
 *          ②链式获取每一个拦截器，拦截器执行invoke方法，每一个拦截器等待下一个拦截器执行完成返回以后，再来执行。
 *           通过这种拦截器链的机制，保证通知方法与目标方法的执行顺序
 *
 *  总结：
 *      1）、@EnableAspectJAutoProxy开启AOP功能
 *      2）、@EnableAspectJAutoProxy会给容器中注册一个组件AnnotationAwareAspectJAutoProxyCreator
 *      3）、AnnotationAwareAspectJAutoProxyCreator是一个后置处理器
 *      4）、容器的创建流程：
 *          1.registerBeanPostProcessors() 注册后置处理器； 创建AnnotationAwareAspectJAutoProxyCreator
 *          2.finishBeanFactoryInitialization() 初始化剩下的单实例bean
 *            ①创建业务逻辑组件和切面组件
 *            ②AnnotationAwareAspectJAutoProxyCreator拦截组件的创建过程
 *            ③组件创建完之后，判断组件是否需要增强
 *              是：切面的通知方法，包装成增强器(Advisor); 给业务逻辑组件创建一个代理对象
 *      5）、执行目标方法：
 *          1.代理对象执行目标方法
 *          2.CglibAopProxy.intercept()
 *            ①得到目标方法的拦截器链（增强器包装成拦截器MethodInterceptor）
 *            ②利用拦截器的链式机制，依次进入每一个拦截器进行执行
 *          3.效果：
 *             正常执行：前置通知 --> 目标方法 --> 后置通知 --> 返回通知
 *             出现异常：前置通知 --> 目标方法 --> 后置通知 --> 异常通知
 */
@EnableAspectJAutoProxy(exposeProxy = true)

/**
 * 使用RabbitMQ
 *  1.引入amqp场景启动器
 *    RabbitAutoConfiguration就会自动生效
 *  2.给容器中自动配置了RabbitTemplate、AmqpAdmin、CachingConnectionFactory、RabbitMessagingTemplate
 *    所有的属性都是在
 *      @ConfigurationProperties(prefix = "spring.rabbitmq") spring.rabbitmq配置文件进行绑定
 *      给配置文件中配置 spring.rabbitmq 信息
 *  3.@EnableRabbit:开启Rabbit功能
 *  4.交换机不同，路由键不同，消息就会到达不同的队列
 *  5.监听消息：使用@RabbitListener注解，必须先开启@EnableRabbit注解
 *    ① @RabbitListener：类 + 方法上（监听哪些队列即可）
 *    ② @RabbitHandler：标注在方法上（重载区分不同的消息）
 *
 *  RabbitMQ业务逻辑
 *      1、创建订单-------> 绑定路由键order.create.order ------> 转发到交换机order-event-exchange
 *      2、交换机order-event-exchange ------> 根据路由键order.create.order ------> 转发到死信队列order.delay.queue
 *      3、死信队列order.delay.queue设置过期时间30分钟，代表30分钟关单。30分钟后根据路由键order.release.order ------> 路由到交换机order-event-exchange
 *      4、交换机order-event-exchange根据路由键order.release.order ------> 转发到监听订单关单队列order.release.order.queue
 *      5、订单服务监听订单关单队列order.release.order.queue进行消费关单。
 *
 *  本地【事务】失效问题
 *  同一个对象内事务方法互调默认失效，原因：绕过了代理对象，事务是使用代理对象来控制的
 *  解决：使用代理对象来调用事务方法
 *    ①引入spring-boot-starter-aop; 引入了aspectjweaver动态代理
 *    ②@EnableAspectJAutoProxy(exposeProxy = true)
 *      开启aspectJ动态代理功能，以后所有的动态代理都是aspectj创建的。（即使没有接口也可以代理，JDK默认的动态代理必须有接口）
 *      exposeProxy = true： 对外暴露代理对象
 *    ③本类互调
 *      OrderServiceImpl orderService = (OrderServiceImpl) AopContext.currentProxy();
 *      orderService.a();
 *      orderService.b();
 *
 *  Seata控制分布式事务
 *  1、每一个i微服务先必须创建undo_log表
 *  2、安装事务协调器：seata-server  https://github.com/seata/seata/releases
 *  3、整合
 *      1.guli-common添加依赖spring-cloud-starter-alibaba-seata seata-all-1.3.0
 *      2.解压并启动seata-server
 *        ①registry.conf注册中心相关的配置   修改registry type=nacos 配置使用file.conf
 *      3.所有想要用到分布式事务的微服务，都要使用seata DataSourceProxy代理自己的数据源
 *      4.每个微服务，都必须导入file.conf、registry.conf两个文件
 *        在 org.springframework.cloud:spring-cloud-starter-alibaba-seata 的org.springframework.cloud.alibaba.seata.GlobalTransactionAutoConfiguration类中，
 *        默认会使用 ${spring.application.name}-fescar-service-group作为服务名注册到 Seata Server上，如果和file.conf 中的配置不一致，会提示 no available server to connect错误
 *        也可以通过配置 spring.cloud.alibaba.seata.tx-service-group修改后缀，但是必须和file.conf中的配置保持一致
 *      5.给分布式大事务的入口标注@GlobalTransactional注解
 *      6.每一个远程的小事务使用@Transactional注解
 *      7.启动测试
 */
@EnableFeignClients
@EnableRedisHttpSession
@EnableRabbit
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.order.dao")
@SpringBootApplication
public class GulimallOrderApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfigOfAop.class);
        MathCalculator bean = context.getBean(MathCalculator.class);
        bean.div(10, 2);
        //SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
