package com.wanjun.canalsync.queue;

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


    public TaskExecutorThread(KMQueueManager kmQueueManager, TaskQueue taskQueue) {
        this.kmQueueManager = kmQueueManager;
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        logger.info("Task执行器运行中,队列名称:{}", taskQueue.getName());
        while (running) {
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
