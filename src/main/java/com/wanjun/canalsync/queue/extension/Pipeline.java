package com.wanjun.canalsync.queue.extension;

import com.wanjun.canalsync.queue.Task;
import com.wanjun.canalsync.queue.TaskQueue;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-30
 *
 * 失败任务（重试三次失败）的处理
 */
public interface Pipeline {


    /**
     * 失败任务的处理
     * @param taskQueue 任务所属队列
     * @param task 任务
     */
    public void process(TaskQueue taskQueue, Task task) ;
}
