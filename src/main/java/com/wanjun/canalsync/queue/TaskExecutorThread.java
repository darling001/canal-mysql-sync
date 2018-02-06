package com.wanjun.canalsync.queue;

import com.wanjun.canalsync.client.ZKMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-31
 */
public class TaskExecutorThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(TaskExecutorThread.class);

    private KMQueueManager kmQueueManager;
    private TaskQueue taskQueue;

    private volatile boolean running = true;

    private ZKMaster zkMaster;


    public TaskExecutorThread(KMQueueManager kmQueueManager, TaskQueue taskQueue,ZKMaster zkMaster) {
        this.kmQueueManager = kmQueueManager;
        this.taskQueue = taskQueue;
        this.zkMaster = zkMaster;
    }

    @Override
    public void run() {
        logger.info("Task执行器运行中,队列名称:{}", taskQueue.getName());
        while (running) {
            //通过ZK实现热备，当服务failover时，自动切换
            if(!zkMaster.isMaster()) {
                continue;
            }
            Task task = taskQueue.popTask();
            // 业务处理放到TaskHandler里
            if (task != null) {
                task.doTask(kmQueueManager, EntryTaskHandler.class);
            }
        }

    }

    public void stopExe() {
        if (!running) {
            return;
        }
        running = false;

        if (this != null) {
            try {
                this.join();
            } catch (InterruptedException e) {
                logger.error("TaskExecutorThread->stop() error", e);
            }
        }

    }
}
