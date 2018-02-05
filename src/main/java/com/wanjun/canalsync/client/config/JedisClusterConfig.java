package com.wanjun.canalsync.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-19
 */
@Configuration
@ConditionalOnClass({JedisCluster.class})
public class JedisClusterConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public JedisCluster jedisClusterFactory() {
        String[] serverArray = redisProperties.getNodes().split(",");
        Set<HostAndPort> nodes = new HashSet<>();
        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
        }
        return new JedisCluster(nodes, redisProperties.getCommandTimeout());
    }

    @Bean(value = "redisTemplate")
    public RedisTemplate redisTemplateFactory() {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());

        //指定具体序列化方式  不过这种方式不是很好,一个系统中可能对应值的类型不一样,如果全部使用StringRedisSerializer 序列化
        //会照成其他类型报错,所以还是推荐使用第一种,直接指定泛型的类型,spring 会根据指定类型序列化。
        //redisTemplate.setKeySerializer( new StringRedisSerializer());
        //redisTemplate.setValueSerializer(new StringRedisSerializer());
        //redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }


    /**
     * redisCluster配置
     *
     * @return
     */
    @Bean
    public RedisClusterConfiguration redisClusterConfiguration() {
        Map<String, Object> source = new HashMap<>();
        source.put("spring.redis.cluster.nodes", redisProperties.getNodes());
        source.put("spring.redis.cluster.timeout", redisProperties.getCommandTimeout());
        return new RedisClusterConfiguration(new MapPropertySource("RedisClusterConfiguration", source));
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisProperties.getMaxTotal());
        jedisPoolConfig.setMaxIdle(redisProperties.getMaxIdle());
        jedisPoolConfig.setNumTestsPerEvictionRun(redisProperties.getNumTestsPerEvictionRun());
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(redisProperties.getTimeBetweenEvictionRunsMillis());
        jedisPoolConfig.setMinEvictableIdleTimeMillis(redisProperties.getMinEvictableIdleTimeMillis());
        jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(redisProperties.getSoftMinEvictableIdleTimeMillis());
        jedisPoolConfig.setMaxWaitMillis(redisProperties.getMaxWaitMillis());
        jedisPoolConfig.setTestOnBorrow(redisProperties.getTestOnBorrow());
        jedisPoolConfig.setTestWhileIdle(redisProperties.getTestWhileIdle());
        jedisPoolConfig.setBlockWhenExhausted(redisProperties.getBlockWhenExhausted());
        return jedisPoolConfig;

    }


    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(redisClusterConfiguration(), jedisPoolConfig());
    }

}
