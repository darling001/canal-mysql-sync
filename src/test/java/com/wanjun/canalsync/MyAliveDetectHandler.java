package com.wanjun.canalsync;

import com.wanjun.canalsync.queue.Task;
import com.wanjun.canalsync.queue.extension.AliveDetectHandler;
import com.wanjun.canalsync.queue.extension.BackupQueueMonitor;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-31
 */
public class MyAliveDetectHandler implements AliveDetectHandler {



    @Override
    public boolean check(BackupQueueMonitor monitor, Task task) {
        RedisTemplate<String,String> redisTemplate = monitor.getResource();
       /* String value = redisTemplate.(task.getId() + ALIVE_KEY_SUFFIX);
        return value != null && !"".equals(value.trim());*/
        return false;
    }
}
