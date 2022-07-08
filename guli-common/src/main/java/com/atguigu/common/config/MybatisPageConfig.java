package com.atguigu.common.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * AOP原理：
 *
 * @EnableTransactionManagement原理
 * 利用TransactionManagementConfigurationSelector给容器中会导入2个组件
 *  1）、AutoProxyRegistrar
 *      1.给容器中注册InfrastructureAdvisorAutoProxyCreator组件
 *       利用后置处理器机制在对象创建以后，包装对象，返回一个代理对象，代理对象有（增强器），代理对象执行方法利用拦截器链进行调用。
 *  2）、ProxyTransactionManagementConfiguration做了什么？
 *      1.给容器中注册事务增强器：AnnotationTransactionAttributeSource解析事务注解 @Transactional
 *      2.事务拦截器
 *        TransactionInterceptor，保存了事务的属性信息，事务管理器
 *        它是一个MethodInterceptor; 容器中存放的是代理对象，代理对象调用目标方法，MethodInterceptor就会进行处理
 *        在目标方法执行的时候
 *          执行拦截器链
 *          事务拦截器：
 *              ①先获取事务相关的属性
 *              ②再获取PlatformTransactionManager，(新版本直接获取TransactionManager)
 *                如果事先没有添加指定任何TransactionManager，最终会从容器中按照类型获取一个PlatformTransactionManager
 *              ③执行目标方法
 *                如果异常，获取到事务管理器，利用事务管理回滚操作。
 *                如果正常，利用事务管理器提交事务
 *
 */
@Configuration
@EnableTransactionManagement    //开启事务
public class MybatisPageConfig {

    //引入分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor interceptor = new PaginationInterceptor();
        //设置请求的页面大于最大页后操作，true回到首页，false继续请求，默认false
        interceptor.setOverflow(true);
        //设置最大单页数量，默认500条，-1不受限制
        interceptor.setLimit(1000);
        return interceptor;
    }
}

