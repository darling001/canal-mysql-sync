package com.wanjun.canalsync.queue;

import com.wanjun.canalsync.client.ZKMaster;
import com.wanjun.canalsync.client.config.CanalProperties;
import com.wanjun.canalsync.queue.config.TaskConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-02-05
 */
@Component
public class KMQueueManagerHanler {
    private static final Logger logger = LoggerFactory.getLogger(KMQueueManagerHanler.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private TaskConfig taskConfig;

    //Redis队列管理器,用于数据处理异常
    private KMQueueManager kmQueueManager;
    //队列
    private TaskQueue taskQueue = null;

    //通过ZK实现热备，当服务failover时，自动切换
    @Resource
    private ZKMaster zkMaster;

    @PostConstruct
    private void init() {
        kmQueueManager = new KMQueueManager.Builder(redisTemplate, taskConfig.getQueues())
                .setAliveTimeout(taskConfig.getAliveTimeout())
                .build();
        //初始化队列管理器
        kmQueueManager.init();
        // 1.获取队列
        taskQueue = kmQueueManager.getTaskQueue(taskConfig.getUsedQueue());
        TaskExecutorThread taskExecutorThread = new TaskExecutorThread(kmQueueManager, taskQueue,zkMaster);
        taskExecutorThread.start();

    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }
}
