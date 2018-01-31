package com.wanjun.canalsync;

import com.wanjun.canalsync.queue.Task;
import com.wanjun.canalsync.queue.TaskQueue;
import com.wanjun.canalsync.queue.extension.Pipeline;
import com.wanjun.canalsync.util.JSONUtil;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-31
 */
public class MyPipeline implements Pipeline {
    @Override
    public void process(TaskQueue taskQueue, Task task) {
        System.out.println("Task is timeout，task - " + JSONUtil.toJson(task));
    }
}
