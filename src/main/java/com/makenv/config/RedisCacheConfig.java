package com.makenv.config;


import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 所有的方法中加@bean所有的形参都被spring所管理
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties
@PropertySource("classpath:redis.properties")
public class RedisCacheConfig extends CachingConfigurerSupport {


    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.pass}")
    private String redisPassword;

    @Value("${redis.expireTime}")
    private int expireTime;

    @Value("${redis.default.db}")
    private int dbIndex;

    @Value("${redis.pool.maxIdle}")
    private int maxIdle;

    @Value("${redis.pool.maxWait}")
    private int maxWait;

    @Value("${redis.pool.maxActive}")
    private int maxActive;

    @Value("${redis.pool.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${redis.pool.testOnReturn}")
    private boolean testOnReturn;

    @Bean
    public JedisPoolConfig jedisPoolConfig(){

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMaxIdle(maxIdle);

        jedisPoolConfig.setMaxWaitMillis(maxWait);

        jedisPoolConfig.setMaxTotal(maxActive);

        jedisPoolConfig.setTestOnBorrow(testOnBorrow);

        jedisPoolConfig.setTestOnReturn(testOnReturn);

        return jedisPoolConfig;
    }

    @Bean
    public RedisConnectionFactory  redisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
        // Defaults
        redisConnectionFactory.setHostName(redisHost);
        redisConnectionFactory.setPort(redisPort);
        redisConnectionFactory.setPassword(redisPassword);
        redisConnectionFactory.setDatabase(dbIndex);
        redisConnectionFactory.setPoolConfig(jedisPoolConfig);
        return redisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        //redisTemplate.setKeySerializer(new StringRedisSerializer());
        //redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setConnectionFactory(cf);
        return redisTemplate;
    }

    @Bean//注入配置文件使用@propertySource指定文件,如果使用@Value需要配置下面的Bean,此方法必须是静态方法
    public static PropertySourcesPlaceholderConfigurer propertyConfigure(){

        return new  PropertySourcesPlaceholderConfigurer();
    }

    /**
     * 缓存管理器
     * @param redisTemplate
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        // Number of seconds before expiration. Defaults to unlimited (0)
        cacheManager.setDefaultExpiration(expireTime);// Sets the default expire time (in seconds)
        return cacheManager;
    }

    /**
     * @description 自定义的缓存key的生成策略</br>
     *              若想使用这个key</br>
     *              只需要讲注解上keyGenerator的值设置为customKeyGenerator即可</br>
     * @return 自定义策略生成的key
     */
    @Bean
    public KeyGenerator customKeyGenerator() {
        return (o, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(o.getClass().getName());
            sb.append(method.getName());
            for (Object obj : objects) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }
}