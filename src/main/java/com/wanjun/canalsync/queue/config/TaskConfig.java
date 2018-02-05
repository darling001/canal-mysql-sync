package com.wanjun.canalsync.queue.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-02-05
 */
@ConfigurationProperties(prefix = "task")
public class TaskConfig {
    /**
     * 所有队列字符串
     */
    private String queues;
    /**
     * 待使用队列名称
     */
    private String usedQueue;

    /**
     用于队列任务唯一性标记，redis set key
      */
    private String uniqueSuffix;
    /**
     * 标记任务为正常执行状态
     */
    private String normal;

    /**
     * 标记任务为重试执行状态
     */
    private String retry;

    /**
     * 任务的存活时间。单位：ms
     * <p>
     * 注意，该时间是任务从创建({@code new Task(...)})到销毁的总时间
     * <p>
     * 该值只针对安全队列起作用
     */
    private long aliveTimeout;

    /**
     * 任务执行的超时时间（一次执行）。单位：ms
     * <p>
     * 该值只针对安全队列起作用
     * <p>
     * TODO 后续会加入心跳健康检测
     */
    private long protectedTimeout;

    /**
     * 任务重试次数
     */
    private int retryTimes;


    public String getQueues() {
        return queues;
    }

    public void setQueues(String queues) {
        this.queues = queues;
    }

    public String getUsedQueue() {
        return usedQueue;
    }

    public void setUsedQueue(String usedQueue) {
        this.usedQueue = usedQueue;
    }

    public String getUniqueSuffix() {
        return uniqueSuffix;
    }

    public void setUniqueSuffix(String uniqueSuffix) {
        this.uniqueSuffix = uniqueSuffix;
    }

    public String getNormal() {
        return normal;
    }

    public void setNormal(String normal) {
        this.normal = normal;
    }

    public String getRetry() {
        return retry;
    }

    public void setRetry(String retry) {
        this.retry = retry;
    }

    public long getAliveTimeout() {
        return aliveTimeout;
    }

    public void setAliveTimeout(long aliveTimeout) {
        this.aliveTimeout = aliveTimeout;
    }

    public long getProtectedTimeout() {
        return protectedTimeout;
    }

    public void setProtectedTimeout(long protectedTimeout) {
        this.protectedTimeout = protectedTimeout;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }
}
