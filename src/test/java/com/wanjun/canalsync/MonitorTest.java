package com.wanjun.canalsync;

import com.wanjun.canalsync.queue.config.Constant;
import com.wanjun.canalsync.queue.extension.BackupQueueMonitor;
import com.wanjun.canalsync.util.KMQUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-31
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MonitorTest {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private BackupQueueMonitor backupQueueMonitor;


    @PostConstruct
    private void init() {

    }

    @Test
    public void monitorTaskTest() {

        // 健康检测
        MyAliveDetectHandler detectHandler = new MyAliveDetectHandler();
        // 任务彻底失败后的处理，需要实现Pipeline接口，自行实现处理逻辑
        MyPipeline pipeline = new MyPipeline();
        // 根据任务队列的名称构造备份队列的名称，注意：这里的任务队列参数一定要和KMQueueManager构造时传入的一一对应。
        String backUpQueueName = KMQUtils.genBackUpQueueName( "worker2_queue:safe");
        // 构造Monitor监听器
         backupQueueMonitor = new BackupQueueMonitor.Builder(redisTemplate, backUpQueueName)
                .setAliveTimeout(Constant.ALIVE_TIMEOUT)
                .setProtectedTimeout(Constant.PROTECTED_TIMEOUT)
                .setRetryTimes(Constant.RETRY_TIMES)
                .registerAliveDetectHandler(detectHandler)
                .setPipeline(pipeline).build();
        // 执行监听
        backupQueueMonitor.monitor();
    }
}
