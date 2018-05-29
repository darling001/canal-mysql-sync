package com.wanjun.canalsync.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-19
 */
@ConfigurationProperties(prefix = "spring.redis.cluster")
public class RedisProperties {

    private String nodes;

    private String password;

    private Integer commandTimeout;
    //最大连接数
    private Integer maxTotal ;
    //最大空闲事件
    private Integer maxIdle;
    //每次最大连接数
    private Integer numTestsPerEvictionRun;
    //释放扫描的扫描间隔
    private Long timeBetweenEvictionRunsMillis;
    //连接的最小空闲时间
    private Long minEvictableIdleTimeMillis;
    //连接控歘按时间多久后释放，当空闲时间>该值且空闲连接>最大空闲连接数时直接释放
    private Long softMinEvictableIdleTimeMillis;
    //获得链接时的最大等待毫秒数，小于0：阻塞不确定时间，默认-1
    private Long maxWaitMillis;
    //在获得链接的时候检查有效性，默认false
    private Boolean testOnBorrow;
    //在空闲时检查有效性，默认false
    private Boolean testWhileIdle;
    // 连接耗尽时是否阻塞，false报异常，true阻塞超时 默认：true
    private Boolean blockWhenExhausted;

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public Integer getCommandTimeout() {
        return commandTimeout;
    }

    public void setCommandTimeout(Integer commandTimeout) {
        this.commandTimeout = commandTimeout;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    public void setNumTestsPerEvictionRun(Integer numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public Long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(Long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public Long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(Long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public Long getSoftMinEvictableIdleTimeMillis() {
        return softMinEvictableIdleTimeMillis;
    }

    public void setSoftMinEvictableIdleTimeMillis(Long softMinEvictableIdleTimeMillis) {
        this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(Long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public Boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(Boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public Boolean getBlockWhenExhausted() {
        return blockWhenExhausted;
    }

    public void setBlockWhenExhausted(Boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }
}
