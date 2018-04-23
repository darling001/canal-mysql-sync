package com.wanjun.canalsync.client.config;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-04-19
 */
//@Configuration
public class RedisConfig {

    //@Bean
   // @ConfigurationProperties(prefix = "spring.redis")
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        return jedisConnectionFactory;
    }

    //@Bean(value = "redisTemplate")
    public RedisTemplate redisTemplate() {

        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

}
