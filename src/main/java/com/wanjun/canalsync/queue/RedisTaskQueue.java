package com.wanjun.canalsync.queue;

import com.wanjun.canalsync.queue.config.Constant;
import com.wanjun.canalsync.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-30
 * 任务队列Redis实现<br/>
 */
public class RedisTaskQueue extends TaskQueue {


    private final static Logger logger = LoggerFactory.getLogger(RedisTaskQueue.class);

    private static final int REDIS_DB_IDX = 0;

    /**
     * 任务队列名称
     */
    private final String name;

    /**
     * 队列模式：DEFAULT - 简单队列，SAFE - 安全队列
     */
    private final String mode;

    /**
     * 队列管理器
     */
    private KMQueueAdapter kmQueueAdapter;

    /**
     * 构造函数
     *
     * @param kmQueueAdapter 队列管理器
     * @param name           任务队列名称
     * @param mode           队列模式
     */
    public RedisTaskQueue(KMQueueAdapter kmQueueAdapter, String name, String mode) {
        this.kmQueueAdapter = kmQueueAdapter;
        if (mode == null || "".equals(mode)) {
            mode = KMQueueManager.DEFAULT;
        }
        this.name = name;
        this.mode = mode;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getMode() {
        return this.mode;
    }

    /**
     * 向任务队列中插入任务
     * <p>
     * 如果插入任务成功，则返回该任务，失败，则返回null
     * <p>
     * 特别的，对于唯一性任务，如果该任务在队列已经存在，则返回null
     *
     * @param task 队列任务
     * @return 插入的任务
     */
    @Override
    public Task pushTask(Task task) {
        RedisTemplate<String, String> redisTemplate = null;
        try {
            redisTemplate = kmQueueAdapter.getResource();

            // 队列任务唯一性校验
            if (this.getMode().equals(KMQueueAdapter.SAFE) && task.isUnique()) {// 唯一性任务

                // Integer reply, specifically: 1 if the new element was added 0 if the element was already a member of the set
                Long isExist = redisTemplate.opsForSet().add(this.name + Constant.UNIQUE_SUFFIX, task.getId());
                if (isExist == 0) {
                    return null;
                }
            }

            String taskJson = JSONUtil.toJson(task);
            redisTemplate.opsForList().leftPush(this.name, taskJson);
            return task;
        } catch (Throwable e) {
            logger.error("RedisTaskQueue->pushTask error! ", e);
        }
        return null;
    }

    @Override
    public void pushTaskToHeader(Task task) {

        RedisTemplate<String, String> redisTemplate = null;
        try {
            redisTemplate = kmQueueAdapter.getResource();
            String taskJson = JSONUtil.toJson(task);
            redisTemplate.opsForList().rightPush(this.name, taskJson);
        } catch (Throwable e) {
            logger.error("RedisTaskQueue->pushTaskToHeader error! ", e);
        }

    }

    /**
     * 1.采用阻塞队列，以阻塞的方式(brpop)获取任务队列中的任务；<br>
     * 2.判断任务存活时间是否超时（对应的是大于`aliveTimeout`）；<br>
     * 3.更新任务的执行时间戳，放入备份队列的队首；<br>
     * <p>
     * 任务状态不变，默认值为`normal`
     *
     * @return
     */
    @Override
    public Task popTask() {
        RedisTemplate<String, String> redisTemplate = null;
        Task task = null;
        try {
            redisTemplate = kmQueueAdapter.getResource();
            // 判断队列模式
            if (KMQueueManager.SAFE.equals(getMode())) {// 安全队列
                // 1.采用阻塞队列，获取任务队列中的任务(brpop)；
                String result = redisTemplate.opsForList().rightPop(getName(), 0, TimeUnit.SECONDS);
                task = JSONUtil.toBean(result, Task.class);

                // 2.判断任务存活时间是否超时（对应的是大于`aliveTimeout`）；
                Task.TaskStatus status = task.getTaskStatus();// 获取任务状态
                long taskGenTimeMillis = status.getGenTimestamp();// 任务生成的时间戳
                long currentTimeMillis = System.currentTimeMillis();// 当前时间戳
                long intervalTimeMillis = currentTimeMillis - taskGenTimeMillis;// 任务的存活时间
                if (intervalTimeMillis <= kmQueueAdapter.getAliveTimeout()) {// 如果大于存活超时时间，则不再执行
                    // 3.更新任务的执行时间戳，放入备份队列的队首；
                    task.getTaskStatus().setExcTimestamp(System.currentTimeMillis());// 更新任务的执行时间戳
                    redisTemplate.opsForList().leftPush(kmQueueAdapter.getBackUpQueueName(), JSONUtil.toJson(task));
                }
            } else if (KMQueueManager.DEFAULT.equals(getMode())) {// 简单队列
                String result = redisTemplate.opsForList().rightPop(getName(), 0, TimeUnit.SECONDS);
                String taskJson = result;
                task = JSONUtil.toBean(taskJson, Task.class);
            }
        } catch (Throwable e) {
            logger.error("RedisTaskQueue->popTask error! ", e);
        }
        return task;
    }

    @Override
    public void finishTask(Task task) {
        if (KMQueueManager.SAFE.equals(getMode())) {
            // 安全队列
            RedisTemplate<String, String> redisTemplate = null;
            try {
                redisTemplate = kmQueueAdapter.getResource();
                String taskJson = JSONUtil.toJson(task);

                // 删除备份队列中的任务
                redisTemplate.opsForList().remove(kmQueueAdapter.getBackUpQueueName(), 0, taskJson);

                // 删除该任务的存在标记
                redisTemplate.opsForSet().remove(this.name + Constant.UNIQUE_SUFFIX, task.getId());
            } catch (Throwable e) {
                logger.error("RedisTaskQueue->finishTask error! ", e);
            }
        }
    }
}
