package com.atguigu.gulimall.product.config;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
/**
 * 配置文件中的东西没有用上：
 *  1.原来和配置文件中绑定的配置类是这样的
 *    @ConfigurationProperties(prefix = "spring.cache")
 *    public class CacheProperties {}
 *  2.要让他生效，使用注解 @EnableConfigurationProperties
 */
@EnableConfigurationProperties({CacheProperties.class})
public class MyCacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties){
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        defaultCacheConfig = defaultCacheConfig.serializeKeysWith
                (RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));  //KEY默认使用String序列化
        defaultCacheConfig = defaultCacheConfig.serializeValuesWith
                (RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));  //value使用spring的json序列化

        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        //将配置文件中的所有配置都生效
        if (redisProperties.getTimeToLive() != null) {
            defaultCacheConfig = defaultCacheConfig.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            defaultCacheConfig = defaultCacheConfig.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            defaultCacheConfig = defaultCacheConfig.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            defaultCacheConfig = defaultCacheConfig.disableKeyPrefix();
        }

    return defaultCacheConfig;
    }
}
