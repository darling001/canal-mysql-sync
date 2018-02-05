package com.wanjun.canalsync.queue;

import com.wanjun.canalsync.queue.extension.Pipeline;
import com.wanjun.canalsync.util.JSONUtil;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-02-05
 */
public class CanalTaskPipeline implements Pipeline {
    @Override
    public void process(TaskQueue taskQueue, Task task) {
        System.out.println("Task is timeout，task - " + JSONUtil.toJson(task));
    }
}
