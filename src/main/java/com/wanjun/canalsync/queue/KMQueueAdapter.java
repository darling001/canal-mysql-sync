package com.wanjun.canalsync.queue;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-30
 */
public abstract class KMQueueAdapter {

    // 队列模式：DEFAULT - 简单队列，SAFE - 安全队列
    public static final String DEFAULT = "default";
    public static final String SAFE = "safe";
    public static String BACK_UP_QUEUE_PREFIX = "back_up_queue_";// 备份队列名称前缀

    /**
     * 备份队列名称
     */
    protected String backUpQueueName;
    /**
     * redis连接
     */
    protected RedisTemplate<String, String> redisTemplate;


    /**
     * 获取备份队列的名称
     *
     * @return 备份队列的名称
     */
    public String getBackUpQueueName() {
        return this.backUpQueueName;
    }

    public abstract long getAliveTimeout();


    public synchronized RedisTemplate<String, String> getResource() {
        Assert.notNull(redisTemplate,"getResource failed");
        return redisTemplate;
    }

}
