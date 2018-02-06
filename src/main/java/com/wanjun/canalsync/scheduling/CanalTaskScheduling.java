package com.wanjun.canalsync.scheduling;

import com.wanjun.canalsync.client.ZKMaster;
import com.wanjun.canalsync.queue.CanalTaskPipeline;
import com.wanjun.canalsync.queue.config.Constant;
import com.wanjun.canalsync.queue.config.TaskConfig;
import com.wanjun.canalsync.queue.extension.BackupQueueMonitor;
import com.wanjun.canalsync.util.DateUtils;
import com.wanjun.canalsync.util.KMQUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-02-02
 */
@Component
public class CanalTaskScheduling implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CanalTaskScheduling.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private TaskConfig taskConfig;

    private BackupQueueMonitor backupQueueMonitor;

    @Autowired
    private ZKMaster zkMaster;

    @PostConstruct
    private void init() {
        CanalTaskPipeline pipeline = new CanalTaskPipeline();

        // 根据任务队列的名称构造备份队列的名称，注意：这里的任务队列参数一定要和KMQueueManager构造时传入的一一对应。
        String backUpQueueName = KMQUtils.genBackUpQueueName(taskConfig.getQueues());

        // 构造Monitor监听器
        backupQueueMonitor = new BackupQueueMonitor.Builder(redisTemplate, backUpQueueName)
                .setAliveTimeout(taskConfig.getAliveTimeout())
                .setProtectedTimeout(taskConfig.getProtectedTimeout())
                .setRetryTimes(taskConfig.getRetryTimes())
                .registerAliveDetectHandler(null)
                .setPipeline(pipeline).build();
    }

    @Override
    @Scheduled(fixedDelay = 10000)
    public void run() {
        //通过ZK实现热备，当服务failover时，自动切换
        if (zkMaster.isMaster() && backupQueueMonitor != null) {
            backupQueueMonitor.monitor();
        }
    }
}
