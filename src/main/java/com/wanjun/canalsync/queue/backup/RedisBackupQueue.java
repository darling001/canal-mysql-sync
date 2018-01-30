package com.wanjun.canalsync.queue.backup;

import com.alibaba.fastjson.JSON;
import com.wanjun.canalsync.queue.KMQueueAdapter;
import com.wanjun.canalsync.queue.Task;
import com.wanjun.canalsync.queue.config.Constant;
import com.wanjun.canalsync.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-30
 */
public class RedisBackupQueue extends BackupQueue {


    private final static Logger logger = LoggerFactory.getLogger(RedisBackupQueue.class);

    private static final int REDIS_DB_IDX = 0;
    public static final String MARKER = "marker";

    /**
     * 备份队列的名称
     */
    private final String name;

    /**
     * 队列管理器
     */
    private KMQueueAdapter kmQueueAdapter;

    public RedisBackupQueue(KMQueueAdapter kmQueueAdapter) {
        this.kmQueueAdapter = kmQueueAdapter;
        this.name = kmQueueAdapter.getBackUpQueueName();
    }

    /**
     * 初始化备份队列，添加备份队列循环标记
     */
    @Override
    public void initQueue() {
        RedisTemplate<String, String> redisTemplate = null;
        try {
            redisTemplate = kmQueueAdapter.getResource();

            // 创建备份队列循环标记
            Task.TaskStatus state = new Task.TaskStatus();
            Task task = new Task(this.name, null, RedisBackupQueue.MARKER, null, state);

            String taskJson = JSONUtil.toJson(task);

            // 注意分布式问题，防止备份队列添加多个循环标记
            // 这里使用redis的事务&乐观锁
            //redisTemplate.watch(this.name);// 监视当前队列 ，Cluster Model build支持
            boolean isExists = redisTemplate.hasKey(this.name);// 查询当前队列是否存在

            List<String> backQueueData = redisTemplate.opsForList().range(this.name, 0, -1);
            logger.info("========================================");
            logger.info("Backup queue already exists! Queue name：" + this.name);
            logger.info("Backup queue[" + this.name + "]data:");
            backQueueData.forEach(logger::info);
            logger.info("========================================");

            //redisTemplate.multi();// 开启事务 Cluster Model build支持
            if (!isExists) {// 只有当前队列不存在，才执行lpush
                redisTemplate.opsForList().leftPush(this.name, taskJson);
                //List<Object> results = redisTemplate.exec();
                logger.info("Thread[" + Thread.currentThread().getName() + "] - (Add backup queue loop tag) Transaction execution result：" + ((this.getName() != null ) ? this.getName() : "Fail"));
            }
        } catch (Throwable e) {
            logger.error("RedisBackupQueue->initQueue error! ", e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Task popTask() {
        RedisTemplate<String, String> redisTemplate = null;
        Task task = null;
        try {
            redisTemplate = kmQueueAdapter.getResource();

            /**
             * 循环取出备份队列的一个元素：从队尾取出元素，并将其放置队首
             */
            String taskValue = redisTemplate.opsForList().rightPopAndLeftPush(this.name, this.name);
            task = JSON.parseObject(taskValue, Task.class);
        } catch (Throwable e) {
            logger.error("RedisBackupQueue->popTask error! ", e);
        }
        return task;
    }

    @Override
    public void finishTask(Task task) {
        RedisTemplate<String, String> redisTemplate = null;
        try {
            redisTemplate = kmQueueAdapter.getResource();
            String taskJson = JSON.toJSONString(task);

            // 删除备份队列中的任务
            redisTemplate.opsForList().remove(this.name, 0, taskJson);

            // 删除该任务的存在标记
            redisTemplate.opsForSet().remove(task.getQueue() + Constant.UNIQUE_SUFFIX, task.getId());
        } catch (Throwable e) {
            logger.error("RedisBackupQueue->finishTask error! ", e);
        }
    }

}
