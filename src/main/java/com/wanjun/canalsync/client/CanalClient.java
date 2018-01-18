package com.wanjun.canalsync.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-12
 */
//@Component
public class CanalClient implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(CanalClient.class);
    private CanalConnector canalConnector;

    @Value("${canal.zkServers}")
    private String zkServers;//zookeeper 地址
    @Value("${canal.destination}")
    private String destination;

    @Bean
    public CanalConnector getCanalConnector() {
        // 基于zookeeper动态获取canal server的地址，建立链接，其中一台server发生crash，可以支持failover
        canalConnector = CanalConnectors.newClusterConnector(zkServers, destination, "", "");
        canalConnector.connect();
        // 指定filter，格式 {database}.{table}，这里不做过滤，过滤操作留给用户
        canalConnector.subscribe();
        // 回滚寻找上次中断的位置
        canalConnector.rollback();
        logger.info("canal客户端启动成功");
        return canalConnector;
    }

    @Override
    public void destroy() throws Exception {
        if (canalConnector != null) {
            canalConnector.disconnect();
        }
    }
}
