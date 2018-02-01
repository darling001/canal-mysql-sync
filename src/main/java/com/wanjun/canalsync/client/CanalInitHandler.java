package com.wanjun.canalsync.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.google.common.collect.Lists;
import com.sun.xml.internal.bind.v2.TODO;
import com.wanjun.canalsync.client.config.CanalProperties;
import com.wanjun.canalsync.queue.KMQueueManager;
import com.wanjun.canalsync.queue.TaskExecutorThread;
import com.wanjun.canalsync.queue.TaskQueue;
import com.wanjun.canalsync.queue.config.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-18
 */
@Component
public class CanalInitHandler implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(CanalInitHandler.class);
    @Autowired
    private CanalProperties canalProperties;
    private ApplicationContext applicationContext;

    private RedisTemplate<String, String> redisTemplate;
    //Redis队列管理器,用于数据处理异常
    private KMQueueManager kmQueueManager;
    //队列
    private TaskQueue taskQueue = null;

    public void initCanalStart() {
        redisTemplate = applicationContext.getBean("redisTemplate", RedisTemplate.class);
        kmQueueManager = new KMQueueManager.Builder(redisTemplate, "worker2_queue:safe")
                .setAliveTimeout(Constant.ALIVE_TIMEOUT)
                .build();
        //初始化队列
        kmQueueManager.init();
        // 1.获取队列
        taskQueue = kmQueueManager.getTaskQueue("worker2_queue");

        List<String> destinations = canalProperties.getDestinations();
        final List<MultiThreadCanalClient> canalClientList = Lists.newArrayList();
        if (destinations != null && destinations.size() > 0) {
            for (String destination : destinations) {
                // 基于zookeeper动态获取canal server的地址，建立链接，其中一台server发生crash，可以支持failover
                CanalConnector connector = CanalConnectors.newClusterConnector(canalProperties.getZkServers(), destination, "", "");
                MultiThreadCanalClient client = new MultiThreadCanalClient(destination, connector, applicationContext, taskQueue);
                canalClientList.add(client);
                client.start();
            }
        }
        TaskExecutorThread taskExecutorThread = new TaskExecutorThread(kmQueueManager,taskQueue);
        taskExecutorThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    logger.info("## stop the canal client");
                    //停止CanalClient线程
                    for (MultiThreadCanalClient canalClient : canalClientList) {
                        canalClient.stop();
                    }
                    logger.info("## stop the task executor thread");
                    taskExecutorThread.stopExe();
                } catch (Throwable e) {
                    logger.warn("##something goes wrong when stopping canal:", e);
                } finally {
                    logger.info("## canal client is down.");
                    logger.info("## task executor thread is down.");
                }
            }

        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
