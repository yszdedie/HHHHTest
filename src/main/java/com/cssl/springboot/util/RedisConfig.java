package com.cssl.springboot.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.UnknownHostException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tym on 2019/8/27 0027.
 * Redis缓存配置类
 * 该缓存配置不支持redis2.x.x
 */
//@EnableCaching
@Configuration
public class RedisConfig extends CachingConfigurerSupport {


    //自定义一个redisTemplate  这是一个固定的模板可以直接使用
    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate2(RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        //序列化配置
        //使用json进行序列化
        Jackson2JsonRedisSerializer jackson2 = new Jackson2JsonRedisSerializer(Object.class);
        //进行转义
        ObjectMapper om=new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2.setObjectMapper(om);
        //String 的序列化
        StringRedisSerializer srs=new StringRedisSerializer();

        //key采用String 的序列化方式
        template.setKeySerializer(srs);
        //hash的key 也采用String的序列化
        template.setHashKeySerializer(srs);
        //value 序列化采用jackson
        template.setValueSerializer(jackson2);
        //hash 的value 序列化采用jackson
        template.setHashValueSerializer(jackson2);
        template.afterPropertiesSet();


        return template;
    }
    //默认缓存管理器
    /*@Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheManager cacheManager = RedisCacheManager.create(factory);
        return cacheManager;
    }*/

    //缓存管理器
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        System.out.println("CacheManager......");
        //反序列化
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance ,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(om);

        //设置缓存配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        RedisCacheConfiguration config1 = config.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .entryTtl(Duration.ofSeconds(300))   //缓存时间,秒
                .disableCachingNullValues()         //不缓存空值
                .prefixKeysWith("users");        //前缀userssss+#id(userssss2)

        RedisCacheConfiguration config2 = config.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .entryTtl(Duration.ofSeconds(60))   //缓存时间,秒
                .disableCachingNullValues()        //不缓存空值
                .prefixKeysWith("news");            //缓存区间

        //对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("user", config1);         //user匹配@Cacheable的value属性值
        configMap.put("news", config2);

        //初始化一个RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);

        //初始化RedisCacheManager
        RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter, config, configMap);
        return cacheManager;
    }
}
