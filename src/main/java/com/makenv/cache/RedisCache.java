package com.makenv.cache;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationUtils;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by wgy on 2016/8/6.
 * redis的缓存工具
 */
@Component
public class RedisCache implements InitializingBean {

    public RedisSerializer getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(RedisSerializer keySerializer) {
        this.keySerializer = keySerializer;
    }

    public RedisSerializer getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(RedisSerializer valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    private RedisSerializer keySerializer;

    private RedisSerializer valueSerializer;

    public RedisTemplate<Serializable, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<Serializable, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Resource(name="redisTemplate")
    private RedisTemplate<Serializable,Object> redisTemplate;

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    public void delete(Collection keys) {

        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        final byte[][] rawKeys = rawKeys(keys);

        redisTemplate.execute(new RedisCallback<Object>() {

            public Object doInRedis(RedisConnection connection) {
                connection.del(rawKeys);
                return null;
            }
        }, true);
    }

    private byte[][] rawKeys(Collection<String> keys) {

        final byte[][] rawKeys = new byte[keys.size()][];

        int i = 0;

        for (String key : keys) {

            rawKeys[i++] = rawKey(key);
        }

        return rawKeys;
    }


    /**
     * 批量删除key
     *
     * @param pattern
     */
    public void removePattern(final String pattern) {

        Set<byte[]> keys = redisTemplate.execute(new RedisCallback<Set<byte[]>>() {

            public Set<byte[]> doInRedis(RedisConnection connection) {

                return connection.keys(rawKey(pattern));
            }
        }, true);

        Set <Serializable> set = SerializationUtils.deserialize(keys,keySerializer);

        if (keys.size() > 0)

            delete(keys);
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {

        if (exists(key)) {

            redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {

                    connection.del(rawKey(key));

                    return null;
                }
            });
        }
    }

    private byte[] rawKey(String key) {

        return keySerializer.serialize(key);
    }

    private byte[] rawValue(Object value) {

        return valueSerializer.serialize(value);
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {

      return redisTemplate.execute(new RedisCallback<Boolean>() {

          public Boolean doInRedis(RedisConnection connection) {

              return connection.exists(rawKey(key));
          }
      }, true);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {

        return redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                return valueSerializer.deserialize(connection.get(rawKey(key)));
            }
        });

    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {

                    connection.set(rawKey(key),rawValue(value));

                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {

        boolean result = false;

        try {

            redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {

                    connection.setEx(rawKey(key),expireTime,rawValue(value));

                    return null;
                }
            });
            result = true;

        } catch (Exception e) {

            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        this.keySerializer = redisTemplate.getStringSerializer();

        this.valueSerializer = redisTemplate.getDefaultSerializer();

    }
}
