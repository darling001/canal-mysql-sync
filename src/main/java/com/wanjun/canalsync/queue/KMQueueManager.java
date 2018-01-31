package com.wanjun.canalsync.queue;

import com.wanjun.canalsync.exception.NestedException;
import com.wanjun.canalsync.queue.backup.BackupQueue;
import com.wanjun.canalsync.queue.backup.RedisBackupQueue;
import com.wanjun.canalsync.util.Assert;
import com.wanjun.canalsync.util.KMQUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-30
 * <p>
 * 队列管理器
 */
public class KMQueueManager extends KMQueueAdapter {

    private final static Logger logger = LoggerFactory.getLogger(KMQueueManager.class);
    private Map<String, Object> queueMap = new ConcurrentHashMap<>();

    /**
     * 待创建的队列的名称集合
     */
    private List<String> queues;

    /**
     * 任务的存活超时时间。单位：ms
     * <p>
     * 注意，该时间是任务从创建({@code new Task(...)})到销毁的总时间
     * <p>
     * 该值只针对安全队列起作用
     * <p>
     * 不设置默认为 Long.MAX_VALUE
     */
    private long aliveTimeout;

    private KMQueueManager() {

    }


    /**
     * 根据名称获取任务队列
     *
     * @param name 队列名称
     * @return 任务队列
     */
    public TaskQueue getTaskQueue(String name) {
        Object queue = this.queueMap.get(name);
        if (queue != null && queue instanceof TaskQueue) {
            return (TaskQueue) queue;
        }
        return null;
    }


    /**
     * 获取任务存活超时时间。注意，该时间是任务从创建({@code new Task(...)})到销毁的总时间。单位：ms
     *
     * @return
     */
    @Override
    public long getAliveTimeout() {
        return this.aliveTimeout;
    }


    /**
     * 初始化队列
     */
    public void init() {
        // 生成备份队列名称
        backUpQueueName = KMQUtils.genBackUpQueueName(this.queues);

        logger.info("Initializing the queues");

        boolean hasSq = false;

        for (String queue : this.queues) {
            String[] qInfos = queue.trim().split(":");
            String qName = qInfos[0].trim();// 队列名称
            String qMode = null;// 队列模式
            if (qInfos.length == 2) {
                qMode = qInfos[1].trim();
            }

            if (qMode != null && !"".equals(qMode) && !qMode.equals(DEFAULT) && !qMode.equals(SAFE)) {
                throw new NestedException("The current queue mode is invalid, the queue name：" + qName);
            }

            if (!"".equals(qName)) {
                if (!queueMap.containsKey(qName)) {
                    if (qMode != null && qMode.equals(SAFE)) {
                        hasSq = true;// 标记存在安全队列
                    }

                    queueMap.put(qName, new RedisTaskQueue(this, qName, qMode));
                    logger.info("Creating a task queue：" + qName);
                } else {
                    logger.info("The current queue already exists. Do not create the queue name repeatedly：" + qName);
                }
            } else {
                throw new NestedException("The current queue name is empty!");
            }
        }

        // 添加备份队列
        if (hasSq) {
            BackupQueue backupQueue = new RedisBackupQueue(this);
            backupQueue.initQueue();
            queueMap.put(backUpQueueName, backupQueue);
            logger.info("Initializing backup queue");
        }
    }

    public static class Builder {


        /**
         * redis连接
         */
        private RedisTemplate<String, String> redisTemplate = null;

        /**
         * 待创建的队列的名称集合
         */
        private List<String> queues;


        /**
         * 任务的存活超时时间。注意，该时间是任务从创建({@code new Task(...)})到销毁的总时间。单位：ms
         * <p>
         * 该值只针对安全队列起作用
         * <p>
         * 不设置默认为 Long.MAX_VALUE
         */
        private long aliveTimeout;

        public Builder(RedisTemplate<String, String> redisTemplate, String... queues) {
            Assert.notNull(redisTemplate, "redisTemplate can't null");
            this.aliveTimeout = Long.MAX_VALUE;
            this.redisTemplate = redisTemplate;
            this.queues = Arrays.asList(queues);
        }

        public Builder setAliveTimeout(long aliveTimeout) {
            Assert.greaterThanEquals(aliveTimeout, 0, "Param aliveTimeout is negative");
            if (aliveTimeout == 0) {
                aliveTimeout = Long.MAX_VALUE;
            }
            this.aliveTimeout = aliveTimeout;
            return this;
        }

        public KMQueueManager build() {
            KMQueueManager queueManager = new KMQueueManager();
            queueManager.redisTemplate = this.redisTemplate;
            queueManager.queues = this.queues;
            queueManager.aliveTimeout = this.aliveTimeout;
            return queueManager;
        }


    }
}
