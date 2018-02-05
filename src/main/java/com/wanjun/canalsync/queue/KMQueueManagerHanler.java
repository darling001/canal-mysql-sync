package com.wanjun.canalsync.queue;

import com.wanjun.canalsync.queue.KMQueueManager;
import com.wanjun.canalsync.queue.TaskExecutorThread;
import com.wanjun.canalsync.queue.TaskQueue;
import com.wanjun.canalsync.queue.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-02-05
 */
@Component
public class KMQueueManagerHanler {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //Redis队列管理器,用于数据处理异常
    private KMQueueManager kmQueueManager;
    //队列
    private TaskQueue taskQueue = null;


    @PostConstruct
    private void init() {
        kmQueueManager = new KMQueueManager.Builder(redisTemplate, "worker2_queue:safe")
                .setAliveTimeout(Constant.ALIVE_TIMEOUT)
                .build();
        //初始化队列
        kmQueueManager.init();
        // 1.获取队列
        taskQueue = kmQueueManager.getTaskQueue("worker2_queue");

        TaskExecutorThread taskExecutorThread = new TaskExecutorThread(kmQueueManager,taskQueue);
        taskExecutorThread.start();
    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }
}
