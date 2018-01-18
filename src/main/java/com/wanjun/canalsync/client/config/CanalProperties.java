package com.wanjun.canalsync.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-18
 */
@ConfigurationProperties(prefix = "canal")
public class CanalProperties {

    private String zkServers;//zookeeper 地址
    private List<String> destinations;//监听instance列表

    public CanalProperties() {
    }

    public String getZkServers() {
        return zkServers;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }

    public List<String> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<String> destinations) {
        this.destinations = destinations;
    }


}